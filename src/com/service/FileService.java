package com.service;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class FileService {

    private static final String DATA_FOLDER = "data";

    public static void ensureDataFolderExists() {
        File dir = new File(DATA_FOLDER);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static void saveToCsv(String fileName, List<String> lines, JPanel parentPanel) {
        ensureDataFolderExists();

        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FOLDER + "/" + fileName))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentPanel,
                    "Error saving file: " + fileName);
        }
    }

    public static List<String> loadFromCsv(String fileName, JPanel parentPanel) {
        ensureDataFolderExists();

        List<String> lines = new ArrayList<>();
        File file = new File(DATA_FOLDER + "/" + fileName);

        if (!file.exists()) {
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parentPanel,
                    "Error loading file: " + fileName);
        }

        return lines;
    }

}