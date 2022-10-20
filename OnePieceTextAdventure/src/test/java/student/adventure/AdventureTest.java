package student.adventure;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class AdventureTest {
    private File file;
    private ObjectMapper mapper;
    private ArrayList<Room> rooms;

    // potential user command inputs
    private final static String EXAMINE = "examine";
    private final static String QUIT = "quit";
    private final static String QUIT_OUTPUT = "Quitting game";
    private final static String TAKE = "take";

    private final static String JSON_FILE_PATH = "src/main/java/student/adventure/OnePieceMap.json";
    private final static Path PATH = Paths.get("src/main/java/student/adventure/testOutput.txt");
    private final static Path ERROR_HANDLE_PATH =
            Paths.get("src/main/java/student/adventure/errorHandle.txt");
    @Before
    public void setUp() throws NullPointerException, FileNotFoundException {
        file = new File(JSON_FILE_PATH);
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        //console input test
        InputStream sysInBackup = System.in;
    }

    /************************************************************************
     * TEST PASSING FILE INTO GAME ENGINE COMMANDS
     ************************************************************************/
    @Test(expected=NullPointerException.class)
    public void fileNullCheck() throws NullPointerException, IOException{
        String[] arg = null;
        Main.main(arg);
    }

    @Test(expected = FileNotFoundException.class)
    public void testInvalidFile() throws IOException {
        String[] arg = {"src/main/resources/randomString.json", null};
        Main.main(arg);
    }

    @Test
    public void testValidFile() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ArrayList<Room> rooms = map.getRooms();
        assertTrue(rooms != null);
    }

    @Test
    public void testCorrectStartRoom() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ArrayList<Room> rooms = map.getRooms();
        assertEquals(rooms.get(0).getName(), "East Blue: Dawn Island");
    }

    //https://www.baeldung.com/java-write-to-file
    @Test
    public void testQuit() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(QUIT.getBytes());
        System.setIn(in);
        String[] arg = {JSON_FILE_PATH};
        Main.main(arg);
        String read =Files.readString(PATH);
        assertEquals(QUIT_OUTPUT, read);
    }

    /************************************************************************
     * TEST DIRECTION COMMANDS
     ************************************************************************/
    @Test
    public void testInvalidDirection() throws IOException, NoSuchElementException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream("asdf".getBytes());
        System.setIn(in);
        String[] arg = {JSON_FILE_PATH};
        Main.main(arg);
        String invalidInput = "I don't understand asdf!";
        String read = Files.readString(ERROR_HANDLE_PATH);
        assertTrue(read.contains(invalidInput));
    }

    @Test
    public void testInvalidDirectionWithLineBreak() throws IOException, NoSuchElementException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        String input = "asdf\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        String[] arg = {JSON_FILE_PATH};
        Main.main(arg);
        String invalidInput = "I don't understand asdf!";
        String read = Files.readString(ERROR_HANDLE_PATH);
        assertTrue(read.contains(invalidInput));
    }

    @Test
    public void testInvalidUserInputGo() throws IOException, NoSuchElementException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream("go".getBytes());
        System.setIn(in);
        String[] arg = {JSON_FILE_PATH};
        Main.main(arg);
        String invalidInput = "I don't understand go!";
        String read = Files.readString(ERROR_HANDLE_PATH);
        assertTrue(read.contains(invalidInput));
    }

    @Test
    public void testValidUserInputGoRandomTab() throws IOException, NoSuchElementException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        String inputString = "go \t \t East";
        ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(in);
        String[] arg = {JSON_FILE_PATH};
        Main.main(arg);
        inputString = inputString.toLowerCase();
        String expected = "You meet a brave, adventurous wannabe-pirate named Ussop and he declares himself as a member of your crew.";
        String read = Files.readString(PATH);
        assertTrue(read.contains(expected));
    }

    @Test
    public void testInvalidUserInputGoSubstring() throws IOException, NoSuchElementException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        String inputString = "golf";
        ByteArrayInputStream in = new ByteArrayInputStream(inputString.getBytes());
        System.setIn(in);
        String[] arg = {JSON_FILE_PATH};
        Main.main(arg);
        String invalidInput = "I don't understand " + inputString;
        String read = Files.readString(ERROR_HANDLE_PATH);
        assertTrue(read.contains(invalidInput));
    }

    @Test
    public void testInvalidGoQuit() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream("Go quit".getBytes());
        System.setIn(in);
        String[] arg = {JSON_FILE_PATH, "East Blue: Dawn Island"};
        Main.main(arg);
        String read = Files.readString(ERROR_HANDLE_PATH);
        assertTrue(read.contains("I don't understand go quit!"));
    }

    @Test
    public void testValidDirection() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream("Go East".getBytes());
        System.setIn(in);
        String[] arg = {JSON_FILE_PATH, "East Blue: Dawn Island"};
        Main.main(arg);
        String read = Files.readString(PATH);
        assertTrue(read.contains("Stopping by at this archipelago, you arrive at Syrup Village."));
    }
    @Test
    public void testValidDirectionToEndRoom() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream input = new ByteArrayInputStream(EXAMINE.getBytes());
        System.setIn(input);

        String[] arg = {JSON_FILE_PATH, "Thriller Bark"};
        Main.main(arg);

        String read = Files.readString(PATH);
        assertTrue(read.contains("This dark, haunting island is ruled by Pirate Gecko Maria.."));
    }
    /************************************************************************
     * TEST EXAMINE COMMANDS
     ************************************************************************/
    @Test
    public void testValidExamine() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream input = new ByteArrayInputStream("examine".getBytes());
        System.setIn(input);
        String[] arg = {JSON_FILE_PATH, "Thriller Bark"};
        Main.main(arg);
        String expectedOutput = "During your time at the island, you meet a talking skeleton and decide to let him join your crew!\n" +
                "You now have a skeleton musician named Brook! He gratefully thanks you!! Yohohoho!";
        String read = Files.readString(PATH);
        assertTrue(read.contains(expectedOutput));
    }
    @Test
    public void testInvalidExamineString() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream input = new ByteArrayInputStream("examine the room".getBytes());
        System.setIn(input);
        String[] arg = {JSON_FILE_PATH, "Thriller Bark"};
        Main.main(arg);
        String expectedOutput = "I don't understand examine the room!";
        String read = Files.readString(ERROR_HANDLE_PATH);
        assertTrue(read.contains(expectedOutput));
    }

    /************************************************************************
     * TEST TAKE COMMANDS
     ************************************************************************/
    @Test
    public void testTake() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream input = new ByteArrayInputStream((TAKE + "flame dial").getBytes());
        System.setIn(input);

        String[] arg = {JSON_FILE_PATH, "Sky Island"};
        rooms = map.getRooms();
        Main.main(arg);

        String read = Files.readAllLines(PATH).get(0);
        assertTrue(read.contains("Welcome to the sky island -- also known as Skypeia!"));
    }

    @Test
    public void testInvalidTakeCommand() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        String userInput = TAKE + "sWEaTSHIRt";
        ByteArrayInputStream input = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(input);
        String[] arg = {JSON_FILE_PATH};
        rooms = map.getRooms();
        Main.main(arg);
        String read = Files.readString(ERROR_HANDLE_PATH);
        userInput = userInput.toLowerCase();
        String invalidInput = "There is no sweatshirt in the room.";
        assertTrue(read.contains(invalidInput));
    }

    @Test
    public void testMissingTakeKeywordInUserInput() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream input = new ByteArrayInputStream("flame dial".getBytes());
        System.setIn(input);
        String[] arg = {JSON_FILE_PATH, "Sky Island"};
        Main.main(arg);
        String read = Files.readString(ERROR_HANDLE_PATH);
        String invalidInput = "I don't understand flame dial!";
        assertTrue(read.contains(invalidInput));
    }

    /************************************************************************
     * TEST DROP COMMANDS
     ************************************************************************/
    @Test
    public void testValidDrop() throws IOException {
        OnePieceMap map = mapper.readValue(file, OnePieceMap.class);
        ByteArrayInputStream input = new ByteArrayInputStream("drop flame dial".getBytes());
        System.setIn(input);
        String[] arg = {JSON_FILE_PATH, "Sky Island"};
        Main.main(arg);
        String read = Files.readAllLines(PATH).get(0);
        assertTrue(read.contains("Welcome to the sky island -- also known as Skypeia!"));
    }

}