package service;

import java.util.*;
import java.util.stream.Collectors;

public class Scanner {

    private TreeMap<String, String> constantsSymbolTable;
    private TreeMap<String, String> variablesSymbolTable;
    private LinkedHashMap<String, String> pifTable;
    private HashMap<String, String> codeTable;
    private String declarationsRegex = "int (.*)|real (.*)";
    private Integer lastCodeTableEntry = 26;
    private Integer lastConstSymbolTableEntry = 1000;
    private Integer lastVariableSymbolTableEntry = 1000;

    public Scanner() {
        constantsSymbolTable = new TreeMap<>();
        variablesSymbolTable = new TreeMap<>();
        pifTable = new LinkedHashMap<>();
        codeTable = new HashMap<>();
    }

    /**
     * The scan method will start the process of creating the tables;
     **/
    public void scan() {
        List<String> programLines = ReadFile.readFile("src/resources/program.txt");
        initCodeTable();
        if (programLines != null) {
            for (String line : programLines) {
                if (line.matches("define (.*)")) {
                    manageDefineStmt(line);
                } else if (line.matches(declarationsRegex)) {
                    manageDeclareStmt(line);
                } else if (line.matches("(.*)=(.*)") &&
                        !line.matches("(.*)<=(.*)|(.*)>=(.*)|(.*)==(.*)|(.*)!=(.*)")) {
                    manageAssignStmt(line);
                } else if (line.matches("input\\((.*)")) {
                    manageInputStmt(line);
                } else if (line.matches("output\\((.*)")) {
                    manageOutputStmt(line);
                } else if (line.matches("while\\((.*)")) {
                    manageWhileStmt(line);
                } else if (line.matches("if\\((.*)")) {
                    manageIfStmt(line);
                }
            }
        }
        printTables();
    }

    private void manageIfStmt(String line) {
    }

    private void manageWhileStmt(String line) {
    }

    private void manageOutputStmt(String line) {
    }

    private void manageInputStmt(String line) {
    }

    private void manageAssignStmt(String line) {
    }

    private void manageDeclareStmt(String line) {
    }

    private void manageDefineStmt(String line) {
        String[] words = line.split(" |\\(|\\)|,");
        pifTable.put(codeTable.get("define"), "-");
        codeTable.put(words[1], String.valueOf(++lastCodeTableEntry));
        pifTable.put(codeTable.get(words[1]), "-");
        pifTable.put(codeTable.get("("), "-");
        for(int i=2;i<words.length - 1;i+=2){
            pifTable.put(codeTable.get(words[i]), "-");
            variablesSymbolTable.put(words[i+1], String.valueOf(++lastVariableSymbolTableEntry));
            pifTable.put("0", variablesSymbolTable.get(words[i+1]));
        }
        pifTable.put(codeTable.get(")"), "-");
        pifTable.put(codeTable.get(";"), "-");
    }

    private void initCodeTable() {
        List<String> codeLines = ReadFile.readFile("src/resources/code.txt");
        codeLines.forEach(line -> {
            String[] split_line = line.split(" ");
            codeTable.put(split_line[0], split_line[1]);
        });
    }

    private void printTables(){
        System.out.print("CODE TABLE: {");
        for(String key : codeTable.keySet()){
            System.out.print(key + ": " + codeTable.get(key) + ", ");
        }
        System.out.println("}");

        System.out.print("PIF: {");
        for(String key : pifTable.keySet()){
            System.out.print(key + ": " + pifTable.get(key) + ", ");
        }
        System.out.println("}");

        System.out.print("CONSTANT SYMBOL TABLE: {");
        for(String key : constantsSymbolTable.keySet()){
            System.out.print(key + ": " + constantsSymbolTable.get(key) + ", ");
        }
        System.out.println("}");

        System.out.print("VARIABLES SYMBOL TABLE: {");
        for(String key : variablesSymbolTable.keySet()){
            System.out.print(key + ": " + variablesSymbolTable.get(key) + ", ");
        }
        System.out.println("}");
    }
}
