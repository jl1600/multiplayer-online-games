package system.gateways;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import system.entities.room.Room;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RoomDataMapper implements RoomDataGateway{
    private final String PATH = System.getProperty("user.dir");
    private final String ROOM_FOLDER = PATH + "/src/system/database//";
    private final File ROOM_COUNT_FILE = new File(PATH + "/src/system/database/countFiles/room.txt");
    private final String[] SUBFOLDERS = {"quiz/", "hangman/"};
    private final String SUFFIX = ".json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * {@inheritDoc}
     */
    public void addRoom(Room room) throws IOException {
        addRoom(room, true);
    }

    /**
     * {@inheritDoc}
     */
    public void updateRoom(Room room) throws IOException {
        deleteRoom(room.getRoomID());
        addRoom(room, false);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteRoom(String roomID) throws IOException {
        boolean deleted = false;
        for (String subfolder : SUBFOLDERS) {
            File file = new File(ROOM_FOLDER + subfolder + roomID + SUFFIX);
            if (file.delete()) {
                deleted = true;
            }
        }
        if (!deleted) {
            throw new IOException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Set<Room> getAllRooms() throws IOException {
        HashSet<Room> rooms = new HashSet<>();
        for (String subfolder : SUBFOLDERS) {
            File folder = new File(ROOM_FOLDER + subfolder);
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.getName().endsWith(SUFFIX)) {
                    String roomString = String.join("\n", Files.readAllLines(file.toPath()));
                    Room room = jsonToRoom(roomString, subfolder);
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    /**
     * {@inheritDoc}
     */
    public int getRoomCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(ROOM_COUNT_FILE));
        return new Integer(rd.readLine());
    }

    private void addRoom(Room room, boolean increment) throws IOException {
        String subfolder;
        subfolder = SUBFOLDERS[0];

        File roomFile = new File(ROOM_FOLDER + subfolder + room.getRoomID() + SUFFIX);
        Writer wr = new FileWriter(roomFile);
        wr.write(roomToJson(room));
        wr.close();

        if (increment) {
            incrementRoomCount();
        }
    }

    private void incrementRoomCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(ROOM_COUNT_FILE));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(ROOM_COUNT_FILE, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    private String roomToJson(Room room) {
        return gson.toJson(room);
    }

    /**
     * Converts Json string to Room
     * @param roomString the room string details to convert
     * @param subfolder indicating which room type folder it is from
     * @return Room object from the json string
     */
    public Room jsonToRoom(String roomString, String subfolder) {
        return gson.fromJson(roomString, Room.class);
    }
}
