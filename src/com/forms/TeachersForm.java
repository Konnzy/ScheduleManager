package com.forms;

import com.service.FileService;
import com.service.InactivityTimerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.main.MainForm;

public class TeachersForm {
    private JPanel TeachersPanel;
    private JLabel manageTeachersLabel;
    private JLabel manageInfoLabel;
    private JLabel statusLabel;
    private JLabel dateTimeLabel;
    private JPanel teacherInfoPanel;
    private JPanel actionPanel;
    private JPanel tableListPanel;
    private JLabel teacherInfoLabel;
    private JTextField teacherNameField;
    private JButton addTeacherButton;
    private JButton updateTeacherButton;
    private JButton removeTeacherButton;
    private JButton backButton;
    private JTable teachersTable;
    private JScrollPane tableScrollPanel;
    private JLabel nameLabel;
    private JLabel surnameLabel;
    private JLabel subjectLabel;
    private JLabel actionsLabel;
    private JLabel teacherListLabel;
    private JTextField teacherSurnameField;
    private JTextField teacherSubjectField;

    private DefaultTableModel tableModel;
    private InactivityTimerService timerService;

    public TeachersForm() {
        timerService = new InactivityTimerService(statusLabel, dateTimeLabel, TeachersPanel);
        timerService.startTimer();


        tableModel = new DefaultTableModel(
                new Object[]{"First Name", "Last Name", "Subject"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        teachersTable.setModel(tableModel);
        teachersTable.setRowHeight(25);
        teachersTable.setFillsViewportHeight(true);
        teachersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teachersTable.getTableHeader().setReorderingAllowed(false);

        loadTeachersFromFile();

        addTeacherButton.addActionListener(e -> {
            timerService.resetTimer();

            String firstName = teacherNameField.getText().trim();
            String lastName = teacherSurnameField.getText().trim();
            String subject = teacherSubjectField.getText().trim();

            if (!validateInput(firstName, lastName, subject)) {
                return;
            }


            tableModel.addRow(new Object[]{firstName, lastName, subject});
            saveTeachersToFile();
            clearFields();
        });

        updateTeacherButton.addActionListener(e -> {
            timerService.resetTimer();

            int selectedRow = teachersTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(TeachersPanel, "Please select a row to update.");
                return;
            }

            String firstName = teacherNameField.getText().trim();
            String lastName = teacherSurnameField.getText().trim();
            String subject = teacherSubjectField.getText().trim();

            if (!validateInput(firstName, lastName, subject)) {
                return;
            }

            tableModel.setValueAt(firstName, selectedRow, 0);
            tableModel.setValueAt(lastName, selectedRow, 1);
            tableModel.setValueAt(subject, selectedRow, 2);


            saveTeachersToFile();
            clearFields();
        });

        removeTeacherButton.addActionListener(e -> {
            timerService.resetTimer();

            int selectedRow = teachersTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(TeachersPanel,
                        "Please select a row to remove.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    TeachersPanel,
                    "Are you sure you want to remove this teacher?",
                    "Remove Teacher",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                saveTeachersToFile();
                clearFields();
            }
        });

        teachersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = teachersTable.getSelectedRow();

                if (selectedRow != -1) {
                    teacherNameField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    teacherSurnameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    teacherSubjectField.setText(tableModel.getValueAt(selectedRow, 2).toString());
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

            Window currentWindow = SwingUtilities.getWindowAncestor(TeachersPanel);
            if (currentWindow != null) {
                currentWindow.dispose();
            }
        });
    }

    public JPanel getRootPanel() {
        return TeachersPanel;
    }

    private boolean validateInput(String firstName, String lastName, String subject) {
        if (firstName.isEmpty() || containsDigits(firstName)) {
            JOptionPane.showMessageDialog(TeachersPanel, "First name cannot be empty or contain digits.");
            return false;
        }else if (lastName.isEmpty() || containsDigits(lastName)) {
            JOptionPane.showMessageDialog(TeachersPanel, "Last name cannot be empty or contain digits.");
            return false;
        }else if (subject.isEmpty() || containsDigits(subject)) {
            JOptionPane.showMessageDialog(TeachersPanel, "Subject cannot be empty or contain digits.");
            return false;
        }

        return true;
    }
    private boolean containsDigits(String text) {
        for (char ch : text.toCharArray()) {
            if (Character.isDigit(ch)) {
                return true;
            }
        }
        return false;
    }

    private void clearFields() {
        teacherNameField.setText("");
        teacherSurnameField.setText("");
        teacherSubjectField.setText("");
        teachersTable.clearSelection();
    }

    private void saveTeachersToFile() {
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String line = tableModel.getValueAt(i, 0) + "," +
                    tableModel.getValueAt(i, 1) + "," +
                    tableModel.getValueAt(i, 2);

            lines.add(line);
        }

        FileService.saveToCsv("teachers.csv", lines, TeachersPanel);
    }

    private void loadTeachersFromFile() {
        List<String> lines = FileService.loadFromCsv("teachers.csv", TeachersPanel);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                String firstName = parts[0].trim();
                String lastName = parts[1].trim();
                String subject = parts[2].trim();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !subject.isEmpty()) {
                    tableModel.addRow(new Object[]{firstName, lastName, subject});
                }
            }
        }
    }
}