package util;

import java.io.*;

public class FileUtil {
    // Path to your util folder
    private static final String BASE_PATH = "D:\\new_project\\src\\util\\";

    public static void writeToFile(String fileName, String data) {
        try {
            File file = new File(BASE_PATH + fileName);

            // Create file if it doesn’t exist
            if (!file.exists()) {
                file.createNewFile();
            }

            // Append data
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(data);
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + fileName + ": " + e.getMessage());
        }
    }
}
