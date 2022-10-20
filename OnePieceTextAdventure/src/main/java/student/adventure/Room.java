package student.adventure;

import java.util.*;

public class Room {
    private String name;
    private String description;
    private String character;
    private ArrayList<String> item;
    private ArrayList<Direction> directions;

    //Getters
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public String getCharacter(){
        return this.character;
    }
    public ArrayList<String> getItem(){
        return this.item;
    }
    public ArrayList<Direction> getDirections(){
        return this.directions;
    }

    //Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCharacter(String character) {
        this.character = character;
    }
    public void setItem(ArrayList<String> item){
        this.item = item;
    }
    public void setDirections(ArrayList<Direction> directions){
        this.directions = directions;
    }

    /**
     *  Gives a list of strings consisting of potential directions that user can choose from.
     * @return a list of all the valid directions that user can go to from current room
     */
    public List<String> getAllDirectionNames() {
        List<String> directions = new ArrayList<>();
        for (int i = 0; i < getDirections().size(); i++) {
            directions.add(getDirections().get(i).getDirectionName());
        }
        return directions;
    }
}
