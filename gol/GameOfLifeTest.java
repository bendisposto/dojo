import static org.junit.Assert.*;
import org.junit.*;

public class GameOfLifeTest {

    @Ignore
    @Test
    public void einsameZellenSterbenKeineNachbarn() throws Exception {
        assertEquals(0, GameOfLife.spielregel(0, 1));
    }

    @Ignore
    @Test
    public void toteZellenMitDreiNachbarnWerdenLebendig() throws Exception {
        assertEquals(1, GameOfLife.spielregel(3, 0));
    }

    @Ignore
    @Test
    public void lebendeZellenMitZweiNachbarnBleibenLebendig() throws Exception {
        assertEquals(1, GameOfLife.spielregel(2, 1));
    }

    @Ignore
    @Test
    public void toteZellenMitZweiNachbarnBleibenTot() throws Exception {
        assertEquals(0, GameOfLife.spielregel(2, 0));
    }

    @Ignore
    @Test
    public void toteZellenOhneNachbarnBleibenTot() throws Exception {
        assertEquals(0, GameOfLife.spielregel(0, 0));
    }

    // Folgende Tests sind zur Sicherheit, sie sollten nun alle laufen.
    // Entfernen Sie den Blockkommentar und pruefen Sie ob alles laeuft.
        
        /* 
        
            @Test
            public void einsameZellenSterbenEinNachbar() throws Exception {
                assertEquals(0, GameOfLife.spielregel(1, 1));
            }
        
            @Test
            public void zellenMitVierNachbarnSterben() throws Exception {
                assertEquals(0, GameOfLife.spielregel(4, 1));
            }
        
            @Test
            public void zellenMitFuenfNachbarnSterben() throws Exception {
                assertEquals(0, GameOfLife.spielregel(5, 1));
            }
        
            @Test
            public void zellenMitSechsNachbarnSterben() throws Exception {
                assertEquals(0, GameOfLife.spielregel(6, 1));
            }
        
            @Test
            public void zellenMitSiebenNachbarnSterben() throws Exception {
                assertEquals(0, GameOfLife.spielregel(7, 1));
            }
        
            @Test
            public void zellenMitAchtNachbarnSterben() throws Exception {
                assertEquals(0, GameOfLife.spielregel(8, 1));
            }
        
            @Test
            public void toteZellenMitEinemNachbarnBleibenTot() throws Exception {
                assertEquals(0, GameOfLife.spielregel(1, 0));
            }
        
        
            @Test
            public void lebendeZellenMitDreiNachbarnBleibenLebendig() throws Exception {
                assertEquals(1, GameOfLife.spielregel(3, 1));
            }
        
            @Test
            public void toteZellenMitVierNachbarnBleibenTot() throws Exception {
                assertEquals(0, GameOfLife.spielregel(4, 0));
            }
        
            @Test
            public void toteZellenMitFuenfNachbarnBleibenTot() throws Exception {
                assertEquals(0, GameOfLife.spielregel(5, 0));
            }
        
            @Test
            public void toteZellenMitSechsNachbarnBleibenTot() throws Exception {
                assertEquals(0, GameOfLife.spielregel(6, 0));
            }
        
            @Test
            public void toteZellenMitSiebenNachbarnBleibenTot() throws Exception {
                assertEquals(0, GameOfLife.spielregel(7, 0));
            } 
        
            @Test
            public void toteZellenMitAchtNachbarnBleibenTot() throws Exception {
                assertEquals(0, GameOfLife.spielregel(8, 0));
            }     
         */

    @Ignore
    @Test
    public void keineNachbarn() {
        int lebendeNachbarn = GameOfLife
                .zaehleLebendeNachbarn(new int[][] {
                                { 0, 0, 0, 0 },
                                { 0, 0, 0, 0 },
                                { 0, 0, 0, 1 } },
                        1, 1); /* 2. Zeile, 2. Spalte */
        assertEquals(0, lebendeNachbarn);
    }

