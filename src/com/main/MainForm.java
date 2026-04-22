package com.main;

import com.service.InactivityTimerService;

import javax.swing.*;
import java.awt.*;
import com.forms.*;

public class MainForm {
    private JButton showScheduleButton;
    private JPanel MainPanel;
    private JButton manageAudiencesButton;
    private JButton manageTeachersButton;
    private JButton manageGroupsButton;
    private JButton manageLessonButton;
    private JPanel buttonsPanel;
    private JButton exitButton;
    private JLabel dateTimeLabel;
    private JLabel statusLabel;
    private JLabel mainMenuLabel;
    private JLabel scheduleSystemLabel;
    private InactivityTimerService timerService;

    public MainForm() {
        timerService = new InactivityTimerService(statusLabel, dateTimeLabel, MainPanel);
        timerService.startTimer();

        showScheduleButton.addActionListener(e -> {
            timerService.resetTimer();
            timerService.stopTimer();

            JFrame frame = new JFrame("Schedule");
            frame.setContentPane(new ScheduleForm().getRootPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/com/images/icon.png"));
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Window currentWindow = SwingUtilities.getWindowAncestor(MainPanel);
            currentWindow.dispose();
        });
        manageLessonButton.addActionListener(e -> {
            timerService.resetTimer();
            timerService.stopTimer();

            JFrame frame = new JFrame("Schedule");
            frame.setContentPane(new LessonsForm().getRootPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/com/images/icon.png"));
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Window currentWindow = SwingUtilities.getWindowAncestor(MainPanel);
            currentWindow.dispose();
        });

        manageAudiencesButton.addActionListener(e -> {
            timerService.resetTimer();
            timerService.stopTimer();

            JFrame frame = new JFrame("Schedule");
            frame.setContentPane(new AudiencesForm().getRootPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/com/images/icon.png"));
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Window currentWindow = SwingUtilities.getWindowAncestor(MainPanel);
            currentWindow.dispose();
        });

        manageTeachersButton.addActionListener(e -> {
            timerService.resetTimer();
            timerService.stopTimer();

            JFrame frame = new JFrame("Schedule");
            frame.setContentPane(new TeachersForm().getRootPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/com/images/icon.png"));
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Window currentWindow = SwingUtilities.getWindowAncestor(MainPanel);
            currentWindow.dispose();
        });

        manageGroupsButton.addActionListener(e -> {
            timerService.resetTimer();
            timerService.stopTimer();

            JFrame frame = new JFrame("Schedule");
            frame.setContentPane(new GroupsForm().getRootPanel());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/com/images/icon.png"));
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            Window currentWindow = SwingUtilities.getWindowAncestor(MainPanel);
            currentWindow.dispose();
        });

        exitButton.addActionListener(e -> {
            timerService.resetTimer();
            timerService.stopTimer();

            int result = JOptionPane.showConfirmDialog(
                    MainPanel,
                    "Are you sure you want to exit?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    public JPanel getRootPanel() {
        return MainPanel;
    }


}
