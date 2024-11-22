import java.io.File;

public class Main {

    public static void main(String[] args) {

        String grammarFile = "Test/Core/grammar.txt";
        String testFile = ""; //full path, doesn't use testFolder
        String testFolder = "Test/Core";
        
        if (!grammarFile.equals("")) {
            System.out.println("Generating grammar from " + grammarFile + "\n");
            Grammar grammar = new Grammar(grammarFile);
            System.out.println(grammar);

            if (!testFile.equals("")) {
                System.out.println("Parsing " + testFile);
                ParseTree parseTree = new ParseTree(testFile, grammar);
                System.out.println(parseTree);
            }

            if (!testFolder.equals("")) {
                String[] partialFilenames = (new File(testFolder)).list();
                for (String partialFilename : partialFilenames) {
                    String filename = testFolder + "/" + partialFilename;

                    if (filename.length() < 11 || !filename.substring(filename.length() - 11, filename.length()).equalsIgnoreCase("grammar.txt")) {
                        System.out.println("Parsing " + filename);
                        ParseTree parseTree = new ParseTree(filename, grammar);
                        System.out.println(parseTree);
                    }
                }
            }
        }

        //ensure output
        System.out.flush();
        try {
            Thread.sleep(1000); // a secondc
        } catch (InterruptedException e) {
        }
    }
}