package flik;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class FlikTest {

    @Test
    public void flikTest127() {
        int i = 127;
        int j = 127;
        assertTrue(Flik.isSameNumber(i, j));
    }
    @Test
    public void flikTest128() {
        int i = 128;
        int j = 128;
        assertTrue(Flik.isSameNumber(i, j));
    }

}
