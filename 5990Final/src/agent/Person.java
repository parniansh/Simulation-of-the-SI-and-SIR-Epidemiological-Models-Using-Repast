package agent;

public class Person {
    private String state; // "S", "I", or "R"

    public Person() {
        this.state = "S"; // Default state: Susceptible
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
