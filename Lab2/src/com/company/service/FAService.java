package com.company.service;

import com.company.model.State;
import com.company.model.Transition;
import com.company.util.ReadFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FAService {
    List<State> states;
    List<String> alphabet;

    public FAService() {
        states = new ArrayList<>();
        alphabet = new ArrayList<>();
        initStates();
        initTransitions();
        initAlphabet();
    }

    /**
     * Function for reading all the states from the file
     */
    private void initStates(){
        List<String> lines = ReadFile.readLines("states.txt");
        for(String line: lines){
            String[] elements = line.split("\\|");
            states.add(new State(elements[0], elements[1].equals("true"), elements[2].equals("true")));
        }
    }

    /**
     * Function for reading all the transitions from the file
     */
    private void initTransitions(){
        List<String> lines = ReadFile.readLines("transitions.txt");
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
        List<String> lines = ReadFile.readLines("alphabet.txt");
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
}
