import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloWorldTest {
    @Test
    public void testStringLength() {
        String hello = "Hello from Nadezhda";

        assertTrue(hello.length() >15, "Length <= 15");
    }
}
