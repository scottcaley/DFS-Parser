import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DecisionTree {

    public static final String EMPTY = "EMPTY";

    public final String decision;
    public final List<DecisionTree> nextTrees;

    public DecisionTree(final List<List<String>> decisionLists) {
        this(EMPTY, decisionLists); //top of the tree should always be empty
    }

    //decision lists are each a string of symbols that could be chosen at this point in the decision tree
    private DecisionTree(final String root, final List<List<String>> decisionLists) {
        this.decision = root;
        nextTrees = new ArrayList<>();

        //find empty lists and remove them, deal with that at the end
        boolean containsEmpty = false;
        List<List<String>> nonEmptyDecisionLists = new LinkedList<>();
        for (List<String> decisionList : decisionLists) {
            if (decisionList.isEmpty() || decisionList.get(0).equals(EMPTY)) {
                containsEmpty = true;
            } else {
                nonEmptyDecisionLists.add(decisionList);
            }
        }

        //each decision (child) is based off the first element
        Map<String, List<List<String>>> decisionListsByFirst = decisionListsByFirst(nonEmptyDecisionLists);
        for (String first : decisionListsByFirst.keySet()) {
            List<List<String>> remainderLists = new LinkedList<>();
            for (List<String> decisionList : decisionListsByFirst.get(first)) {
                List<String> remainderList = decisionList.subList(1, decisionList.size());
                remainderLists.add(remainderList);
            }
            DecisionTree nextTree = new DecisionTree(first, remainderLists);
            nextTrees.add(nextTree);
        }

        //empty element should be the last choice to prevent short-circuiting
        if (containsEmpty) {
            DecisionTree emptyTree = new DecisionTree(EMPTY, new LinkedList<>());
            nextTrees.add(emptyTree);
        }
    }
        
    //sorts lists
    private Map<String, List<List<String>>> decisionListsByFirst(final List<List<String>> decisionLists) {
        Map<String, List<List<String>>> decisionListsByFirst = new HashMap<>();

        for (List<String> decisionList : decisionLists) {
            String first = decisionList.get(0);

            List<List<String>> decisionListsOfFirst;
            if (decisionListsByFirst.containsKey(first)) {
                decisionListsOfFirst = decisionListsByFirst.get(first);
            } else {
                decisionListsOfFirst = new LinkedList<>();
                decisionListsByFirst.put(first, decisionListsOfFirst);
            }

            decisionListsOfFirst.add(decisionList);
        }

        return decisionListsByFirst;
    }

    //set of all node values in this tree
    public Set<String> decisionSet() {
        Set<String> decisions = new HashSet<>();
        if (!decision.equals(EMPTY)) {
            decisions.add(decision);
        }

        for (DecisionTree decisionTree : nextTrees) {
            decisions.addAll(decisionTree.decisionSet());
        }

        return decisions;
    }

    //builds a tree lookin string for this tree
    //i-th element contains the number of children remaining for the i-th element of the sequence to arrive at this tree
    private StringBuilder treeVisualization(List<Integer> genData) {
        StringBuilder rep = new StringBuilder();

        for (int i = 0; i < genData.size(); i++) {
            int numSiblingsRemaining = genData.get(i);
            if (numSiblingsRemaining > 0) {
                if (i < genData.size() - 1) {
                    rep.append('|');
                } else {
                    rep.append('•');
                    genData.set(i, numSiblingsRemaining - 1);
                }
            } else {
                rep.append(' ');
            }
            rep.append(' ');
        }
        rep.append(decision);
        rep.append('\n');

        genData.add(nextTrees.size());
        for (int i = 0; i < nextTrees.size(); i++) {
            rep.append(nextTrees.get(i).treeVisualization(genData));
        }
        genData.remove(genData.size() - 1);

        return rep;
    }

    @Override
    public String toString() {
        return treeVisualization(new LinkedList<>()).toString();
    }

}