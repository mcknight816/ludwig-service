package com.bluntsoftware.ludwig.conduit.activities.python.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PythonRequest implements EntitySchema {

    // One of these must be provided
    private String code;          // Python code to execute (piped to stdin)
    private String scriptPath;    // Path to a .py file to execute

    // Optional invocation options
    private List<String> args;           // Args passed after "-" or script path
    private String interpreter;          // Defaults to "python3"
    private String workingDirectory;     // Working dir for the process
    private Map<String, String> env;     // Extra environment variables
    private String stdin;                // Data to pass to Python stdin
    private Long timeoutSeconds;         // Defaults to 60
    private Charset charset;             // Defaults to UTF-8

    @Override
    public JsonSchema getJsonSchema() {
        Map<String, Property> props = new HashMap<>();
        props.put("code", StringProperty.builder()
                .title("Python Code")
                .format(PropertyFormat.PYTHON)
                .defaultValue(String.join("\n",
                                "def add(a,b):",
                                "    return a+b",
                                "print(add(2,3))"
                        )
                ).build());
        return JsonSchema.builder().properties(props).build();
    }
}
