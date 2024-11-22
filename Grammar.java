import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Grammar {

    public final String s;
    public final Map<String, DecisionTree> productions;
    public final Set<String> terminals;

    public Grammar(String filename) {
        List<String> words = Tokenizer.words(filename);
        s = words.get(0);
        
        Map<String, List<List<String>>> productionWords = Tokenizer.productionWords(words);
        productions = new HashMap<>();
        for (String nonTerminal : productionWords.keySet()) {
            DecisionTree productionTree = new DecisionTree(productionWords.get(nonTerminal));
            productions.put(nonTerminal, productionTree);
        }

        terminals = terminals();
    }

    private Set<String> terminals() {
        Set<String> allTerminals = new HashSet<>();

        for (DecisionTree decisionTree : productions.values()) {
            Set<String> decisionSet = decisionTree.decisionSet();
            Set<String> decisionTerminals = new HashSet<>();

            for (String decision : decisionSet) {
                if (!productions.containsKey(decision)) {
                    decisionTerminals.add(decision);
                }
            }
            allTerminals.addAll(decisionTerminals);
        }

        return allTerminals;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(s);
        sb.append(" production:\n");
        sb.append(productions.get(s).toString());
        sb.append('\n');

        for (String symbol : productions.keySet()) {
            if (!symbol.equals(s)) {
                sb.append(symbol);
                sb.append(" production:\n");
                sb.append(productions.get(symbol).toString());
                sb.append('\n');
            }
        }

        return sb.toString();
    }
}