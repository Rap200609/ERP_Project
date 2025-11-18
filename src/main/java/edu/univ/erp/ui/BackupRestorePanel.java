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
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        UITheme.styleCardPanel(buttonPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.CENTER;

        JButton backupBothBtn = new JButton("Backup Both Databases");
        UITheme.stylePrimaryButton(backupBothBtn);
        backupBothBtn.setPreferredSize(new Dimension(250, 40));
        JButton backupMainBtn = new JButton("Backup Main Database");
        UITheme.stylePrimaryButton(backupMainBtn);
        backupMainBtn.setPreferredSize(new Dimension(250, 40));
        JButton backupAuthBtn = new JButton("Backup Auth Database");
        UITheme.stylePrimaryButton(backupAuthBtn);
        backupAuthBtn.setPreferredSize(new Dimension(250, 40));
        JButton restoreBothBtn = new JButton("Restore Both Databases");
        UITheme.styleSecondaryButton(restoreBothBtn);
        restoreBothBtn.setPreferredSize(new Dimension(250, 40));
        JButton restoreMainBtn = new JButton("Restore Main Database");
        UITheme.styleSecondaryButton(restoreMainBtn);
        restoreMainBtn.setPreferredSize(new Dimension(250, 40));
        JButton restoreAuthBtn = new JButton("Restore Auth Database");
        UITheme.styleSecondaryButton(restoreAuthBtn);
        restoreAuthBtn.setPreferredSize(new Dimension(250, 40));

        statusLabel.setPreferredSize(new Dimension(500, 60));
        statusLabel.setFont(UITheme.FONT_BODY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(backupBothBtn, gbc);
        gbc.gridy = 1;
        buttonPanel.add(backupMainBtn, gbc);
        gbc.gridy = 2;
        buttonPanel.add(backupAuthBtn, gbc);
        gbc.gridy = 3;
        buttonPanel.add(restoreBothBtn, gbc);
        gbc.gridy = 4;
        buttonPanel.add(restoreMainBtn, gbc);
        gbc.gridy = 5;
        buttonPanel.add(restoreAuthBtn, gbc);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(UITheme.BG_MAIN);
        statusPanel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        add(buttonPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

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