    @Ignore
    @Test
    public void einNachbar() {
        int lebendeNachbarn = GameOfLife
                .zaehleLebendeNachbarn(new int[][] {
                        { 0, 0, 0, 0 },
                        { 0, 0, 0, 0 },
                        { 0, 0, 0, 1 } }, 1, 2); /* 2. Zeile, 3. Spalte */
        assertEquals(1, lebendeNachbarn);
    }

    @Ignore
    @Test
    public void einNachbarUndEineEntfernteZelle() {
        int lebendeNachbarn = GameOfLife
                .zaehleLebendeNachbarn(new int[][] {
                        { 1, 0, 0, 0 },
                        { 0, 0, 0, 0 },
                        { 0, 0, 0, 1 } }, 1, 2);
        assertEquals(1, lebendeNachbarn);
    }

    @Ignore
    @Test
    public void vierNachbarnEinerInaktivenZelle() {
        int lebendeNachbarn = GameOfLife
                .zaehleLebendeNachbarn(new int[][] {
                        { 1, 0, 1, 0 },
                        { 0, 0, 0, 1 },
                        { 0, 1, 0, 1 } }, 1, 2);
        assertEquals(4, lebendeNachbarn);
    }

    @Ignore
    @Test
    public void vierNachbarnEinerAktivenZelle() {
        int lebendeNachbarn = GameOfLife
                .zaehleLebendeNachbarn(new int[][] {
                        { 1, 0, 1, 0 },
                        { 0, 0, 1, 1 },
                        { 0, 1, 0, 1 } }, 1, 2);
        assertEquals(4, lebendeNachbarn);
    }

    @Ignore
    @Test
    public void achtNachbarnEinerAktivenZelle() {
        int lebendeNachbarn = GameOfLife
                .zaehleLebendeNachbarn(new int[][] {
                        { 1, 1, 1, 1 },
                        { 0, 1, 1, 1 },
                        { 0, 1, 1, 1 } }, 1, 2);
        assertEquals(8, lebendeNachbarn);
    }

    @Ignore
    @Test
    public void achtNachbarnEinerInaktivenZelle() {
        int lebendeNachbarn = GameOfLife
                .zaehleLebendeNachbarn(new int[][] {
                        { 1, 1, 1, 1 },
                        { 0, 1, 0, 1 },
                        { 0, 1, 1, 1 } }, 1, 2);
        assertEquals(8, lebendeNachbarn);
    }

    /* Randzellen sind immer tot! */

    @Ignore
    @Test
    public void simpleGen1() throws Exception {
        int[][] input = {
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 } };
        int[][] output = {
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 } };
        checkBoard(GameOfLife.berechneNaechsteGeneration(input), output);
    }

    @Ignore
    @Test
    public void simpleGen2() throws Exception {
        int[][] input = {
                { 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 } };
        int[][] output = {
                { 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 } };
        checkBoard(GameOfLife.berechneNaechsteGeneration(input), output);
    }

    @Ignore
    @Test
    public void simpleGen3() throws Exception {
        int[][] input = {
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 1, 0 },
                { 0, 1, 1, 1, 0 },
                { 0, 0, 0, 0, 0 } };
        int[][] output = {
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 1, 0 },
                { 0, 0, 1, 1, 0 },
                { 0, 0, 0, 0, 0 } };
        checkBoard(GameOfLife.berechneNaechsteGeneration(input), output);
    }

    @Ignore
    @Test
    public void simpleGen4() throws Exception {
        int[][] input = {
                { 0, 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 0, 0 },
                { 0, 0, 0, 1, 1, 0 },
                { 0, 1, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 0, 0 } };
        int[][] output = {
                { 0, 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 1, 0 },
                { 0, 1, 0, 0, 1, 0 },
                { 0, 0, 1, 1, 1, 0 },
                { 0, 0, 0, 0, 0, 0 } };
        checkBoard(GameOfLife.berechneNaechsteGeneration(input), output);
    }


    private void checkBoard(int[][] output, int[][] input) {
        assertEquals(input.length, output.length);
        for (int i = 0; i < output.length; i++) {
            assertArrayEquals(output[i], input[i]);
        }
    }

}