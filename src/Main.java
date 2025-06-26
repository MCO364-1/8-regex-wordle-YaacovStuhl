import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        String actual = "actor";
        ArrayList<List<WordleResponse>> listOfResponses = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            Scanner keyboard = new Scanner(System.in);
            System.out.println("Please enter a word: ");
            String userGuess = keyboard.nextLine();
            List<WordleResponse> list = RegexMethods.generateWordleResponses(userGuess);
            listOfResponses.add(list);
            System.out.println(RegexMethods.wordleMatches(listOfResponses));
        }

    }
}
