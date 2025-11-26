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

        JButton backupMainBtn = new JButton("Backup erp_main Database");
        UITheme.stylePrimaryButton(backupMainBtn);
        backupMainBtn.setPreferredSize(new Dimension(250, 40));
        
        JButton backupAuthBtn = new JButton("Backup erp_auth Database");
        UITheme.stylePrimaryButton(backupAuthBtn);
        backupAuthBtn.setPreferredSize(new Dimension(250, 40));

        statusLabel.setPreferredSize(new Dimension(500, 60));
        statusLabel.setFont(UITheme.FONT_BODY);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(backupMainBtn, gbc);
        gbc.gridy = 1;
        buttonPanel.add(backupAuthBtn, gbc);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(UITheme.BG_MAIN);
        statusPanel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        
        add(buttonPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        backupMainBtn.addActionListener(e -> chooseDestination("erp_main_backup.sql", file ->
                runTask("Backing up erp_main...", () -> backupApi.backup(DatabaseTarget.MAIN, file))));

        backupAuthBtn.addActionListener(e -> chooseDestination("erp_auth_backup.sql", file ->
                runTask("Backing up erp_auth...", () -> backupApi.backup(DatabaseTarget.AUTH, file))));
    }

    private void chooseDestination(String defaultName, java.util.function.Consumer<File> action) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Backup File");
        chooser.setSelectedFile(new File(defaultName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            action.accept(chooser.getSelectedFile());
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
                                response.getMessage(), "Backup", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(BackupRestorePanel.this,
                                response.getMessage(), "Backup", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Backup failed: " + ex.getMessage());
                    JOptionPane.showMessageDialog(BackupRestorePanel.this,
                            "Backup failed: " + ex.getMessage(),
                            "Backup", JOptionPane.ERROR_MESSAGE);
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
