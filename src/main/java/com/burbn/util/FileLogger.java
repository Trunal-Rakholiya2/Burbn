package com.burbn.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 * Utility for persisting system activity logs to disk.
 */
public class FileLogger {
    private static final String DATA_DIRECTORY = "data";

    public static void log(String fileName, String data) {
        try {
            File dir = new File(DATA_DIRECTORY);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println("[" + LocalDateTime.now() + "] " + data);
            }
        } catch (IOException e) {
            System.err.println("[FileLogger Error] Failed writing to " + fileName + ": " + e.getMessage());
        }
    }
}
