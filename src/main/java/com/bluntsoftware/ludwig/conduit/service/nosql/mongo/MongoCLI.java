package com.bluntsoftware.ludwig.conduit.service.nosql.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


import static org.apache.commons.lang3.Validate.notEmpty;

public class MongoCLI {
    private final Logger log = LoggerFactory.getLogger(MongoCLI.class);
    private String mongoHome = "C:\\Program Files\\MongoDB\\Server\\3.6";
    private String mongodumpBin = "mongodump";
    private String mongorestoreBin = "mongorestore";
    private Boolean forceMongoHome = false;

    public static void main(String[] args) {
        MongoCLI mongoCLI = new MongoCLI();
        try {
            File backupFile = mongoCLI.backupDatabase("off");
            System.out.print(backupFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File backupDatabase(String databaseName) throws Exception {
        String backupLocalZipFile = backup(databaseName,null,databaseName + ".zip");
        return new File(backupLocalZipFile);
    }

    public void restoreDatabase(String databaseName) throws Exception {
         restore(databaseName,null,databaseName + ".zip");
    }

    private Boolean isWindows(){
        String OS = System.getProperty("os.name").toLowerCase();
        return (OS.contains("win"));
    }

    private String getMongoCommandAbsolutePath(String command) {
        if (isWindows()) {
            return String.format("%s%s%s%s%s.exe",mongoHome, File.separator, "bin", File.separator, command);
        }
        if (forceMongoHome) { // tarball custom install directory use case
            return String.format("%s%s%s%s%s",mongoHome, File.separator, "bin", File.separator, command);
        }
        // suppose binary are in the path of the host
        return command;
    }
    private String getMongoDumpBinAbsolutePath() {
        return getMongoCommandAbsolutePath(mongodumpBin);
    }
    private String getMongoRestoreBinAbsolutePath() {
        return getMongoCommandAbsolutePath(mongorestoreBin);
    }

    private File getBackupFolder() {
        File userAppFolder = new File(System.getProperty("java.io.tmpdir"),"catwalk");
        File backupFolder = new File(userAppFolder,"mongoBackup");
        backupFolder.mkdirs();
        return backupFolder;
    }

    public synchronized String backup(String dbName,String collection,String backupFile) throws Exception {
        notEmpty(dbName, "database name is required");
        String mongodumpCmd = getMongoDumpBinAbsolutePath();
        String finalBackupName = new File(getBackupFolder(),backupFile).getAbsolutePath();
        return _backupCmd(mongodumpCmd,dbName, collection, finalBackupName);
    }

    public synchronized void restore(String dbName,String collection,String restoreFile) throws Exception {
        notEmpty(dbName, "database name is required");
        String mongoRestoreCmd = getMongoRestoreBinAbsolutePath();
        String finalRestoreName = new File(getBackupFolder(),restoreFile).getAbsolutePath();
        _restoreCmd(mongoRestoreCmd,dbName,collection,finalRestoreName);
    }

    private String _backupCmd(String mongodumpCmd, String dbName, String collectionName, String finalBackupName) throws Exception {

        List<String> cmdArgs;

        String archiveOption = String.format("/archive:%s", finalBackupName);
        if (collectionName != null) {
            cmdArgs = Arrays.asList(mongodumpCmd, archiveOption, "--gzip", "--db", dbName,"--collection", collectionName);
        } else {
            cmdArgs = Arrays.asList(mongodumpCmd, archiveOption, "--gzip", "--db", dbName);
        }
        try {
            return _hostBackupProcessCommand("mongodump", cmdArgs, finalBackupName);
        } catch (Throwable t) {
            String errMsg = String.format("Error during the backup of '%s' : %s", dbName, t.getMessage());
            log.error(errMsg, t);
            throw new Exception(errMsg);
        }
    }
    public String stringArrayToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list)
        {
            sb.append(s);
            sb.append(" ");
        }

        return sb.toString();
    }
    private String _hostBackupProcessCommand(String processName, List<String> cmdArgs, String outFileName)
            throws IOException, InterruptedException, Exception {
        ProcessBuilder builder = new ProcessBuilder(cmdArgs);

        log.info("{} : {}", processName, stringArrayToString(cmdArgs));
        Process process = builder.start();
        if ("mongodump".equals(processName)) {
            log.info("please notice that mongodump reports all dump action into stderr (not only errors)");
        }

        process.waitFor();
        int exitValue = process.exitValue();
        if (exitValue == 0) {
            log.info("created : {}", outFileName);
            return outFileName;
        }


        throw new Exception("Backup Failed with code " + exitValue);
    }
    private void _restoreCmd(String mongoRestoreCmd, String dbName, String collection, String backupFile) throws Exception {
        log.info("restore cmd:{}, db:{}, collection:{}, backupFile:{}",
                mongoRestoreCmd, dbName, collection != null ? collection : "(not set)", backupFile);

        String archiveOption = String.format("/archive:%s", backupFile);
        List<String> cmdArgs;
        if (collection != null) {
            cmdArgs = Arrays.asList(mongoRestoreCmd, archiveOption, "--gzip", "-v", "--drop", "--db", dbName,"--collection", collection);
        } else {
            cmdArgs = Arrays.asList(mongoRestoreCmd, archiveOption, "--gzip", "-v", "--drop", "--db", dbName);
        }
        try {
            _hostRestoreProcessCommand("mongorestore", cmdArgs,  backupFile);
        } catch (Throwable t) {
            String errMsg = String.format("Error during the restore of '%s' : %s", dbName, t.getMessage());
            log.error(errMsg, t);
            throw new Exception(errMsg);
        }
    }

    private void _hostRestoreProcessCommand(String processName, List<String> cmdArgs, String backupFile)
            throws Exception, IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmdArgs);

        log.info("{} : {}", processName, stringArrayToString(cmdArgs));
        Process process = builder.start();
        if ("mongorestore".equals(processName)) {
            log.info("please notice that mongorestore reports all restore action into stderr (not only errors)");
        }

        process.waitFor();
        int exitValue = process.exitValue();
        if (exitValue == 0) {
            log.info("{} with success : {}", processName, backupFile);
            return;
        }
        throw new Exception("Restore Failed with code " + exitValue);
    }
    public Boolean getForceMongoHome() {
        return forceMongoHome;
    }

    public void setForceMongoHome(Boolean forceMongoHome) {
        this.forceMongoHome = forceMongoHome;
    }

}


