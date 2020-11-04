package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public String getFileContent(String filePath) {
        StringBuilder content = new StringBuilder();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while( (line = reader.readLine()) != null) {
                content.append(line.trim()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public List<String> getListFileContent(String filePath) {
        List<String> contents = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while( (line = reader.readLine()) != null) {
                contents.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contents;
    }

    public void writeContentToFile(List<String> list, String fileName) {
        PrintWriter pw;
        try {
            pw = new PrintWriter(fileName);
            list.forEach(pw::println);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
