import static org.junit.Assert.*;
import org.junit.*;

public class TinyMazeTest {

    @Test
    public void singleRowMaze() {
        String[][] maze = {
                { "S", "E" }
        };
        TinyMaze tm = new TinyMaze(maze);
        String[][] solution = {
                { "x", "x" }
        };
        assertArrayEquals(solution, tm.solve());
    }

}