package service;

import javafx.util.Pair;

import java.util.*;

public class Scanner {

    private TreeMap<String, String> constantsSymbolTable;
    private TreeMap<String, String> variablesSymbolTable;
    private ArrayList<Pair<String, String>> pifTable;
    private HashMap<String, String> codeTable;
    private String declarationsRegex = "int (.*)|real (.*)";
    private List<String> arithmOperations = new ArrayList<>(Arrays.asList("+", "-", "*", "/", "%", "."));
    private List<String> logicalOperations = new ArrayList<>(Arrays.asList("<", ">", "=", "!"));
    private Integer lastCodeTableEntry = 26;
    private Integer lastConstSymbolTableEntry = 2000;
    private Integer lastVariableSymbolTableEntry = 1000;

    public Scanner() {
        constantsSymbolTable = new TreeMap<>();
        variablesSymbolTable = new TreeMap<>();
        pifTable = new ArrayList<>();
        codeTable = new HashMap<>();
    }

    /**
     * The scan method will start the process of creating the tables;
     */
    public void scan() {
        List<String> programLines = ReadFile.readFile("src/resources/program.txt");
        initCodeTable();
        if (programLines != null) {
            for (String line : programLines) {
                line = line.trim();
                if (line.matches("define (.*)")) {
                    try {
                        if (!line.contains(";")) {
                            System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                    + ": Missing semi-colon");
                            return;
                        }
                        manageDefineStmt(line);
                    } catch (Exception e) {
                        System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                + ": " + e.getMessage());
                        return;
                    }
                } else if (line.matches(declarationsRegex)) {
                    try {
                        if (!line.contains(";")) {
                            System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                    + ": Missing semi-colon");
                            return;
                        }
                        manageDeclareStmt(line);
                    } catch (Exception e) {
                        System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                + ": " + e.getMessage());
                        return;
                    }
                } else if (line.matches("(.*)=(.*)") &&
                        !line.matches("(.*)<=(.*)|(.*)>=(.*)|(.*)==(.*)|(.*)!=(.*)")) {
                    try {
                        if (!line.contains(";")) {
                            System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                    + ": Missing semi-colon");
                            return;
                        }
                        manageAssignStmt(line);
                    } catch (Exception e) {
                        System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                + ": " + e.getMessage());
                        return;
                    }
                } else if (line.matches("input\\((.*)") || line.matches("output\\((.*)")) {
                    try {
                        if (!line.contains(";")) {
                            System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                    + ": Missing semi-colon");
                            return;
                        }
                        manageInputOutputStmt(line);
                    } catch (Exception e) {
                        System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                + ": " + e.getMessage());
                        return;
                    }
                } else if (line.matches("while\\((.*)") || line.matches("if\\((.*)")) {
                    try {
                        manageIfAndWhileStmt(line);
                    } catch (Exception e) {
                        System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                                + ": " + e.getMessage());
                        return;
                    }
                } else if (line.matches("^end;$")) {
                    pifTable.add(new Pair<>(codeTable.get("end"), "-"));
                    pifTable.add(new Pair<>(codeTable.get(";"), "-"));
                } else {
                    System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1)
                            + ": This is not a valid statement.");
                    return;
                }

            }
        }
        printTables();
    }

    /**
     * This method processes the if and while statements, adding the necessary keywords, variables
     * and constants to the PIF. The program will not enter here unless the statement begins with:
     * {if(} or {while(}
     *
     * @param line - the actual statement that has to be processed
     */
    private void manageIfAndWhileStmt(String line) throws Exception {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(line.split("\\(|\\)")));
        removeEmptyElements(words);
        pifTable.add(new Pair<>(codeTable.get(words.get(0).trim()), "-"));
        pifTable.add(new Pair<>(codeTable.get("("), "-"));
        manageLogicalExpression(words.get(1).trim());
        pifTable.add(new Pair<>(codeTable.get(")"), "-"));
        pifTable.add(new Pair<>(codeTable.get("begin:"), "-"));
    }

    /**
     * This method processes any logical expression, adding the necessary keywords, variables
     * and constants to the PIF and to the symbol tables. The program will call this method if the
     * statement contains: "<", ">", "<=", ">=", "==", "!="
     *
     * @param words - the actual expression that has to be processed
     */
    private void manageLogicalExpression(String words) throws Exception {
        StringBuilder partialRes = new StringBuilder("");
        for (int i = 0; i < words.length(); i++) {
            if (logicalOperations.contains(String.valueOf(words.charAt(i)))) {
                addConstantOrVariable(partialRes.toString().trim());
                if (String.valueOf(words.charAt(i + 1)).equals("=")) {
                    pifTable.add(new Pair<>(codeTable.get(String.valueOf(words.charAt(i)) +
                            String.valueOf(words.charAt(++i))),
                            "-"));
                } else {
                    pifTable.add(new Pair<>(codeTable.get(String.valueOf(words.charAt(i))),
                            "-"));
                }
                partialRes = new StringBuilder("");
            } else {
                partialRes.append(words.charAt(i));
            }
        }
        addConstantOrVariable(partialRes.toString().trim());
    }

    /**
     * This method processes the input and output statements, adding the necessary keywords, variables
     * and constants to the PIF. The program will enter here if the statement begins with:
     * {input(} or {output(}
     *
     * @param line - the actual statement that has to be processed
     */
    private void manageInputOutputStmt(String line) throws Exception {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(line.split("\\(|\\)|;")));
        removeEmptyElements(words);
        pifTable.add(new Pair<>(codeTable.get(words.get(0).trim()), "-"));
        pifTable.add(new Pair<>(codeTable.get("("), "-"));
        manageArithmeticExpression(words.get(1).trim());
        pifTable.add(new Pair<>(codeTable.get(")"), "-"));
        pifTable.add(new Pair<>(codeTable.get(";"), "-"));
    }

    /**
     * This method processes the assign statement, adding the necessary keywords, variables
     * and constants to the PIF. The program will enter here if the line contains "=" but it is not
     * a logical expression
     *
     * @param line - the actual statement that has to be processed
     */
    private void manageAssignStmt(String line) throws Exception {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(line.split("=|;")));
        pifTable.add(new Pair<>(codeTable.get("id"), variablesSymbolTable.get(words.get(0).trim())));
        pifTable.add(new Pair<>(codeTable.get("="), "-"));
        manageArithmeticExpression(words.get(1).trim());
        pifTable.add(new Pair<>(codeTable.get(";"), "-"));
    }

    /**
     * This method processes the arithmetic expressions, adding the necessary keywords, variables
     * and constants to the PIF and symbol tables.
     *
     * @param words - the actual statement that has to be processed
     */
    private void manageArithmeticExpression(String words) throws Exception {
        StringBuilder partialRes = new StringBuilder("");
        for (int i = 0; i < words.length(); i++) {
            if (arithmOperations.contains(String.valueOf(words.charAt(i)))) {
                addConstantOrVariable(partialRes.toString().trim());
                pifTable.add(new Pair<>(codeTable.get(String.valueOf(words.charAt(i))),
                        "-"));
                partialRes = new StringBuilder("");
            } else {
                partialRes.append(words.charAt(i));
            }
        }
        addConstantOrVariable(partialRes.toString().trim());
    }

    /**
     * This method will check if the value is a constant or a variable and add it to the corresponding
     * symbol table.
     *
     * @param value - the value to be checked
     */
    private void addConstantOrVariable(String value) throws Exception {
        if (variablesSymbolTable.containsKey(value)) {
            pifTable.add(new Pair<>(codeTable.get("id"), variablesSymbolTable.get(value)));
        } else if (value.equals("PI")) {
            constantsSymbolTable.putIfAbsent("PI", String.valueOf(++lastConstSymbolTableEntry));
            pifTable.add(new Pair<>(codeTable.get("id"), constantsSymbolTable.get("PI")));
        } else if (value.matches("[0-9]+")) {
            constantsSymbolTable.putIfAbsent(value, String.valueOf(++lastConstSymbolTableEntry));
            pifTable.add(new Pair<>(codeTable.get("id"), constantsSymbolTable.get(value)));
        } else {
            throw new Exception("Error! Symbol not found");
        }
    }

    /**
     * This method processes the declaration statement, adding the necessary keywords, variables
     * and constants to the PIF. The program will enter here if the line begins with "int ", "real "
     * or a user defined type.
     *
     * @param line - the actual statement that has to be processed
     */
    private void manageDeclareStmt(String line) throws Exception {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(line.split(" |,|;")));
        removeEmptyElements(words);
        pifTable.add(new Pair<>(codeTable.get(words.get(0).trim()), "-"));
        for (int i = 1; i < words.size(); i++) {
            if (words.get(i).length() <= 8) {
                variablesSymbolTable.put(words.get(i), String.valueOf(++lastVariableSymbolTableEntry));
            } else {
                throw new Exception("Variable name longer than 8 characters");
            }
            pifTable.add(new Pair<>(codeTable.get("id"), variablesSymbolTable.get(words.get(i))));
            if (i != words.size() - 1) {
                pifTable.add(new Pair<>(codeTable.get(","), "-"));
            }
        }
        pifTable.add(new Pair<>(codeTable.get(";"), "-"));
    }

    /**
     * This method processes the define statement, adding the necessary keywords, variables
     * and constants to the PIF. The program will enter here if the line contains "define("
     *
     * @param line - the actual statement that has to be processed
     */
    private void manageDefineStmt(String line) throws Exception {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(line.split(" |\\(|\\)|,")));
        removeEmptyElements(words);
        pifTable.add(new Pair<>(codeTable.get("define"), "-"));
        codeTable.put(words.get(1), String.valueOf(++lastCodeTableEntry));
        declarationsRegex += "|" + words.get(1) + " (.*)";
        pifTable.add(new Pair<>(codeTable.get(words.get(1)), "-"));
        pifTable.add(new Pair<>(codeTable.get("("), "-"));
        for (int i = 2; i < words.size() - 1; i += 2) {
            pifTable.add(new Pair<>(codeTable.get(words.get(i)), "-"));
            if (words.get(i + 1).length() <= 8) {
                variablesSymbolTable.put(words.get(i + 1), String.valueOf(++lastVariableSymbolTableEntry));
            } else {
                throw new Exception("Variable name longer than 8 characters");
            }
            pifTable.add(new Pair<>(codeTable.get("id"), variablesSymbolTable.get(words.get(i + 1))));
            if (i != words.size() - 3) {
                pifTable.add(new Pair<>(codeTable.get(","), "-"));
            }
        }
        pifTable.add(new Pair<>(codeTable.get(")"), "-"));
        pifTable.add(new Pair<>(codeTable.get(";"), "-"));
    }

    /**
     * This method removes any empty elements of an array, that might have remained from the split
     *
     * @param array - the array to clean
     */
    private void removeEmptyElements(ArrayList<String> array) {
        List<String> toRemove = new ArrayList<>();
        for (String el : array) {
            if (el.isEmpty()) {
                toRemove.add(el);
            }
        }
        array.removeAll(toRemove);
    }

    /**
     * This method initializes the code table from a file
     */
    private void initCodeTable() {
        List<String> codeLines = ReadFile.readFile("src/resources/code.txt");
        codeLines.forEach(line -> {
            String[] split_line = line.split(" ");
            codeTable.put(split_line[0], split_line[1]);
        });
    }

    /**
     * This method prints all the necessary tables.
     */
    private void printTables() {
        System.out.print("CODE TABLE: {");
        for (String key : codeTable.keySet()) {
            System.out.print(key + ": " + codeTable.get(key) + ", ");
        }
        System.out.println("}");

        System.out.print("PIF: {");
        for (Pair<String, String> el : pifTable) {
            System.out.print(el.getKey() + ": " + el.getValue() + ", ");
        }
        System.out.println("}");

        System.out.print("CONSTANT SYMBOL TABLE: {");
        for (String key : constantsSymbolTable.keySet()) {
            System.out.print(key + ": " + constantsSymbolTable.get(key) + ", ");
        }
        System.out.println("}");

        System.out.print("VARIABLES SYMBOL TABLE: {");
        for (String key : variablesSymbolTable.keySet()) {
            System.out.print(key + ": " + variablesSymbolTable.get(key) + ", ");
        }
        System.out.println("}");
    }
}
