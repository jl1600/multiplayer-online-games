package system.entities.chat;

import java.util.ArrayList;

public class Chat {
    private String chatID;
    private ArrayList<Integer> userIDs;
    private ArrayList<String> chatLog;

    public Chat(String chatID, ArrayList<Integer> userIDs){
        this.chatID = chatID;
        this.userIDs = userIDs;
        this.chatLog = new ArrayList<String>();
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public ArrayList<Integer> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(ArrayList<Integer> userIDs) {
        this.userIDs = userIDs;
    }

    public ArrayList<String> getChatLog() {
        return chatLog;
    }

    public void setChatLog(ArrayList<String> chatLog) {
        this.chatLog = chatLog;
    }
}
