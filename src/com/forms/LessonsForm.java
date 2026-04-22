package com.forms;

import com.service.FileService;
import com.service.InactivityTimerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.main.MainForm;

public class LessonsForm {
    private JPanel LessonsPanel;
    private JLabel manageLessonsLabel;
    private JLabel manageInfoLabel;
    private JLabel statusLabel;
    private JLabel dateTimeLabel;
    private JPanel lessonInfoPanel;
    private JPanel actionPanel;
    private JPanel tableListPanel;
    private JLabel lessonInfoLabel;
    private JButton addLessonButton;
    private JButton updateLessonButton;
    private JButton removeLessonButton;
    private JButton backButton;
    private JTable lessonsTable;
    private JScrollPane tableScrollPanel;
    private JLabel teacherLabel;
    private JLabel audienceLabel;
    private JLabel lessonTimeLabel;
    private JLabel actionsLabel;
    private JLabel lessonListLabel;
    private JTextField seatsCountField;
    private JComboBox lessonTimeComboBox;
    private JLabel groupLabel;
    private JComboBox groupComboBox;
    private JComboBox teacherComboBox;
    private JComboBox audienceComboBox;
    private JLabel dayLabel;
    private JComboBox dayComboBox;

    private DefaultTableModel tableModel;
    private InactivityTimerService timerService;

