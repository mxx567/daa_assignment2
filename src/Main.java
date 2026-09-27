import java.io.IOException;
import datatypes.*;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("=== Correctness Validation ===");
        Tests.run();

        System.out.println("\n=== Experiments ===");
        Experiments.run();
    }
}
