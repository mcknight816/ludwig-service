package com.bluntsoftware.ludwig.conduit.activities.python;

import com.bluntsoftware.ludwig.conduit.activities.TypedActivity;
import com.bluntsoftware.ludwig.conduit.activities.python.domain.PythonRequest;
import com.bluntsoftware.ludwig.conduit.activities.python.domain.PythonResponse;

import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PythonActivity extends TypedActivity<PythonRequest, PythonResponse> {

    public PythonActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository, PythonRequest.class);
    }

    @Override
    public PythonRequest input() {
        return PythonRequest.builder().build();
    }

    @Override
    public PythonResponse output() {
        return PythonResponse.builder().build();
    }

    @Override
    public PythonResponse run(PythonRequest req) throws Exception {
        // Defaults
        String interpreter = Optional.ofNullable(req.getInterpreter()).orElse("python3");
        long timeoutSec = Optional.ofNullable(req.getTimeoutSeconds()).orElse(60L);
        Charset charset = Optional.ofNullable(req.getCharset()).orElse(StandardCharsets.UTF_8);

        List<String> cmd = new ArrayList<>();
        cmd.add(interpreter);

        boolean usingStdin = false;

        if (req.getScriptPath() != null && !req.getScriptPath().isEmpty()) {
            // Run a file with optional args
            cmd.add(req.getScriptPath());
            if (req.getArgs() != null) {
                cmd.addAll(req.getArgs());
            }
        } else if (req.getCode() != null) {
            // Prefer piping code via stdin to avoid escaping issues
            cmd.add("-");
            if (req.getArgs() != null) {
                cmd.addAll(req.getArgs());
            }
            usingStdin = true;
        } else {
            return PythonResponse.builder()
                    .success(false)
                    .exitCode(2)
                    .stderr("PythonActivity: either 'code' or 'scriptPath' must be provided.")
                    .text("")
                    .durationMs(0L)
                    .build();
        }

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false); // keep stderr separate

        // Working directory
        if (req.getWorkingDirectory() != null && !req.getWorkingDirectory().isEmpty()) {
            pb.directory(new File(req.getWorkingDirectory()));
        }

        // Environment
        if (req.getEnv() != null && !req.getEnv().isEmpty()) {
            Map<String, String> env = pb.environment();
            env.putAll(req.getEnv());
        }

        log.info("PythonActivity starting process: {}", String.join(" ", cmd));

        Instant start = Instant.now();
        Process p = pb.start();

        // If sending code or stdin, write it
        if (usingStdin) {
            try (OutputStream os = p.getOutputStream()) {
                os.write(req.getCode().getBytes(charset));
                if (req.getStdin() != null) {
                    os.write(req.getStdin().getBytes(charset));
                }
                os.flush();
            }
        } else if (req.getStdin() != null && !req.getStdin().isEmpty()) {
            try (OutputStream os = p.getOutputStream()) {
                os.write(req.getStdin().getBytes(charset));
                os.flush();
            }
        } else {
            // Close stdin if unused
            p.getOutputStream().close();
        }

        // Read output concurrently
        StringBuilder stdoutBuf = new StringBuilder();
        StringBuilder stderrBuf = new StringBuilder();

        Thread tOut = new Thread(() -> readStream(p.getInputStream(), stdoutBuf, charset));
        Thread tErr = new Thread(() -> readStream(p.getErrorStream(), stderrBuf, charset));
        tOut.start();
        tErr.start();

        boolean finished = p.waitFor(timeoutSec, TimeUnit.SECONDS);
        if (!finished) {
            p.destroyForcibly();
            Duration dur = Duration.between(start, Instant.now());
            return PythonResponse.builder()
                    .success(false)
                    .exitCode(124) // conventionally timeout
                    .stderr("Python process timed out after " + timeoutSec + "s")
                    .text(stdoutBuf.toString())
                    .durationMs(dur.toMillis())
                    .build();
        }

        // Ensure streams fully read
        tOut.join(TimeUnit.SECONDS.toMillis(5));
        tErr.join(TimeUnit.SECONDS.toMillis(5));

        int exit = p.exitValue();
        Duration dur = Duration.between(start, Instant.now());
        boolean success = exit == 0;

        return PythonResponse.builder()
                .success(success)
                .exitCode(exit)
                .text(stdoutBuf.toString())
                .stderr(stderrBuf.toString())
                .durationMs(dur.toMillis())
                .build();
    }

    private static void readStream(InputStream is, StringBuilder target, Charset charset) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(is, charset))) {
            String line;
            while ((line = r.readLine()) != null) {
                target.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            target.append("<<IOError reading stream: ").append(e.getMessage()).append(">>")
                    .append(System.lineSeparator());
        }
    }
}

