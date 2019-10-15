import static org.junit.Assert.*;
import org.junit.*;

public class DieHardRabattTest {

    @Test
    public void keineTeileKostenNichts() {
        int[] warenkorb = { 0, 0, 0, 0, 0 };
        assertEquals(0.0, DieHardRabatt.summe(warenkorb), 0.01);
    }

}