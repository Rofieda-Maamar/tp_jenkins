package test;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",  // path to your feature files
        glue = "test.steps",                        // package for step definitions
        plugin = {
                "json:build/cucumber/cucumber.json",
                "html:build/cucumber/html",
                "junit:build/test-results/test/cucumber.xml"
        }
)
public class MainTest {}
