package com.forms;

import com.service.FileService;
import com.service.InactivityTimerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.main.MainForm;

public class GroupsForm {
    private JPanel GroupsPanel;
    private JLabel manageGroupsLabel;
    private JLabel manageInfoLabel;
    private JLabel statusLabel;
    private JLabel dateTimeLabel;
    private JPanel groupInfoPanel;
    private JPanel actionPanel;
    private JPanel tableListPanel;
    private JLabel groupInfoLabel;
    private JTextField groupCodeField;
    private JButton addGroupButton;
    private JButton updateGroupButton;
    private JButton removeGroupButton;
    private JButton backButton;
    private JTable groupsTable;
    private JScrollPane tableScrollPanel;
    private JLabel groupCodeLabel;
    private JLabel groupNumberLabel;
    private JLabel numberOfStudentsLabel;
    private JLabel actionsLabel;
    private JLabel groupsListLabel;
    private JTextField groupNumberField;
    private JTextField studentsCountField;

    private DefaultTableModel tableModel;
    private InactivityTimerService timerService;

    public GroupsForm() {
        timerService = new InactivityTimerService(statusLabel, dateTimeLabel, GroupsPanel);
        timerService.startTimer();


        tableModel = new DefaultTableModel(
                new Object[]{"Group Code", "Group Number", "Students Count"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        groupsTable.setModel(tableModel);
        groupsTable.setRowHeight(25);
        groupsTable.setFillsViewportHeight(true);
        groupsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupsTable.getTableHeader().setReorderingAllowed(false);

        loadGroupsFromFile();

        addGroupButton.addActionListener(e -> {
            timerService.resetTimer();

            String groupCode = groupCodeField.getText().trim().toUpperCase();
            String groupNumberText = groupNumberField.getText().trim();
            String studentsCountText = studentsCountField.getText().trim();

            if (!validateInput(groupCode, groupNumberText, studentsCountText)) {
                return;
            }

            int groupNumber = Integer.parseInt(groupNumberText);
            int studentsCount = Integer.parseInt(studentsCountText);

            if (isDuplicateGroup(groupCode, groupNumber, -1)) {
                JOptionPane.showMessageDialog(GroupsPanel, "This group already exists.");
                return;
            }

            tableModel.addRow(new Object[]{groupCode, groupNumber, studentsCount});
            saveGroupsToFile();
            clearFields();
        });

        updateGroupButton.addActionListener(e -> {
            timerService.resetTimer();

            int selectedRow = groupsTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(GroupsPanel, "Please select a row to update.");
                return;
            }

            String groupCode = groupCodeField.getText().trim().toUpperCase();
            String groupNumberText = groupNumberField.getText().trim();
            String studentsCountText = studentsCountField.getText().trim();

            if (!validateInput(groupCode, groupNumberText, studentsCountText)) {
                return;
            }

            int groupNumber = Integer.parseInt(groupNumberText);
            int studentsCount = Integer.parseInt(studentsCountText);

            if (isDuplicateGroup(groupCode, groupNumber, selectedRow)) {
                JOptionPane.showMessageDialog(GroupsPanel, "This group already exists.");
                return;
            }

            tableModel.setValueAt(groupCode, selectedRow, 0);
            tableModel.setValueAt(groupNumber, selectedRow, 1);
            tableModel.setValueAt(studentsCount, selectedRow, 2);

            saveGroupsToFile();
            clearFields();
        });

        removeGroupButton.addActionListener(e -> {
            timerService.resetTimer();

            int selectedRow = groupsTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(GroupsPanel,
                        "Please select a row to remove.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    GroupsPanel,
                    "Are you sure you want to remove this group?",
                    "Remove Group",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                saveGroupsToFile();
                clearFields();
            }
        });

        groupsTable.getSelectionModel().addListSelectionListener(this::handleTableSelection);

        backButton.addActionListener(e -> {
            timerService.resetTimer();
            timerService.stopTimer();

            JFrame frame = new JFrame("Main Menu");
            frame.setContentPane(new MainForm().getRootPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/com/images/icon.png"));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Window currentWindow = SwingUtilities.getWindowAncestor(GroupsPanel);
            if (currentWindow != null) {
                currentWindow.dispose();
            }
        });
    }
    private void handleTableSelection(javax.swing.event.ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = groupsTable.getSelectedRow();

            if (selectedRow != -1) {
                groupCodeField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                groupNumberField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                studentsCountField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            }
        }
    }

    public JPanel getRootPanel() {
        return GroupsPanel;
    }

    private boolean validateInput(String groupCode, String groupNumberText, String studentsCountText) {
        if (groupCode.isEmpty()) {
            JOptionPane.showMessageDialog(GroupsPanel, "Group code cannot be empty.");
            return false;
        }

        int groupNumber;
        int studentsCount;

        try {
            groupNumber = Integer.parseInt(groupNumberText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(GroupsPanel, "Group number must be an integer.");
            return false;
        }

        try {
            studentsCount = Integer.parseInt(studentsCountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(GroupsPanel, "Students count must be an integer.");
            return false;
        }

        if (groupNumber < 1 || groupNumber > 99) {
            JOptionPane.showMessageDialog(GroupsPanel, "Group number must be between 1 and 99.");
            return false;
        }

        if (studentsCount < 1 || studentsCount > 35) {
            JOptionPane.showMessageDialog(GroupsPanel, "Students count must be between 1 and 35.");
            return false;
        }

        return true;
    }

    private boolean isDuplicateGroup(String groupCode, int groupNumber, int excludedRow) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (i == excludedRow) {
                continue;
            }

            String existingCode = tableModel.getValueAt(i, 0).toString();
            int existingNumber = Integer.parseInt(tableModel.getValueAt(i, 1).toString());

            if (existingCode.equalsIgnoreCase(groupCode) && existingNumber == groupNumber) {
                return true;
            }
        }
        return false;
    }

    private void clearFields() {
        groupCodeField.setText("");
        groupNumberField.setText("");
        studentsCountField.setText("");
        groupsTable.clearSelection();
    }

    private void saveGroupsToFile() {
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String line = tableModel.getValueAt(i, 0) + "," +
                    tableModel.getValueAt(i, 1) + "," +
                    tableModel.getValueAt(i, 2);

            lines.add(line);
        }

        FileService.saveToCsv("groups.csv", lines, GroupsPanel);
    }

    private void loadGroupsFromFile() {
        List<String> lines = FileService.loadFromCsv("groups.csv", GroupsPanel);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                try {
                    String groupCode = parts[0].trim().toUpperCase();
                    int groupNumber = Integer.parseInt(parts[1].trim());
                    int studentsCount = Integer.parseInt(parts[2].trim());

                    if (groupNumber >= 1 && groupNumber <= 99 &&
                            studentsCount >= 1 && studentsCount <= 35) {
                        tableModel.addRow(new Object[]{groupCode, groupNumber, studentsCount});
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }
}