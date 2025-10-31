package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class BackupRestorePanel extends JPanel {
    public BackupRestorePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(24, 24, 24, 24);

        JButton backupBtn = new JButton("Backup ERP DB");
        JButton restoreBtn = new JButton("Restore ERP DB");
        JLabel info = new JLabel();

        gbc.gridx = 0; gbc.gridy = 0; add(backupBtn, gbc);
        gbc.gridy = 1; add(restoreBtn, gbc);
        gbc.gridy = 2; add(info, gbc);

        // Update these paths if your MySQL is installed elsewhere!
        String mysqldumpPath = "C:\\Program Files\\MySQL\\MySQL Server 9.4\\bin\\mysqldump.exe\"";
        String mysqlPath = "C:\\Program Files\\MySQL\\MySQL Server 9.4\\bin\\mysql.exe\"";
        String password = "Rap@2006";

        backupBtn.addActionListener(e -> {
            info.setText("Backing up...");
            SwingUtilities.invokeLater(() -> {
                try {
                    String cmd = mysqldumpPath + " -u root -p" + password + " erp_main -r erp_main_backup.sql";
                    Process proc = Runtime.getRuntime().exec(cmd);
                    int result = proc.waitFor();
                    if (result == 0) {
                        info.setText("Backup complete: erp_main_backup.sql");
                    } else {
                        info.setText("Backup error. Exit code: " + result);
                    }
                } catch (Exception ex) {
                    info.setText("Backup error: " + ex.getMessage());
                }
            });
        });

        restoreBtn.addActionListener(e -> {
            info.setText("Restoring...");
            SwingUtilities.invokeLater(() -> {
                try {
                    String cmd = mysqlPath + " -u root -p" + password + " erp_main < erp_main_backup.sql";
                    String[] fullCmd = { "cmd.exe", "/c", cmd };
                    Process proc = Runtime.getRuntime().exec(fullCmd);
                    int result = proc.waitFor();
                    if (result == 0) {
                        info.setText("Restore complete.");
                    } else {
                        info.setText("Restore error. Exit code: " + result);
                    }
                } catch (Exception ex) {
                    info.setText("Restore error: " + ex.getMessage());
                }
            });
        });
    }
}
