import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMethods {


    static Boolean properName(String s){
        Pattern pattern = Pattern.compile("^[A-Z][a-z]+$");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    static Boolean integer(String s){
        Pattern pattern = Pattern.compile("^[\\+\\-]?[\\d]$|^[1-9\\+\\-]\\d+[\\.]?\\d*$|^[\\+\\-]?0\\.\\d+");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    static Boolean ancestor(String s) {
        Pattern pattern = Pattern.compile("^(father|mother|(great\\s+)*grandfather|(great\\s+)*grandmother)$");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }
        static Boolean palindrome(String s){
            Pattern pattern = Pattern.compile("^(.)(.)(.)(.)(.)\\5\\4\\3\\2\\1$");
            Matcher matcher = pattern.matcher(s);
            return matcher.find();
    }

    static List<String> wordleMatches(List <List <WordleResponse> > responses) throws FileNotFoundException {
        StringBuilder builder = new StringBuilder();
        WordleResponse[] locationLocked = new WordleResponse[5];
        ArrayList<WordleResponse> atWrongLocation = new ArrayList<>();
        ArrayList<WordleResponse> doesNotContain = new ArrayList<>();
        List<Character>[] notAtIndex = new ArrayList[5];
        for (int i = 0; i < 5; i++) {
            notAtIndex[i] = new ArrayList<>();
        }
        
        for(List<WordleResponse> w: responses){
            for (WordleResponse wordleResponse : w) {
                LetterResponse r = wordleResponse.resp;

                switch (r) {
                    case CORRECT_LOCATION:
                        locationLocked[wordleResponse.index] = wordleResponse;
                        break;
                    case WRONG_LOCATION:
                        atWrongLocation.add(wordleResponse);
                        notAtIndex[wordleResponse.index].add(wordleResponse.c);
                        break;
                    case WRONG_LETTER:
                        doesNotContain.add(wordleResponse);
                        notAtIndex[wordleResponse.index].add(wordleResponse.c);
                        break;
                }
            }
        }
        
        builder.append('^');
        builder.append("(?=[a-z]{5}$)");//ensures that the string must be 5 letter characters long
        for (int i = 0; i < 5; i++) {
            if (locationLocked[i] != null) {
                builder.append(locationLocked[i].c);
            } else {
                StringBuilder excludedChars = new StringBuilder();
                for (char c : notAtIndex[i]) {
                    boolean isRequiredElsewhere = false;
                    for (WordleResponse wr : atWrongLocation) {
                        if (wr.c == c) {
                            isRequiredElsewhere = true;
                            break;
                        }
                    }
                    if (!isRequiredElsewhere) {
                        excludedChars.append(c);
                    }
                }
                
                if (excludedChars.length() > 0) {
                    builder.append("[^");
                    builder.append(excludedChars);
                    builder.append("]");
                } else {
                    builder.append("."); // Any character if nothing is excluded
                }
            }
        }
        System.out.println("Regex pattern: " + builder.toString());
        Pattern pattern = Pattern.compile(builder.toString());
        List<String> listOfWords = listOfWords("wordlewords.txt");
        ArrayList<String> matchedWords = new ArrayList<>();
        
        for (String s : listOfWords) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                // Check if word contains all required letters (wrong location responses)
                boolean containsAllRequired = true;
                for (WordleResponse wr : atWrongLocation) {
                    if (!s.contains(String.valueOf(wr.c))) {
                        containsAllRequired = false;
                        break;
                    }
                }
                
                if (containsAllRequired) {
                    boolean containsNoWrongLetters = true;
                    for (WordleResponse wr : doesNotContain) {
                        boolean letterIsForbidden = true;
                        for (WordleResponse correct : locationLocked) {
                            if (correct != null && correct.c == wr.c) {
                                letterIsForbidden = false;
                                break;
                            }
                        }
                        // Check if this letter appears in wrong location responses
                        if (letterIsForbidden) {
                            for (WordleResponse wrongLoc : atWrongLocation) {
                                if (wrongLoc.c == wr.c) {
                                    letterIsForbidden = false;
                                    break;
                                }
                            }
                        }

                        if (letterIsForbidden && s.contains(String.valueOf(wr.c))) {
                            containsNoWrongLetters = false;
                            break;
                        }
                    }
                    if (containsNoWrongLetters) {
                        matchedWords.add(s);
                    }
                }
            }
        }
        System.out.println("Words matching regex: " + matchedWords.size());
        return matchedWords;
    }
//I created this method thinking of a different version of the game where the code knows the actual word, but I real
//this is sopposed to be more of a helper, but I'm leaving the method anyway
    static List<WordleResponse> wordleResponseGenerator(String s, String actual){
        Boolean[] foundAtCorrectLocation = new Boolean[5];
        Boolean[] usedInWord = new Boolean[5];
        List<WordleResponse> responses = new ArrayList<>();

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == actual.charAt(i)) {
                WordleResponse wordleResponse = new WordleResponse(s.charAt(i), i, LetterResponse.CORRECT_LOCATION);
                responses.add(wordleResponse);
                foundAtCorrectLocation[i] = true;
                usedInWord[i] = true;
            }
        }

        for (int i = 0; i < s.length(); i++) {
            if (foundAtCorrectLocation[i] != null) {
                continue;
            }

            boolean foundWrongLocation = false;

            for (int j = 0; j < actual.length(); j++) {
                if (s.charAt(i) == actual.charAt(j) && usedInWord[j] == null) {
                    WordleResponse wordleResponse = new WordleResponse(s.charAt(i), i, LetterResponse.WRONG_LOCATION);
                    responses.add(wordleResponse);
                    usedInWord[j] = true;
                    foundWrongLocation = true;
                    break;
                }
            }

            if (!foundWrongLocation) {
                WordleResponse wordleResponse = new WordleResponse(s.charAt(i), i, LetterResponse.WRONG_LETTER);
                responses.add(wordleResponse);
            }
        }

        return responses;
    }

    static List<WordleResponse> generateWordleResponses(String s){
        ArrayList<WordleResponse> responses = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter 1 if letter is wrong:\n" +
                "enter 2 if letter is correct but in wrong place.\n" +
                "enter 3 if letter is correct and in right place");
        switch (keyboard.nextInt()) {
            case 1:
                responses.add(new WordleResponse(s.charAt(i), i, LetterResponse.WRONG_LETTER));
                break;
            case 2:
                responses.add(new WordleResponse(s.charAt(i), i, LetterResponse.WRONG_LOCATION));
                break;
            case 3:
                responses.add(new WordleResponse(s.charAt(i), i, LetterResponse.CORRECT_LOCATION));
                break;
            }
        }
        return responses;
    }

//    A method I made to store all the possible wordle words, which I copied from GitHub and pasted into a file named "wordlewords.txt"
    public static ArrayList<String> listOfWords(String filename) throws FileNotFoundException {
        ArrayList<String> words = new ArrayList<>();
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            words.add(scanner.nextLine());
        }
        scanner.close();
        return words;
    }
}
