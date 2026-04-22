package com.forms;

import com.service.FileService;
import com.service.InactivityTimerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.main.MainForm;

public class AudiencesForm {
    private JPanel AudiencesPanel;
    private JLabel manageAudiencesLabel;
    private JLabel manageInfoLabel;
    private JLabel statusLabel;
    private JLabel dateTimeLabel;
    private JPanel audienceInfoPanel;
    private JPanel actionPanel;
    private JPanel tableListPanel;
    private JLabel audienceInfoLabel;
    private JTextField audienceNumberField;
    private JButton addAudienceButton;
    private JButton updateAudienceButton;
    private JButton removeAudienceButton;
    private JButton backButton;
    private JTable audiencesTable;
    private JScrollPane tableScrollPanel;
    private JLabel numberAudienceLabel;
    private JLabel countSeatsLabel;
    private JLabel audienceTypeLabel;
    private JLabel actionsLabel;
    private JLabel audienceListLabel;
    private JTextField seatsCountField;
    private JComboBox audienceComboBox;

    private DefaultTableModel tableModel;
    private InactivityTimerService timerService;

    public AudiencesForm() {
        timerService = new InactivityTimerService(statusLabel, dateTimeLabel, AudiencesPanel);
        timerService.startTimer();


        tableModel = new DefaultTableModel(
                new Object[]{"Number of Audience", "Number Of Seats", "Audience Type"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        audienceComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "Standard",
                "ComputerClass",
                "Laboratory"
        }));

        audiencesTable.setModel(tableModel);
        audiencesTable.setRowHeight(25);
        audiencesTable.setFillsViewportHeight(true);
        audiencesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        audiencesTable.getTableHeader().setReorderingAllowed(false);

        loadAudiencesFromFile();

        addAudienceButton.addActionListener(e -> {
            timerService.resetTimer();

            String audienceNumberText = audienceNumberField.getText().trim();
            String seatsCountText = seatsCountField.getText().trim();
            String audienceType = audienceComboBox.getSelectedItem().toString();

            if (!validateInput(audienceNumberText, seatsCountText, audienceType)) {
                return;
            }

            int audienceNumber = Integer.parseInt(audienceNumberText);
            int seatsCount = Integer.parseInt(seatsCountText);

            if (isDuplicateAudience(audienceNumber, -1)) {
                JOptionPane.showMessageDialog(AudiencesPanel, "This audience already exists.");
                return;
            }

            tableModel.addRow(new Object[]{audienceNumber, seatsCount, audienceType});
            saveAudiencesToFile();
            clearFields();
        });

        updateAudienceButton.addActionListener(e -> {
            timerService.resetTimer();

            int selectedRow = audiencesTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AudiencesPanel, "Please select a row to update.");
                return;
            }

            String audienceNumberText = audienceNumberField.getText().trim();
            String seatsCountText = seatsCountField.getText().trim();
            String audienceType = audienceComboBox.getSelectedItem().toString();

            if (!validateInput(audienceNumberText, seatsCountText, audienceType)) {
                return;
            }

            int audienceNumber = Integer.parseInt(audienceNumberText);
            int seatsCount = Integer.parseInt(seatsCountText);

            if (isDuplicateAudience(audienceNumber, selectedRow)) {
                JOptionPane.showMessageDialog(AudiencesPanel, "This audience already exists.");
                return;
            }

            tableModel.setValueAt(audienceNumber, selectedRow, 0);
            tableModel.setValueAt(seatsCount, selectedRow, 1);
            tableModel.setValueAt(audienceType, selectedRow, 2);

            saveAudiencesToFile();
            clearFields();
        });

        removeAudienceButton.addActionListener(e -> {
            timerService.resetTimer();

            int selectedRow = audiencesTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(AudiencesPanel, "Please select a row to remove.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    AudiencesPanel,
                    "Are you sure you want to remove this audience?",
                    "Remove Audience",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                saveAudiencesToFile();
                clearFields();
            }
        });

        audiencesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = audiencesTable.getSelectedRow();

                if (selectedRow != -1) {
                    audienceNumberField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    seatsCountField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    audienceComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                }
            }
        });

        backButton.addActionListener(e -> {
            timerService.resetTimer();
            timerService.stopTimer();

            JFrame frame = new JFrame("Schedule");
            frame.setContentPane(new MainForm().getRootPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/com/images/icon.png"));
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Window currentWindow = SwingUtilities.getWindowAncestor(AudiencesPanel);
            if (currentWindow != null) {
                currentWindow.dispose();
            }
        });
    }

    public JPanel getRootPanel() {
        return AudiencesPanel;
    }

    private boolean validateInput(String audienceNumberText, String seatsCountText, String audienceType) {
        int audienceNumber;
        int seatsCount;

        if (audienceNumberText.isEmpty()) {
            JOptionPane.showMessageDialog(AudiencesPanel, "Audience number cannot be empty.");
            return false;
        }else if (seatsCountText.isEmpty()) {
            JOptionPane.showMessageDialog(AudiencesPanel, "Seats count cannot be empty.");
            return false;
        }
        try {
            audienceNumber = Integer.parseInt(audienceNumberText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(AudiencesPanel, "Audience number must be an integer.");
            return false;
        }
        try {
            seatsCount = Integer.parseInt(seatsCountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(AudiencesPanel, "Seats count must be an integer.");
            return false;
        }
        if (audienceNumber <= 0) {
            JOptionPane.showMessageDialog(AudiencesPanel, "Audience number must be greater than 0.");
            return false;
        }
        if (seatsCount <= 0) {
            JOptionPane.showMessageDialog(AudiencesPanel, "Seats count must be greater than 0.");
            return false;
        }

        if (audienceType == null || audienceType.isEmpty()) {
            JOptionPane.showMessageDialog(AudiencesPanel, "Please select an audience type.");
            return false;
        }
        return true;
    }
    private boolean isDuplicateAudience(int audienceNumber, int excludedRow) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (i == excludedRow) {
                continue;
            }

            int existingAudienceNumber = Integer.parseInt(tableModel.getValueAt(i, 0).toString());

            if (existingAudienceNumber == audienceNumber) {
                return true;
            }
        }
        return false;
    }
    private void clearFields() {
        audienceNumberField.setText("");
        seatsCountField.setText("");
        audienceComboBox.setSelectedIndex(0);
        audiencesTable.clearSelection();
    }
    private void saveAudiencesToFile() {
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String line = tableModel.getValueAt(i, 0) + "," +
                    tableModel.getValueAt(i, 1) + "," +
                    tableModel.getValueAt(i, 2);

            lines.add(line);
        }

        FileService.saveToCsv("audiences.csv", lines, AudiencesPanel);
    }

    private void loadAudiencesFromFile() {
        List<String> lines = FileService.loadFromCsv("audiences.csv", AudiencesPanel);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                try {
                    int audienceNumber = Integer.parseInt(parts[0].trim());
                    int seatsCount = Integer.parseInt(parts[1].trim());
                    String audienceType = parts[2].trim();

                    if (audienceNumber > 0 && seatsCount > 0 && !audienceType.isEmpty()) {
                        tableModel.addRow(new Object[]{audienceNumber, seatsCount, audienceType});
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }
}