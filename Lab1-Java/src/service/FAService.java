package service;

import model.State;
import model.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FAService {
    List<State> states;
    List<String> alphabet;
    String statesFile, transFile, alphabetFile;

    public FAService(String statesFile, String transFile, String alphabetFile) {
        states = new ArrayList<>();
        alphabet = new ArrayList<>();

        this.statesFile = statesFile;
        this.alphabetFile = alphabetFile;
        this.transFile = transFile;

        initStates();
        initTransitions();
        initAlphabet();
    }

    /**
     * Function for reading all the states from the file
     */
    private void initStates(){
        List<String> lines = ReadFile.readLines(statesFile);
        for(String line: lines){
            String[] elements = line.split("\\|");
            states.add(new State(elements[0], elements[1].equals("true"), elements[2].equals("true")));
        }
    }

    /**
     * Function for reading all the transitions from the file
     */
    private void initTransitions(){
        List<String> lines = ReadFile.readLines(transFile);
        for(String line: lines){
            String[] elements = line.split("\\|");
            Transition t = new Transition(elements[0], elements[1], elements[2]);
            getStateByName(elements[0]).ifPresent(s -> s.getOutTransitions().add(t));
        }
    }

    /**
     * Function for reading the alphabet from the file
     */
    private void initAlphabet(){
        List<String> lines = ReadFile.readLines(alphabetFile);
        alphabet.addAll(lines);
    }

    /**
     * Function for getting a state by its name
     * @param stateName - the name of the state
     * @return - optional wrapper with the state, if it is found, or null otherwise
     */
    public Optional<State> getStateByName(String stateName){
        return states.stream().filter(s -> s.getName().equals(stateName)).findFirst();
    }

    /**
     * Function for getting the initial state
     * @return - optional wrapper with the state, if it is found, or null otherwise
     */
    public Optional<State> getInitialState(){
        return states.stream().filter(s -> s.isInitial()).findFirst();
    }

    /**
     * Function for getting the initial state
     * @return - optional wrapper with the state, if it is found, or null otherwise
     */
    public List<State> getFinalStates(){
        return states.stream().filter(s -> s.isFinal()).collect(Collectors.toList());
    }

    /**
     * Function for getting all the states
     * @return - all the states
     */
    public List<State> getStates() {
        return states;
    }

    /**
     * Function for getting the alphabet
     * @return - the alphabet
     */
    public List<String> getAlphabet() {
        return alphabet;
    }

    /**
     * Function for checking if a sequence is accepted by the automaton
     * @param sequence - the sequence to check
     * @return - true if the sequence is accepted or false otherwise
     */
    public boolean checkIfSequenceIsAccepted(String sequence) {
        State currentState = this.getInitialState().get();
        for(int i=0;i<sequence.length();i++){
            String letter = String.valueOf(sequence.charAt(i));
            Optional<Transition> transition = currentState.getOutTransitions()
                    .stream()
                    .filter(t -> t.getValue().contains(letter))
                    .findFirst();
            if(transition.isPresent()){
                currentState = this.getStateByName(transition.get().getState2Name()).get();
            }
            else{
                return false;
            }
        }
        return currentState.isFinal();
    }

    /**
     * Function for printing the longest accepted prefix of a given sequence
     * @return - optional with the prefix, if it exists, or null otherwise
     */
    public Optional<String> longestSequence(String sequence) {
        for(int i=sequence.length();i>=0;i--){
            String prefix = sequence.substring(0, i);
            if(checkIfSequenceIsAccepted(prefix)){
                return Optional.of(prefix);
            }
        }
        return Optional.empty();
    }
}
