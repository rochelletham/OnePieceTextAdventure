package student.adventure;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class OnePieceMap {
    private String startingRoom;
    private String endingRoom;
    @JsonProperty("rooms")
    private ArrayList<Room> rooms;

    //Getters
    public String getStartingRoomName(){
        return this.startingRoom;
    }
    public String getEndingRoomName(){
        return this.endingRoom;
    }
//    public Room getStartingRoom() {return this.getRoom(0)}

    public ArrayList<Room> getRooms() {
        return this.rooms;
    }

    /**
     * Takes a room name string and returns the corresponding Room object.
     * When called in game engine, we get the correct room object of the room we want to start/test from
     */
    public Room getRoom(String roomName) {
        Room room = new Room();
        for (int i = 0; i < rooms.size(); i++) {
            Room tempRoom = rooms.get(i);
            if (tempRoom.getName().equals(roomName)) {
                room = tempRoom;
            }
        }
        return room;
    }

    // Setters
    public void setStartingRoom(String startingRoom) {
        this.startingRoom = startingRoom;
    }
    public void setEndingRoom(String endingRoom) {
        this.endingRoom = endingRoom;
    }
    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * Print all the room names found in json
     */
    public void printRooms() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = getRooms().get(i);
            System.out.println(room.getName());
            ArrayList<Direction> directions = room.getDirections();
            if (directions == null || directions.size() == 0) {
                System.out.println("There are no rooms that this place can go to.");
                continue;
            }
        }
    }
}