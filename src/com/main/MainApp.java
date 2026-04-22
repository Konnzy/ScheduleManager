package com.main;


import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    public MainApp() {
        setTitle("Schedule");
        setMinimumSize(new Dimension(720, 480));
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage("src/com/images/icon.png"));
        setContentPane(new MainForm().getRootPanel());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new MainApp().setVisible(true);
    }
}
