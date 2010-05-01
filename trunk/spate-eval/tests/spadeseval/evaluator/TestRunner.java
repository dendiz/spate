package spadeseval.evaluator;
import junit.framework.TestCase;

 
public class TestRunner extends TestCase {
	public static void main(String[] args) {
		System.out.println("running unit tests");
		org.junit.runner.JUnitCore.runClasses(TestEv.class);
		org.junit.runner.JUnitCore.runClasses(TestHandRank.class);
		org.junit.runner.JUnitCore.runClasses(TestLoadEv.class);
		org.junit.runner.JUnitCore.runClasses(TestWinners.class);
	}
}
