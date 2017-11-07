package com.company.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for representing the state
 */
public class State {
    /**
     * The name of the state
     */
    private String name;
    /**
     * Checks if the state is initial
     */
    private Boolean isInitial;
    /**
     * Checks if the state is final
     */
    private Boolean isFinal;
    /**
     * List for all the transition that exit the state
     */
    private List<Transition> outTransitions;

    public State(String name, Boolean isInitial, Boolean isFinal) {
        this.name = name;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        this.outTransitions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInitial(Boolean initial) {
        isInitial = initial;
    }

    public void setFinal(Boolean aFinal) {
        isFinal = aFinal;
    }

    public List<Transition> getOutTransitions() {
        return outTransitions;
    }

    public void setOutTransitions(List<Transition> outTransitions) {
        this.outTransitions = outTransitions;
    }

    public boolean isInitial(){
        return isInitial;
    }

    public boolean isFinal(){
        return isFinal;
    }


}
