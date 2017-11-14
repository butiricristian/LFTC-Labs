package service;


import util.Pair;

import java.util.*;

public class Scanner2 {

    private TreeMap<String, String> constantsSymbolTable;
    private TreeMap<String, String> variablesSymbolTable;
    private ArrayList<Pair<String, String>> pifTable;
    private HashMap<String, String> codeTable;
    private List<Character> separators = new ArrayList<>(Arrays.asList('(', ')', ';', ',', '+', '-', '*', '/', '%', '.', ' ', '<', '>', '=', '!', ':'));
    private Integer lastConstSymbolTableEntry = 2000;
    private Integer lastVariableSymbolTableEntry = 1000;

    private FAService faVariables, faConstants, faKeywords;

    public Scanner2() {
        constantsSymbolTable = new TreeMap<>();
        variablesSymbolTable = new TreeMap<>();
        pifTable = new ArrayList<>();
        codeTable = new HashMap<>();

        faVariables = new FAService("src/resources/varState.txt", "src/resources/varTransitions.txt", "src/resources/varAlphabet.txt");
        faConstants = new FAService("src/resources/constState.txt", "src/resources/constTransitions.txt", "src/resources/constAlphabet.txt");
        faKeywords = new FAService("src/resources/kwState.txt", "src/resources/kwTransitions.txt", "src/resources/kwAlphabet.txt");
    }

    public void scan(){
        initCodeTable();
        List<String> programLines = ReadFile.readFile("src/resources/program.txt");
        if(programLines != null) {
            for (String line : programLines) {
                StringBuilder partialRes = new StringBuilder("");
                for (int i = 0; i < line.length(); i++) {
                    if(separators.contains(line.charAt(i))){
                        if(!partialRes.toString().isEmpty()) {
                            if (codeTable.keySet().contains(partialRes.toString().trim())) {
                                pifTable.add(new Pair<>(codeTable.get(partialRes.toString().trim()), "-"));
                            } else {
                                try {
                                    addConstantOrVariable(partialRes.toString().trim());
                                } catch (Exception e) {
                                    System.out.println("Error on line " + String.valueOf(programLines.indexOf(line) + 1) + ": " + e.getMessage());
                                    return;
                                }
                            }
                        }
                        if(line.charAt(i) != ' ' && line.charAt(i) != '\n') {
                            if (i + 1 < line.length() && line.charAt(i + 1) == '=') {
                                pifTable.add(new Pair<>(codeTable.get(String.valueOf(line.charAt(i)) + String.valueOf(line.charAt(i + 1))), "-"));
                                i++;
                            } else {
                                pifTable.add(new Pair<>(codeTable.get(String.valueOf(line.charAt(i))), "-"));
                            }
                        }
                        partialRes = new StringBuilder("");
                    }
                    else{
                        partialRes.append(line.charAt(i));
                    }
                }
            }
        }
        printTables();
    }

    public void scanWithAutomata(){
        initCodeTable();
        List<String> programLines = ReadFile.readFile("src/resources/program.txt");
        if(programLines != null) {
            for (String line : programLines) {
//                faKeywords.longestSequence(line).ifPresent((prefix) ->
//                    pifTable.add(new Pair<>(
//                            codeTable.get(prefix),
//                            "-"
//                    ))
//                );
                faVariables.longestSequence(line).ifPresent((prefix) -> {
                    try {
                        addConstantOrVariable(prefix);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
//                faConstants.longestSequence(line).ifPresent((prefix) -> {
//                    try {
//                        addConstantOrVariable(prefix);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
            }
        }
        printTables();
    }

    private void addConstantOrVariable(String value) throws Exception {
        if(value.length() <= 8 && value.matches("[a-zA-Z]+") && !value.equals("PI")) {
            variablesSymbolTable.putIfAbsent(value, String.valueOf(++lastVariableSymbolTableEntry));
            pifTable.add(new Pair<>(codeTable.get("id"), variablesSymbolTable.get(value)));
        } else if (value.equals("PI")) {
            constantsSymbolTable.putIfAbsent("PI", String.valueOf(++lastConstSymbolTableEntry));
            pifTable.add(new Pair<>(codeTable.get("id"), constantsSymbolTable.get("PI")));
        } else if (value.matches("[0-9]+")) {
            constantsSymbolTable.putIfAbsent(value, String.valueOf(++lastConstSymbolTableEntry));
            pifTable.add(new Pair<>(codeTable.get("id"), constantsSymbolTable.get(value)));
        } else {
            throw new Exception("Error! Incorrect identifier");
        }
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
