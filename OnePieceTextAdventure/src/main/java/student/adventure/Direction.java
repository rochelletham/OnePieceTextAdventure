package student.adventure;

public class Direction {
    private String directionName;
    private String room;

    //Getters
    public String getDirectionName() {
        return this.directionName.toLowerCase();
    }
    public String getRoom() {
        return this.room;
    }

    //Setters
    public void setDirectionName(String directionName) {
        this.directionName = directionName.toLowerCase();
    }
    public void setRoom(String room) {
        this.room = room;
    }
}