    public LessonsForm() {
        timerService = new InactivityTimerService(statusLabel, dateTimeLabel, LessonsPanel);
        timerService.startTimer();


        tableModel = new DefaultTableModel(
                new Object[]{"Group", "Teacher", "Audience", "Day", "Time"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lessonsTable.setModel(tableModel);
        lessonsTable.setRowHeight(25);
        lessonsTable.setFillsViewportHeight(true);
        lessonsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lessonsTable.getTableHeader().setReorderingAllowed(false);

        loadGroupsToComboBox();
        loadTeachersToComboBox();
        loadAudiencesToComboBox();
        loadStaticComboBoxes();
        loadLessonsFromFile();

        addLessonButton.addActionListener(e -> {
            timerService.resetTimer();

            String group = groupComboBox.getSelectedItem().toString();
            String teacher = teacherComboBox.getSelectedItem().toString();
            String audience = audienceComboBox.getSelectedItem().toString();
            String day = dayComboBox.getSelectedItem().toString();
            String time = lessonTimeComboBox.getSelectedItem().toString();

            if (!validateInput(group, teacher, audience, day, time)) {
                return;
            }
            if (!checkAudienceCapacity(group, audience)) {
                return;
            }

            String conflictMessage = findLessonConflict(group, teacher, audience, day, time, -1);
            if (conflictMessage != null) {
                JOptionPane.showMessageDialog(LessonsPanel, conflictMessage);
                return;
            }

            tableModel.addRow(new Object[]{group, teacher, audience, day, time});
            saveLessonsToFile();
        });

        updateLessonButton.addActionListener(e -> {
            timerService.resetTimer();

            int selectedRow = lessonsTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(LessonsPanel, "Select lesson to update.");
                return;
            }

            String group = groupComboBox.getSelectedItem().toString();
            String teacher = teacherComboBox.getSelectedItem().toString();
            String audience = audienceComboBox.getSelectedItem().toString();
            String day = dayComboBox.getSelectedItem().toString();
            String time = lessonTimeComboBox.getSelectedItem().toString();

            if (!validateInput(group, teacher, audience, day, time)) {
                return;
            }
            if (!checkAudienceCapacity(group, audience)) {
                return;
            }

            String conflictMessage = findLessonConflict(group, teacher, audience, day, time, selectedRow);
            if (conflictMessage != null) {
                JOptionPane.showMessageDialog(LessonsPanel, conflictMessage);
                return;
            }

            tableModel.setValueAt(group, selectedRow, 0);
            tableModel.setValueAt(teacher, selectedRow, 1);
            tableModel.setValueAt(audience, selectedRow, 2);
            tableModel.setValueAt(day, selectedRow, 3);
            tableModel.setValueAt(time, selectedRow, 4);

            saveLessonsToFile();
        });

        removeLessonButton.addActionListener(e -> {
            timerService.resetTimer();

            int selectedRow = lessonsTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(LessonsPanel, "Please select a row to remove.");
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    LessonsPanel,
                    "Are you sure you want to remove this lesson?",
                    "Remove Lesson",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                saveLessonsToFile();
            }
        });

        lessonsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = lessonsTable.getSelectedRow();

                if (selectedRow != -1) {
                    groupComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 0).toString());
                    teacherComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 1).toString());
                    audienceComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                    dayComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 3).toString());
                    lessonTimeComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
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

            Window currentWindow = SwingUtilities.getWindowAncestor(LessonsPanel);
            if (currentWindow != null) {
                currentWindow.dispose();
            }
        });
    }

    public JPanel getRootPanel() {
        return LessonsPanel;
    }

    private boolean validateInput(String group, String teacher, String audience, String day, String time) {

        if (group == null || group.isEmpty()) {
            JOptionPane.showMessageDialog(LessonsPanel, "Select group.");
            return false;
        }else if (teacher == null || teacher.isEmpty()) {
            JOptionPane.showMessageDialog(LessonsPanel, "Select teacher.");
            return false;
        }else if (audience == null || audience.isEmpty()) {
            JOptionPane.showMessageDialog(LessonsPanel, "Select audience.");
            return false;
        }else if (day == null || day.isEmpty()) {
            JOptionPane.showMessageDialog(LessonsPanel, "Select day.");
            return false;
        }else if (time == null || time.isEmpty()) {
            JOptionPane.showMessageDialog(LessonsPanel, "Select time.");
            return false;
        }

        return true;
    }
    private String findLessonConflict(String group, String teacher, String audience,
                                      String day, String time, int excludedRow) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (i == excludedRow) continue;

            String existingGroup = tableModel.getValueAt(i, 0).toString();
            String existingTeacher = tableModel.getValueAt(i, 1).toString();
            String existingAudience = tableModel.getValueAt(i, 2).toString();
            String existingDay = tableModel.getValueAt(i, 3).toString();
            String existingTime = tableModel.getValueAt(i, 4).toString();

            boolean sameSlot = existingDay.equals(day) && existingTime.equals(time);

            if (sameSlot) {
                if (existingGroup.equals(group)) {
                    return "This group already has a lesson at the selected day and time.";
                }
                if (existingTeacher.equals(teacher)) {
                    return "This teacher already has a lesson at the selected day and time.";
                }
                if (existingAudience.equals(audience)) {
                    return "This audience is already occupied at the selected day and time.";
                }
            }
        }
        return null;
    }
    private int getStudentsCountForGroup(String selectedGroup) {
        List<String> lines = FileService.loadFromCsv("groups.csv", LessonsPanel);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                String groupCode = parts[0].trim();
                String groupNumber = parts[1].trim();
                int studentsCount = Integer.parseInt(parts[2].trim());

                String fullGroupName = groupCode + "-" + groupNumber;

                if (fullGroupName.equals(selectedGroup)) {
                    return studentsCount;
                }
            }
        }
        return -1;
    }
    private int getSeatsCountForAudience(String selectedAudience) {
        List<String> lines = FileService.loadFromCsv("audiences.csv", LessonsPanel);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length >= 2) {
                String audienceNumber = parts[0].trim();
                int seatsCount = Integer.parseInt(parts[1].trim());

                if (selectedAudience.startsWith(audienceNumber + " ")) {
                    return seatsCount;
                }
            }
        }
        return -1;
    }
    private boolean checkAudienceCapacity(String group, String audience) {
        int studentsCount = getStudentsCountForGroup(group);
        int seatsCount = getSeatsCountForAudience(audience);

        if (studentsCount == -1 || seatsCount == -1) {
            JOptionPane.showMessageDialog(
                    LessonsPanel,
                    "Cannot determine group size or audience capacity."
            );
            return false;
        }

        if (studentsCount > seatsCount) {
            JOptionPane.showMessageDialog(
                    LessonsPanel,
                    "Selected audience has only " + seatsCount +
                            " seats, but group has " + studentsCount + " students."
            );
            return false;
        }

        return true;
    }
    private void saveLessonsToFile() {
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String line = tableModel.getValueAt(i, 0) + "," +
                    tableModel.getValueAt(i, 1) + "," +
                    tableModel.getValueAt(i, 2) + "," +
                    tableModel.getValueAt(i, 3) + "," +
                    tableModel.getValueAt(i, 4);

            lines.add(line);
        }

        FileService.saveToCsv("lessons.csv", lines, LessonsPanel);
    }

    private void loadLessonsFromFile() {
        List<String> lines = FileService.loadFromCsv("lessons.csv", LessonsPanel);

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 5) {
                try {
                    tableModel.addRow(new Object[]{
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim()
                    });
                }catch (NumberFormatException ignored) {

                }
            }
        }
    }
    private void loadGroupsToComboBox() {
        groupComboBox.removeAllItems();

        List<String> lines = FileService.loadFromCsv("groups.csv", LessonsPanel);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                String groupCode = parts[0].trim();
                String groupNumber = parts[1].trim();

                groupComboBox.addItem(groupCode + "-" + groupNumber);
            }
        }
    }
    private void loadTeachersToComboBox() {
        teacherComboBox.removeAllItems();

        List<String> lines = FileService.loadFromCsv("teachers.csv", LessonsPanel);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                String firstName = parts[0].trim();
                String lastName = parts[1].trim();

                teacherComboBox.addItem(firstName + " " + lastName);
            }
        }
    }
    private void loadAudiencesToComboBox() {
        audienceComboBox.removeAllItems();

        List<String> lines = FileService.loadFromCsv("audiences.csv", LessonsPanel);

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                String audienceNumber = parts[0].trim();
                String seats = parts[1].trim();
                String type = parts[2].trim();

                audienceComboBox.addItem(audienceNumber + " (" + seats + " seats" + ")");
            }
        }
    }
    private void loadStaticComboBoxes() {
        dayComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        }));
        lessonTimeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "1 (08:00 - 09:30)",
                "2 (09:40 - 11:10)",
                "3 (11:20 - 12:50)",
                "4 (13:10 - 14:40)",
                "5 (14:50 - 16:20)",
                "6 (16:30 - 18:00)",
                "7 (18:10 - 19:40)",
        }));
    }
}