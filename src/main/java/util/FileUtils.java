package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public String getFileContent(String filePath) {
        StringBuilder content = new StringBuilder();
        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while( (line = reader.readLine()) != null) {
                content.append(line.trim()).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public List<String> getListFileContent(String filePath) {
        List<String> contents = new ArrayList<>();
        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            while( (line = reader.readLine()) != null) {
                contents.add(line.trim());
            }
            reader.close();
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
