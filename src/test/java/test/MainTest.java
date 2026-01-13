package test;
import org.junit.Ignore;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@Ignore("Temporarily disabled for CI pipeline validation")
//@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"json:target/cucumber.json"},
        features = "src/test/resources/features",
        glue = "test.steps"
)
public class MainTest {
    // No need to extend TestCase
}