package flik;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class FlikTest {

    @Test
    public void flikTest() {
        int i = 128;
        int j = 128;
        assertTrue(Flik.isSameNumber(i, j));
    }

}
