package student.adventure;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class GameEngine {
    private OnePieceMap map;
    private ArrayList<Room> rooms;
    private ArrayList<String> characters = new ArrayList<>();
    private ArrayList<String> inventory = new ArrayList<>();
    private final static String EXAMINE = "examine";
    private final static String QUIT = "quit";
    private final static String EXIT = "exit";
    private final static String TAKE = "take";
    private final static String DROP = "drop";
    private final static String GO = "go";
    private final static String FINISH_GAME_TEXT = "You finished the game!";
    private final static String QUIT_OUTPUT = "Quitting game";
    private final static String CREW = "crew";
    private final static Path OUTPUT_PATH = Paths.get("src/main/java/student/adventure/testOutput.txt");
    private final static Path ERROR_HANDLE_PATH =
            Paths.get("src/main/java/student/adventure/errorHandle.txt");
    // the user input from the console
    private String userInput;
    private String testRoom;

    public void runGame(String[] args) throws NullPointerException, IOException, NoSuchElementException {
          File file = new File("src/main/java/student/adventure/OnePieceMap.json");
        // File file = new File(args[0]);
        // check to make sure we only assign testRoom name if there's a second argument
        if (args.length > 1) {
            testRoom = args[1];
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        if (file.exists()) {
            try {
                map = mapper.readValue(file, OnePieceMap.class);
                //Get command line input:  https://www.geeksforgeeks.org/ways-to-read-input-from-console-in-java/
                Scanner input = new Scanner(System.in);
                rooms = map.getRooms();

                // When user first starts program, they should start with the starting room
                Room currRoom = map.getRooms().get(0);

                // get the correct room object of the room we want to start/test from
                if (testRoom != null) {
                    currRoom = map.getRoom(testRoom);
                }
                // only continue the game if player has not reached the ending room
                while (currRoom.getName() != map.getEndingRoomName()) {
                    String name = currRoom.getName();
                    String description = currRoom.getDescription();
                    String character = currRoom.getCharacter();
                    ArrayList<String> item = currRoom.getItem();
                    ArrayList<Direction> directions = currRoom.getDirections();
                    // prints out all the information that user needs
                    examineRoom(name, description, character, item, directions);
                    if (currRoom.getName().equals(map.getEndingRoomName())) {
                        break;
                    }
                    System.out.print("> ");
                    try {
                        userInput = input.nextLine();
                    } catch (NoSuchElementException e) {
                        break;
                    }
                    // formatting the user input string
                    userInput = userInput.toLowerCase();
                    String userInputDir = userInput.replace(GO, "");
                    userInputDir = userInputDir.strip();
                    if (userInput.contains(GO)) {
                        // check if the user inputted a direction that is valid for current room
                        if (currRoom.getAllDirectionNames().contains(userInputDir)) {
                            currRoom = getNextRoom(userInputDir, directions);
                        } else {
                        // If the user inputs an invalid direction,
                            // then the game should inform them it can't go that way
                            while (!currRoom.getAllDirectionNames().contains(userInputDir)
                                    && !userInput.equals(QUIT) && !userInput.equals(EXAMINE)
                                    && !userInput.contains(TAKE) && !userInput.contains(DROP)) {
                                String invalidInput = "I don't understand " + userInput + "!";
                                byte[] strToBytes = invalidInput.getBytes();
                                Files.write(ERROR_HANDLE_PATH, strToBytes);
                                System.out.println(invalidInput);
                                System.out.print("> ");
                                try {
                                    userInput = input.nextLine();
                                    userInputDir = userInput.replace(GO, "");
                                    userInputDir = userInputDir.strip();
                                } catch (NoSuchElementException e) {
                                    break;
                                }
                            }
                        }
                    } else if (userInput.equals(QUIT) || userInput.equals(EXIT)) {
                        // quit the game if the player types in "quit" or "exit"
                        byte[] strToBytes = QUIT_OUTPUT.getBytes();
                        Files.write(OUTPUT_PATH, strToBytes);
                        System.out.println(QUIT_OUTPUT);
                        break;
                    } else if (userInput.equals(EXAMINE)) {
                        examineRoom(name, description, character, item, directions);
                    } else if (userInput.contains(TAKE)) {
                        String reqItem = userInput.replace(TAKE, " ");
                        if (item.contains(reqItem)) {
                            item.remove(reqItem);
                            this.inventory.add(reqItem);
                        } else {
                            String noItemResponse = "There is no " + reqItem + " in the room.";
                            System.out.println(noItemResponse);
                            byte[] strToBytes = noItemResponse.getBytes();
                            Files.write(ERROR_HANDLE_PATH, strToBytes);
                        }
                    } else if (userInput.contains(DROP)) {
                        String dropItem = userInput.replace(DROP, " ");
                        if (this.inventory.contains(dropItem)) {
                            this.inventory.remove(dropItem);
                            String dropItemResponse = "Dropped item: " + dropItem;
                            System.out.println(dropItemResponse);
                            byte[] strToBytes = dropItemResponse.getBytes();
                            Files.write(OUTPUT_PATH, strToBytes);
                        } else {
                            String noItemResponse = "You don't have " + dropItem +"!";
                            System.out.println(noItemResponse);
                            byte[] strToBytes = noItemResponse.getBytes();
                            Files.write(OUTPUT_PATH, strToBytes);
                        }
                    } else if (userInput.contains(CREW)) {
                        ArrayList<String> crew = getCurrentCrew();
                        if (crew == null) {
                            String crewResponse = "You don't have a crew yet.";
                            System.out.println(crewResponse);
                            byte[] strToBytes = crewResponse.getBytes();
                            Files.write(OUTPUT_PATH, strToBytes);
                        } else {
                            System.out.println(crew);
                        }
                    }
                }
                input.close();
            } catch (IOException error) {
                error.printStackTrace();
            }
        } else {
            throw new FileNotFoundException("File doesn't exist.");
        }
    }
    /** Returns the next room that player will go to given the current user input direction
     * @param userInput is the command line input from player
     * @param directions the array of valid directions that player can go to from current room
     * @return the room that player will go to next based on user input
     * */
    private Room getNextRoom(String userInput, ArrayList<Direction> directions) {
        for (int i = 0; i < directions.size(); i++) {
            if (directions.get(i).getDirectionName().equals(userInput)) {
                // update the current room to the next room the player will go to
                String nextRoomName = directions.get(i).getRoom();
                for (int j = 0; j < rooms.size(); j++) {
                    if (rooms.get(j).getName().equals(nextRoomName)) {
                        return rooms.get(j);
                    }
                }
            }
        }
        System.out.println("Could not find a location using user's inputted direction.");
        return null;
    }
    /** Returns the next room that player will go to given the current user input direction
     * @param name is the command line input from player
     * @param description string that describes the current room player is in
     * @param character the current character(s) that player meets in current room
     * @param item an array list that includes all the available items in current room
     * @param directions the array of valid directions that player can go to from current room
     * @return the room that player will go to next based on user input
     * */
    private void examineRoom(String name, String description,
                             String character, ArrayList<String> item,
                             ArrayList<Direction> directions) throws IOException {

        System.out.println("Current Location: " + name);
        System.out.println(description);

        byte[] currLocation = ("Current Location: " + name).getBytes();
        byte[] descriptionBytes = description.getBytes();
        Files.write(OUTPUT_PATH, currLocation);
        Files.write(OUTPUT_PATH, descriptionBytes);

        // checks if there are new characters in this room and NOT already in crew
        if (character.length() != 0) {
            if (this.characters != null && this.characters.contains(character)) {
                System.out.println(character + " is now part of your crew!");
            } else {
                System.out.println("New crew member who joined: " + character);
                this.characters.add(character);
            }
        }
        // checks if there are items in this room
        if (item.size() != 0) {
            System.out.println("Items visible: ");
            for (int j = 0; j < item.size(); j++) {
                System.out.println(item.get(j));
            }
        }
        // checks what is in your current inventory
        if (this.inventory.size() != 0) {
            System.out.println("Your Inventory has: ");
            for (int j = 0; j < this.inventory.size(); j++) {
                System.out.println(this.inventory.get(j));
            }
        }
        if (this.characters.size() != 0) {
            System.out.println("Current crew: " + this.characters);
        }
        //if current room is the last room (meaning no more directions), then the game ends here
        if (directions.size() == 0) {
            byte[] strToBytes = FINISH_GAME_TEXT.getBytes();
            Files.write(OUTPUT_PATH, strToBytes);
            System.out.println(FINISH_GAME_TEXT);
            return;
        }
        System.out.print("From here, you can go: ");
        for (int i = 0; i < directions.size(); i++) {
            if (i == directions.size() - 1) {
                System.out.println(directions.get(i).getDirectionName());
            } else {
                System.out.print(directions.get(i).getDirectionName() + ", ");
            }
        }
    }

    private ArrayList<String> getCurrentCrew() {
        return this.characters;
    }
}

