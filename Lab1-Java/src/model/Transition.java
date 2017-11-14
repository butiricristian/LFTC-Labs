package model;

/**
 * The class for representing the transition
 */
public class Transition {

    /**
     * Name of the from state
     */
    private String state1Name;
    /**
     * Name of the in state
     */
    private String state2Name;
    /**
     * Accepted values
     */
    private String value;

    public Transition(String state1Name, String state2Name, String value) {
        this.state1Name = state1Name;
        this.state2Name = state2Name;
        this.value = value;
    }

    public String getState1Name() {
        return state1Name;
    }

    public void setState1Name(String state1Name) {
        this.state1Name = state1Name;
    }

    public String getState2Name() {
        return state2Name;
    }

    public void setState2Name(String state2Name) {
        this.state2Name = state2Name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}