import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CreateWordleFile {
    public static void main(String[] args) {
        String fileName = "wordlewords.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Optional: Write something in the file to confirm it's working
            writer.write("apple\nbrave\ncrane\ndream\neagle");
            System.out.println("File '" + fileName + "' created successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while creating the file: " + e.getMessage());
        }
    }
}

