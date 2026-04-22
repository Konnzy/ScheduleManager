package com.forms;

import com.service.FileService;
import com.service.InactivityTimerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.main.MainForm;

public class ScheduleForm {
    private JPanel SchedulePanel;
    private JLabel manageScheduleLabel;
    private JLabel manageInfoLabel;
    private JLabel statusLabel;
    private JLabel dateTimeLabel;
    private JPanel audienceInfoPanel;
    private JPanel actionPanel;
    private JPanel tableListPanel;
    private JLabel filtersInfoLabel;
    private JButton filterButton;
    private JButton clearFilterButton;
    private JButton backButton;
    private JTable schedulesTable;
    private JScrollPane tableScrollPanel;
    private JLabel actionsLabel;
    private JLabel scheduleListLabel;
    private JLabel groupLabel;
    private JComboBox groupComboBox;
    private JLabel dayLabel;
    private JComboBox dayComboBox;

    private DefaultTableModel tableModel;
    private InactivityTimerService timerService;

    public ScheduleForm() {
        timerService = new InactivityTimerService(statusLabel, dateTimeLabel, SchedulePanel);
        timerService.startTimer();


        tableModel = new DefaultTableModel(
                new Object[]{"Group", "Teacher", "Audience", "Day", "Time"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        schedulesTable.setModel(tableModel);
        schedulesTable.setRowHeight(25);
        schedulesTable.setFillsViewportHeight(true);
        schedulesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        schedulesTable.getTableHeader().setReorderingAllowed(false);


        loadGroupsToComboBox();
        loadStaticComboBoxes();
        loadLessonsFromFile();

        filterButton.addActionListener(e -> {
            timerService.resetTimer();

            String selectedGroup = groupComboBox.getSelectedItem().toString();
            String selectedDay = dayComboBox.getSelectedItem().toString();

            tableModel.setRowCount(0);

            List<String> lines = FileService.loadFromCsv("lessons.csv", SchedulePanel);

            for (String line : lines) {
                String[] parts = line.split(",");

                if (parts.length == 5) {
                    String group = parts[0].trim();
                    String teacher = parts[1].trim();
                    String audience = parts[2].trim();
                    String day = parts[3].trim();
                    String time = parts[4].trim();

                    boolean matchGroup = selectedGroup.equals("All Groups") || group.equals(selectedGroup);
                    boolean matchDay = selectedDay.equals("All Days") || day.equals(selectedDay);

                    if (matchGroup && matchDay) {
                        tableModel.addRow(new Object[]{group, teacher, audience, day, time});
                    }
                }
            }

        });

        clearFilterButton.addActionListener(e -> {
            timerService.resetTimer();

            groupComboBox.setSelectedIndex(0);
            dayComboBox.setSelectedIndex(0);

            tableModel.setRowCount(0);
            loadLessonsFromFile();
        });

        schedulesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = schedulesTable.getSelectedRow();

                if (selectedRow != -1) {
                    groupComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 0).toString());
                    dayComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 3).toString());
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

            Window currentWindow = SwingUtilities.getWindowAncestor(SchedulePanel);
            if (currentWindow != null) {
                currentWindow.dispose();
            }
        });
    }

    public JPanel getRootPanel() {
        return SchedulePanel;
    }

    private void loadLessonsFromFile() {
        List<String> lines = FileService.loadFromCsv("lessons.csv", SchedulePanel);

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

        List<String> lines = FileService.loadFromCsv("groups.csv", SchedulePanel);
        groupComboBox.addItem("All Groups");

        for (String line : lines) {
            String[] parts = line.split(",");

            if (parts.length == 3) {
                String groupCode = parts[0].trim();
                String groupNumber = parts[1].trim();

                groupComboBox.addItem(groupCode + "-" + groupNumber);
            }
        }
    }
    private void loadStaticComboBoxes() {
        dayComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "All Days","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        }));
    }
}