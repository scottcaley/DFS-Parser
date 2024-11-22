import java.util.LinkedList;
import java.util.List;

class ParseTree {

    private final String root;
    private final List<ParseTree> children;
    private int numTerminals;
    private final boolean isComplete;

    public ParseTree(String filename, Grammar grammar) {
        this(grammar.s, Tokenizer.tokens(filename, grammar.terminals), 0, grammar);
    }

    //symbol: the candidate for the root of the parse tree we are trying to make
    //terminals: string of symbols from input
    //index: index of terminal we are on
    //grammar: contains decision tree info
    private ParseTree(String symbol, List<String> terminals, int index, Grammar grammar) {
        root = symbol;
        numTerminals = 0;
        children = new LinkedList<>();

        if (grammar.productions.containsKey(symbol)) { //if a non-terminal
            DecisionTree decisionTree = grammar.productions.get(symbol);

            isComplete = getNextChildren(decisionTree, terminals, index, grammar);
            for (int i = 0; i < children.size(); i++) {
                numTerminals += children.get(i).numTerminals;
                //recall this will never execute if getNextChildren() produces an empty list and thus numTerminals == 0 will indicate that this production didn't work
            }

        } else if (index < terminals.size()) { //if at leaf node of decision tree

            isComplete = root.equals(terminals.get(index));
            if (isComplete) { //if the correct getCore
                numTerminals++; //important for marking that this constructor was successful, see isComplete()
            } // else we don't have the right getCore and are not on the right path (no correct paths indicates the program has an error), signified by numTerminals == 0

        } else { //token stream finished
            isComplete = symbol.equals(DecisionTree.EMPTY); //complete if token stream is supposed to be finished
        }
    }

    //attempt to generate list of parse tree children
    //decisionTree: root is the decision we just made, must use next decisions
    //terminals: string of symbols from input
    //index: index of terminal we are on
    //grammar: contains decision tree info
    //returns true if all children are successfully obtained
    private boolean getNextChildren(DecisionTree decisionTree, List<String> terminals, int index, Grammar grammar) {
        List<DecisionTree> nextDecisions = decisionTree.nextTrees;

        for (int i = 0; i < nextDecisions.size(); i++) {
            DecisionTree nextDecisionTree = nextDecisions.get(i);
            String symbol = nextDecisionTree.decision;
            if (symbol.equals(DecisionTree.EMPTY)) {
                return true; //found the end of a path
            }

            ParseTree nextStringElement = new ParseTree(symbol, terminals, index, grammar);

            if (nextStringElement.isComplete) {
                children.add(nextStringElement);

                if (getNextChildren(nextDecisionTree, terminals, index + nextStringElement.numTerminals, grammar)) {
                    //if recursive call is successful, this is a good path
                    return true;
                } else {
                    //else the new string element did not work
                    children.remove(children.size() - 1);
                }
            }
        }
        
        return false; //no solution found
    }

    //builds a tree lookin string for this tree
    //i-th of genData element contains the number of children remaining for the i-th element of the sequence to arrive at this tree
    private StringBuilder treeVisualization(List<Integer> genData) {
        StringBuilder rep = new StringBuilder();

        for (int i = 0; i < genData.size(); i++) {
            int numSiblingsRemaining = genData.get(i);
            if (numSiblingsRemaining >0) {
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
        rep.append(root);
        rep.append('\n');

        genData.add(children.size());
        for (int i = 0; i < children.size(); i++) {
            rep.append(children.get(i).treeVisualization(genData));
        }
        genData.remove(genData.size() - 1);

        return rep;
    }

    @Override
    public String toString() {
        return treeVisualization(new LinkedList<>()).toString();
    }
    
}