package edu.univ.erp.ui;

import edu.univ.erp.api.admin.AdminBackupApi;
import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.DatabaseTarget;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.function.Supplier;

public class BackupRestorePanel extends JPanel {
    private final AdminBackupApi backupApi = new AdminBackupApi();
    private final JLabel statusLabel = new JLabel();

    public BackupRestorePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(24, 24, 24, 24);

        JButton backupBothBtn = new JButton("Backup Both Databases");
        JButton backupMainBtn = new JButton("Backup Main Database");
        JButton backupAuthBtn = new JButton("Backup Auth Database");
        JButton restoreBothBtn = new JButton("Restore Both Databases");
        JButton restoreMainBtn = new JButton("Restore Main Database");
        JButton restoreAuthBtn = new JButton("Restore Auth Database");

        statusLabel.setPreferredSize(new Dimension(500, 60));

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(backupBothBtn, gbc);
        gbc.gridy = 1;
        add(backupMainBtn, gbc);
        gbc.gridy = 2;
        add(backupAuthBtn, gbc);
        gbc.gridy = 3;
        add(restoreBothBtn, gbc);
        gbc.gridy = 4;
        add(restoreMainBtn, gbc);
        gbc.gridy = 5;
        add(restoreAuthBtn, gbc);
        gbc.gridy = 6;
        add(statusLabel, gbc);

        backupMainBtn.addActionListener(e -> chooseDestination("erp_main_backup.sql", file ->
                runTask("Backing up main...", () -> backupApi.backup(DatabaseTarget.MAIN, file))));

        backupAuthBtn.addActionListener(e -> chooseDestination("erp_auth_backup.sql", file ->
                runTask("Backing up auth...", () -> backupApi.backup(DatabaseTarget.AUTH, file))));

        backupBothBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Main Database Backup");
            chooser.setSelectedFile(new File("erp_main_backup.sql"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File mainFile = chooser.getSelectedFile();
                File authFile = new File(mainFile.getParentFile(), "erp_auth_backup.sql");
                runTask("Backing up both databases...",
                        () -> backupApi.backupBoth(mainFile, authFile));
            }
        });

        restoreMainBtn.addActionListener(e -> chooseSource(file ->
                confirmAndRun("WARNING: This will overwrite the main database!", () ->
                        runTask("Restoring main...", () -> backupApi.restore(DatabaseTarget.MAIN, file)))));

        restoreAuthBtn.addActionListener(e -> chooseSource(file ->
                confirmAndRun("WARNING: This will overwrite the auth database!", () ->
                        runTask("Restoring auth...", () -> backupApi.restore(DatabaseTarget.AUTH, file)))));

        restoreBothBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Main Database Backup File");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File mainFile = chooser.getSelectedFile();
                File authFile = new File(mainFile.getParentFile(), "erp_auth_backup.sql");
                if (!authFile.exists()) {
                    JOptionPane.showMessageDialog(this,
                            "Auth backup file not found at " + authFile.getAbsolutePath() +
                                    "\nPlease select it manually.",
                            "File Not Found", JOptionPane.WARNING_MESSAGE);
                    JFileChooser authChooser = new JFileChooser(mainFile.getParentFile());
                    authChooser.setDialogTitle("Select Auth Database Backup File");
                    if (authChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        authFile = authChooser.getSelectedFile();
                    } else {
                        return;
                    }
                }
                File finalAuthFile = authFile;
                confirmAndRun("WARNING: This will overwrite BOTH databases!", () ->
                        runTask("Restoring both databases...",
                                () -> backupApi.restoreBoth(mainFile, finalAuthFile)));
            }
        });
    }

    private void chooseDestination(String defaultName, java.util.function.Consumer<File> action) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Backup File");
        chooser.setSelectedFile(new File(defaultName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            action.accept(chooser.getSelectedFile());
        }
    }

    private void chooseSource(java.util.function.Consumer<File> action) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Backup File");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            action.accept(chooser.getSelectedFile());
        }
    }

    private void confirmAndRun(String warningMessage, Runnable task) {
        int confirm = JOptionPane.showConfirmDialog(this, warningMessage,
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            task.run();
        }
    }

    private void runTask(String startMessage, Supplier<ApiResponse> action) {
        statusLabel.setText(startMessage);
        setButtonsEnabled(false);
        SwingWorker<ApiResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected ApiResponse doInBackground() {
                return action.get();
            }

            @Override
            protected void done() {
                setButtonsEnabled(true);
                try {
                    ApiResponse response = get();
                    statusLabel.setText(response.getMessage());
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(BackupRestorePanel.this,
                                response.getMessage(), "Backup/Restore", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(BackupRestorePanel.this,
                                response.getMessage(), "Backup/Restore", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Operation failed: " + ex.getMessage());
                    JOptionPane.showMessageDialog(BackupRestorePanel.this,
                            "Operation failed: " + ex.getMessage(),
                            "Backup/Restore", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Component component : getComponents()) {
            component.setEnabled(enabled);
            if (component instanceof Container container) {
                setContainerEnabled(container, enabled);
            }
        }
        statusLabel.setEnabled(true);
    }

    private void setContainerEnabled(Container container, boolean enabled) {
        for (Component child : container.getComponents()) {
            child.setEnabled(enabled);
            if (child instanceof Container nested) {
                setContainerEnabled(nested, enabled);
            }
        }
    }
}
