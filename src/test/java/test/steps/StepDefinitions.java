package test.steps;

import io.cucumber.java.en.Given;

public class StepDefinitions {
    @Given("I have a working test")
    public void i_have_a_working_test() {
        System.out.println("Step executed!");
    }
}
