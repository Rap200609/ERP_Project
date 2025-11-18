package edu.univ.erp.service.admin;

import edu.univ.erp.domain.DatabaseTarget;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class BackupService {

    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "Rap@2006";

    private static final List<String> MYSQLDUMP_PATHS = Arrays.asList(
            "C:\\Program Files\\MySQL\\MySQL Server 9.4\\bin\\mysqldump.exe",
            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe",
            "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysqldump.exe",
            "C:\\xampp\\mysql\\bin\\mysqldump.exe",
            "mysqldump.exe"
    );

    private static final List<String> MYSQL_PATHS = Arrays.asList(
            "C:\\Program Files\\MySQL\\MySQL Server 9.4\\bin\\mysql.exe",
            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe",
            "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysql.exe",
            "C:\\xampp\\mysql\\bin\\mysql.exe",
            "mysql.exe"
    );

    private final String mysqldumpExecutable;
    private final String mysqlExecutable;
    private final String username;
    private final String password;

    public BackupService() {
        this(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public BackupService(String username, String password) {
        this.username = username;
        this.password = password;
        this.mysqldumpExecutable = locateExecutable(MYSQLDUMP_PATHS);
        this.mysqlExecutable = locateExecutable(MYSQL_PATHS);
    }

    public ProcessResult backupDatabase(DatabaseTarget target, File destination) throws Exception {
        ensureParentDirectory(destination);
        ProcessBuilder builder = new ProcessBuilder(
                mysqldumpExecutable,
                "-u", username,
                "-p" + password,
                "--single-transaction",
                "--routines",
                "--triggers",
                target.getDatabaseName()
        );
        builder.redirectOutput(destination);
        builder.redirectErrorStream(true);
        return execute(builder, "Backup " + target.getDatabaseName());
    }

    public ProcessResult restoreDatabase(DatabaseTarget target, File source) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(
                mysqlExecutable,
                "-u", username,
                "-p" + password,
                target.getDatabaseName()
        );
        builder.redirectInput(source);
        builder.redirectErrorStream(true);
        return execute(builder, "Restore " + target.getDatabaseName());
    }

    private ProcessResult execute(ProcessBuilder builder, String operation) throws Exception {
        Process process = builder.start();
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }
        int exitCode = process.waitFor();
        boolean success = exitCode == 0;
        String message = success ? operation + " completed." : output.toString();
        return new ProcessResult(success, message.trim());
    }

    private void ensureParentDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private String locateExecutable(List<String> possiblePaths) {
        for (String path : possiblePaths) {
            File candidate = new File(path);
            if (candidate.exists()) {
                return candidate.getAbsolutePath();
            }
        }
        return possiblePaths.get(possiblePaths.size() - 1);
    }

    public static class ProcessResult {
        private final boolean success;
        private final String message;

        public ProcessResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
