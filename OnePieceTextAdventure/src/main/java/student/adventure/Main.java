package student.adventure;
import java.io.IOException;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) throws NullPointerException,
                                                  IOException, NoSuchElementException {
        GameEngine game = new GameEngine();
        game.runGame(args);
    }
}