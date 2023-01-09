package system.entities.room;

import java.util.ArrayList;

public class Room {

    private String roomID;
    private String title;
    private String password;
    private int maxUser;
    private ArrayList<String> userIDs;
    private ArrayList<String> chatIDs;

    public Room(String roomID, String title, String password, int maxUser){
        this.roomID = roomID;
        this.title = title;
        if (password == null){
             this.password = "88888888";
        } else{
            this.password = password;
        }
        this.maxUser = maxUser;
        this.userIDs = new ArrayList<>();
        this.chatIDs = new ArrayList<>();
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxUser() {
        return maxUser;
    }

    public void setMaxUser(int maxUser) {
        this.maxUser = maxUser;
    }

    public ArrayList<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(ArrayList<String> userIDs) {
        this.userIDs = userIDs;
    }

    public ArrayList<String> getChatIDs() {
        return chatIDs;
    }

    public void setChatIDs(ArrayList<String> chatIDs) {
        this.chatIDs = chatIDs;
    }
}
