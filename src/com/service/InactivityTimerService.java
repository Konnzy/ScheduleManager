package com.service;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
public class InactivityTimerService {
    private final JLabel statusLabel;
    private final JLabel dateTimeLabel;
    private final Component parentComponent;

    private int inactivitySeconds = 0;
    private Timer timer;

    public InactivityTimerService(JLabel statusLabel, JLabel dateTimeLabel, Component parentComponent) {
        this.statusLabel = statusLabel;
        this.dateTimeLabel = dateTimeLabel;
        this.parentComponent = parentComponent;
    }

    public void startTimer() {
        timer = new Timer(1000, e -> {
            inactivitySeconds++;

            dateTimeLabel.setText(
                    "Date: " + LocalDate.now() +
                            " | Time: " + LocalTime.now().withNano(0)
            );

            if (inactivitySeconds >= 15 && inactivitySeconds < 30) {
                statusLabel.setText("Status: Warning");
                statusLabel.setForeground(new Color(128, 85, 14));
            } else if (inactivitySeconds >= 30) {
                statusLabel.setText("Status: Inactive");
                statusLabel.setForeground(Color.RED);

                timer.stop();
                JOptionPane.showMessageDialog(
                        parentComponent,
                        "No activity detected. Application will close."
                );
                System.exit(0);
            }
        });

        timer.start();

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> resetTimer(),
                AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    public void resetTimer() {
        inactivitySeconds = 0;
        statusLabel.setText("Status: Ready");
        statusLabel.setForeground(new Color(2, 114, 17));
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
}
