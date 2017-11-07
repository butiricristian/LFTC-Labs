package com.company.ui;

import com.company.model.State;
import com.company.model.Transition;
import com.company.service.FAService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserInterface {
    FAService faService;
    Scanner s;

    public UserInterface() {
        this.faService = new FAService();
        s = new Scanner(System.in);
    }

    /**
     * Main function of the UI
     */
    public void start(){
        boolean exit = false;
        while(!exit){
            printMenu();
            String option = s.next();
            switch(option){
                case "1":
                    printStates();
                    break;
                case "2":
                    printTransitions();
                    break;
                case "3":
                    printAlphabet();
                    break;
                case "4":
                    printFinalStates();
                    break;
                case "5":
                    if(checkSequence()){
                        System.out.println("Sequence accepted.");
                    }
                    else{
                        System.out.println("Sequence not accepted!");
                    }
                    break;
                case "6":
                    Optional<String> prefix = longestSequence();
                    if(prefix.isPresent()){
                        System.out.println(prefix.get());
                    }
                    else{
                        System.out.println("No subsequence is accepted!");
                    }
                    break;
                case "0":
                    exit = true;
                    break;
                default:
                    System.out.println("This is not a valid option!");
            }
        }
    }

    /**
     * Function for printing all the final states
     */
    private void printFinalStates() {
        List<State> finalStates = faService.getFinalStates();
        for(State s: finalStates){
            System.out.println(s.getName() + ", ");
        }
    }

    /**
     * Function for printing the longest accepted prefix of a given sequence
     * @return - optional with the prefix, if it exists, or null otherwise
     */
    private Optional<String> longestSequence() {
        System.out.print("Enter the sequence: ");
        String sequence = s.next();
        for(int i=sequence.length();i>=0;i--){
            String prefix = sequence.substring(0, i);
            if(checkIfSequenceIsAccepted(prefix)){
                return Optional.of(prefix);
            }
        }
        return Optional.empty();
    }

    /**
     * Function for reading a sequence and checking if it is accepted by the automaton
     * @return - true if the sequence is accepted or false otherwise
     */
    private boolean checkSequence() {
        System.out.print("Enter the sequence: ");
        String sequence = s.next();
        return checkIfSequenceIsAccepted(sequence);
    }

    /**
     * Function for checking if a sequence is accepted by the automaton
     * @param sequence - the sequence to check
     * @return - true if the sequence is accepted or false otherwise
     */
    private boolean checkIfSequenceIsAccepted(String sequence) {
        State currentState = faService.getInitialState().get();
        for(int i=0;i<sequence.length();i++){
            String letter = String.valueOf(sequence.charAt(i));
            Optional<Transition> transition = currentState.getOutTransitions()
                    .stream()
                    .filter(t -> t.getValue().contains(letter))
                    .findFirst();
            if(transition.isPresent()){
                currentState = faService.getStateByName(transition.get().getState2Name()).get();
            }
            else{
                return false;
            }
        }
        return currentState.isFinal();
    }

    /**
     * Function for printing the alphabet
     */
    private void printAlphabet() {
        faService.getAlphabet().forEach(System.out::println);
    }

    /**
     * Function for printing all the transitions
     */
    private void printTransitions() {
        List<State> states = faService.getStates();
        for(State s: states) {
            List<Transition> transitions = s.getOutTransitions();
            for(Transition t: transitions) {
                System.out.println("(" + t.getState1Name() + " -> " + t.getState2Name() + ", alphabet: " + t.getValue());
            }
        }
    }

    /**
     * Function for printing all the states
     */
    private void printStates() {
        List<State> states = faService.getStates();
        for(State s: states) {
            System.out.println("(" + s.getName() + ", " + "initial: " + s.isInitial() + ", final: " + s.isFinal() + ")");
        }
    }

    /**
     * Function for printing the menu
     */
    private void printMenu() {
        System.out.println("FINITE AUTOMATON:");
        System.out.println("\t1. Print all the states");
        System.out.println("\t2. Print all the transitions");
        System.out.println("\t3. Print the alphabet");
        System.out.println("\t4. Print all final states");
        System.out.println("\t5. Check if a sequence is accepted by the automaton");
        System.out.println("\t6. Print the longest accepted sequence");
        System.out.println("\t0. Exit");
        System.out.print("Choose option: ");
    }


}
