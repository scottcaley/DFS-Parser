import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tokenizer {

    private static final String RULE = "::=";
    private static final String OR = "|";

    //returns file data as a string builder variable
    private static StringBuilder fileChars(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            return sb;

        } catch (IOException e) {
            System.err.println("bad file name");
            System.exit(1);
            return null;
        }
    }

    //key is the word at the start of the line, productionWords list is the strings of words separated by "|"
    private static List<List<String>> productionsOfLine(List<String> line) {
        List<List<String>> productions = new LinkedList<>();

        List<String> production = new LinkedList<>();
        for (int i = 0; i < line.size(); i++) {
            String symbol = line.get(i);
            if (!symbol.equals(OR)) {
                production.add(symbol);
            } else { //don't check for non-empty, empty string is valid
                productions.add(production);
                production = new LinkedList<>();
            }
        }

        productions.add(production);

        return productions;
    }

    //map each non-terminal string to its lists of candidate productions
    public static Map<String, List<List<String>>> productionWords(List<String> words) {
        Map<String, List<List<String>>> productions = new HashMap<>();
        
        int nonTerminalIndex = 0;
        while (nonTerminalIndex < words.size()) {
            int nextNonTerminalIndex = words.subList(nonTerminalIndex + 2, words.size()).indexOf(RULE) + (nonTerminalIndex + 2) - 1;
            if (nextNonTerminalIndex == nonTerminalIndex) nextNonTerminalIndex = words.size(); // for end case

            String nonTerminal = words.get(nonTerminalIndex);
            List<List<String>> nonTerminalProductions = productionsOfLine(words.subList(nonTerminalIndex + 2, nextNonTerminalIndex));
            productions.put(nonTerminal, nonTerminalProductions);

            nonTerminalIndex = nextNonTerminalIndex;
        }

        return productions;
    }

    public static List<String> words(String filename) {
        StringBuilder chars = fileChars(filename);
        List<String> words = new LinkedList<>();

        String word = "";
        for (int i = 0; i < chars.length(); i++) {
            char ch = chars.charAt(i);

            if (!Character.isWhitespace(ch)) {
                word += ch;
            } else {
                if (!word.isEmpty()) {
                    words.add(word);
                    word = "";
                }
            }
        }
        //last character is \n so whitespace check will get the last word in the last

        return words;
    }

    private static List<String> findTokens(String word, Set<String> language) {
        List<String> tokens = new LinkedList<>();

        int beginIndex = 0;
        while (beginIndex < word.length()) {
            int endIndex = word.length();
            while (!language.contains(word.substring(beginIndex, endIndex))) {
                endIndex--;
                if (beginIndex == endIndex) {
                    System.err.println("Word not in language.");
                    System.exit(1);
                }
            }

            tokens.add(word.substring(beginIndex, endIndex));
            beginIndex = endIndex;
        }

        return tokens;
    }

    public static List<String> tokens(String filename, Set<String> language) {
        List<String> words = words(filename);

        List<String> tokens = new LinkedList<>();
        for (String word : words) {
            tokens.addAll(findTokens(word, language));
        }
        return tokens;
    }

}