package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class BackupRestorePanel extends JPanel {
    public BackupRestorePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(24, 24, 24, 24);

        JButton backupBtn = new JButton("Backup Main Database");
        JButton restoreBtn = new JButton("Restore Main Database");
        JButton backupAuthBtn = new JButton("Backup Auth Database");
        JButton restoreAuthBtn = new JButton("Restore Auth Database");
        JButton backupBothBtn = new JButton("Backup Both Databases");
        JButton restoreBothBtn = new JButton("Restore Both Databases");
        JLabel info = new JLabel();
        info.setPreferredSize(new Dimension(500, 60));

        gbc.gridx = 0; gbc.gridy = 0; add(backupBothBtn, gbc);
        gbc.gridy = 1; add(backupBtn, gbc);
        gbc.gridy = 2; add(backupAuthBtn, gbc);
        gbc.gridy = 3; add(restoreBothBtn, gbc);
        gbc.gridy = 4; add(restoreBtn, gbc);
        gbc.gridy = 5; add(restoreAuthBtn, gbc);
        gbc.gridy = 6; add(info, gbc);

        // Update these paths if your MySQL is installed elsewhere!
        String mysqldumpPath = findMySQLDumpPath();
        String mysqlPath = findMySQLPath();
        String username = "root";
        String password = "Rap@2006";

        backupBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Backup File");
            fileChooser.setSelectedFile(new File("erp_main_backup.sql"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File backupFile = fileChooser.getSelectedFile();
                performBackup(mysqldumpPath, username, password, "erp_main", backupFile, info);
            }
        });

        backupAuthBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Backup File");
            fileChooser.setSelectedFile(new File("erp_auth_backup.sql"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File backupFile = fileChooser.getSelectedFile();
                performBackup(mysqldumpPath, username, password, "erp_auth", backupFile, info);
            }
        });

        backupBothBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Main Database Backup");
            fileChooser.setSelectedFile(new File("erp_main_backup.sql"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File mainFile = fileChooser.getSelectedFile();
                
                // Get auth backup filename from same location
                File authFile = new File(mainFile.getParent(), "erp_auth_backup.sql");
                
                // Run backups sequentially in a background thread
                new Thread(() -> {
                    performBackupSync(mysqldumpPath, username, password, "erp_main", mainFile, info);
                    performBackupSync(mysqldumpPath, username, password, "erp_auth", authFile, info);
                }).start();
            }
        });

        restoreBothBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Main Database Backup File");
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File mainFile = fileChooser.getSelectedFile();
                
                // Find auth backup file in same directory
                File authFile = new File(mainFile.getParent(), "erp_auth_backup.sql");
                
                File finalAuthFile = authFile;
                if (!finalAuthFile.exists()) {
                    JOptionPane.showMessageDialog(this,
                        "Auth backup file not found: " + finalAuthFile.getName() + "\n\nPlease select it manually.",
                        "File Not Found", JOptionPane.WARNING_MESSAGE);
                    JFileChooser authChooser = new JFileChooser(mainFile.getParent());
                    authChooser.setDialogTitle("Select Auth Database Backup File");
                    if (authChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        finalAuthFile = authChooser.getSelectedFile();
                    } else {
                        return; // User cancelled
                    }
                }
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "WARNING: This will overwrite BOTH databases!\nAre you sure you want to continue?",
                    "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                final File authFileToUse = finalAuthFile; // Create final reference for lambda
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Run restores sequentially in a background thread
                    new Thread(() -> {
                        performRestoreSync(mysqlPath, username, password, "erp_main", mainFile, info);
                        performRestoreSync(mysqlPath, username, password, "erp_auth", authFileToUse, info);
                    }).start();
                }
            }
        });

        restoreBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Backup File to Restore");
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File backupFile = fileChooser.getSelectedFile();
                int confirm = JOptionPane.showConfirmDialog(this,
                    "WARNING: This will overwrite the current database!\nAre you sure you want to continue?",
                    "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    String dbName = backupFile.getName().contains("auth") ? "erp_auth" : "erp_main";
                    performRestore(mysqlPath, username, password, dbName, backupFile, info);
                }
            }
        });

        restoreAuthBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Auth Backup File to Restore");
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File backupFile = fileChooser.getSelectedFile();
                int confirm = JOptionPane.showConfirmDialog(this,
                    "WARNING: This will overwrite the auth database!\nAre you sure you want to continue?",
                    "Confirm Restore", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    performRestore(mysqlPath, username, password, "erp_auth", backupFile, info);
                }
            }
        });
    }

    private String findMySQLDumpPath() {
        String[] possiblePaths = {
            "C:\\Program Files\\MySQL\\MySQL Server 9.4\\bin\\mysqldump.exe",
            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe",
            "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysqldump.exe",
            "C:\\xampp\\mysql\\bin\\mysqldump.exe",
            "mysqldump.exe" // Try PATH
        };
        
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) return path;
        }
        return "mysqldump.exe"; // Fallback to PATH
    }

    private String findMySQLPath() {
        String[] possiblePaths = {
            "C:\\Program Files\\MySQL\\MySQL Server 9.4\\bin\\mysql.exe",
            "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe",
            "C:\\Program Files\\MySQL\\MySQL Server 8.4\\bin\\mysql.exe",
            "C:\\xampp\\mysql\\bin\\mysql.exe",
            "mysql.exe" // Try PATH
        };
        
        for (String path : possiblePaths) {
            File f = new File(path);
            if (f.exists()) return path;
        }
        return "mysql.exe"; // Fallback to PATH
    }

    private void performBackup(String mysqldumpPath, String username, String password, 
                               String dbName, File outputFile, JLabel infoLabel) {
        infoLabel.setText("Backing up " + dbName + "...");
        SwingUtilities.invokeLater(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    mysqldumpPath,
                    "-u", username,
                    "-p" + password,
                    "--single-transaction",
                    "--routines",
                    "--triggers",
                    dbName
                );
                pb.redirectOutput(outputFile);
                pb.redirectErrorStream(true);
                
                Process proc = pb.start();
                int result = proc.waitFor();
                
                if (result == 0) {
                    infoLabel.setText("Backup complete: " + outputFile.getAbsolutePath());
                    JOptionPane.showMessageDialog(this, 
                        "Backup completed successfully!\nSaved to: " + outputFile.getAbsolutePath(),
                        "Backup Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Read error stream
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(proc.getInputStream()))) {
                        StringBuilder errorMsg = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorMsg.append(line).append("\n");
                        }
                        infoLabel.setText("Backup error. Check console.");
                        JOptionPane.showMessageDialog(this, 
                            "Backup failed:\n" + errorMsg.toString(),
                            "Backup Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                infoLabel.setText("Backup error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Backup error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void performRestore(String mysqlPath, String username, String password, 
                                String dbName, File backupFile, JLabel infoLabel) {
        infoLabel.setText("Restoring " + dbName + "...");
        SwingUtilities.invokeLater(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    mysqlPath,
                    "-u", username,
                    "-p" + password,
                    dbName
                );
                pb.redirectInput(backupFile);
                pb.redirectErrorStream(true);
                
                Process proc = pb.start();
                
                // Read any error output
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(proc.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                
                int result = proc.waitFor();
                
                if (result == 0) {
                    infoLabel.setText("Restore complete.");
                    JOptionPane.showMessageDialog(this, 
                        "Database restored successfully!",
                        "Restore Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    infoLabel.setText("Restore error. Check message.");
                    JOptionPane.showMessageDialog(this, 
                        "Restore failed:\n" + output.toString(),
                        "Restore Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                infoLabel.setText("Restore error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Restore error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Synchronous versions for sequential operations
    private void performBackupSync(String mysqldumpPath, String username, String password, 
                                   String dbName, File outputFile, JLabel infoLabel) {
        SwingUtilities.invokeLater(() -> infoLabel.setText("Backing up " + dbName + "..."));
        
        try {
            ProcessBuilder pb = new ProcessBuilder(
                mysqldumpPath,
                "-u", username,
                "-p" + password,
                "--single-transaction",
                "--routines",
                "--triggers",
                dbName
            );
            pb.redirectOutput(outputFile);
            pb.redirectErrorStream(true);
            
            Process proc = pb.start();
            int result = proc.waitFor();
            
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    infoLabel.setText("Backup complete: " + outputFile.getAbsolutePath());
                });
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                StringBuilder errorMsg = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorMsg.append(line).append("\n");
                }
                final String error = errorMsg.toString();
                SwingUtilities.invokeLater(() -> {
                    infoLabel.setText("Backup error for " + dbName);
                    JOptionPane.showMessageDialog(this, 
                        "Backup failed for " + dbName + ":\n" + error,
                        "Backup Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                infoLabel.setText("Backup error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Backup error for " + dbName + ": " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private void performRestoreSync(String mysqlPath, String username, String password, 
                                    String dbName, File backupFile, JLabel infoLabel) {
        SwingUtilities.invokeLater(() -> infoLabel.setText("Restoring " + dbName + "..."));
        
        try {
            ProcessBuilder pb = new ProcessBuilder(
                mysqlPath,
                "-u", username,
                "-p" + password,
                dbName
            );
            pb.redirectInput(backupFile);
            pb.redirectErrorStream(true);
            
            Process proc = pb.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int result = proc.waitFor();
            
            if (result == 0) {
                SwingUtilities.invokeLater(() -> {
                    infoLabel.setText("Restore complete for " + dbName + ".");
                });
            } else {
                final String error = output.toString();
                SwingUtilities.invokeLater(() -> {
                    infoLabel.setText("Restore error for " + dbName);
                    JOptionPane.showMessageDialog(this, 
                        "Restore failed for " + dbName + ":\n" + error,
                        "Restore Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                infoLabel.setText("Restore error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, 
                    "Restore error for " + dbName + ": " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }
}
