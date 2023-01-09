package system.gateways;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import shared.constants.IDType;
import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.entities.room.Room;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RoomDataMapper implements RoomDataGateway{
    private final String PATH = System.getProperty("user.dir");
    private final String ROOM_FOLDER = PATH + "/src/system/database/rooms/";
    private final File ROOM_COUNT_FILE = new File(PATH + "/src/system/database/countFiles/room.txt");
    private final String SUFFIX = ".json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    /**
     * {@inheritDoc}
     */
    public void addRoom(Room Room) throws IOException {
        addRoom(Room, true);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteRoom(String RoomId) throws IOException {
        File file = new File(ROOM_FOLDER + RoomId + SUFFIX);
        if (!file.delete()) {
            throw new IOException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void updateRoom(Room Room) throws InvalidIDException, IOException {
        try {
            deleteRoom(Room.getRoomID());
        } catch (IOException e) {
            throw new InvalidIDException(IDType.ROOM);
        }
        addRoom(Room, false);
    }

    /**
     * {@inheritDoc}
     */
    public Set<Room> getAllRooms() throws IOException {
        File folder = new File(ROOM_FOLDER);
        HashSet<Room> Rooms = new HashSet<>();
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().endsWith(SUFFIX)) {
                String RoomString = String.join("\n", Files.readAllLines(file.toPath()));
                Room Room = jsonToRoom(RoomString);
                Rooms.add(Room);
            }
        }
        return Rooms;
    }

    /**
     * {@inheritDoc}
     */
    public int getRoomCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(ROOM_COUNT_FILE));
        return new Integer(rd.readLine());
    }

    /**
     * {@inheritDoc}
     */
    public void incrementRoomCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(ROOM_COUNT_FILE));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(ROOM_COUNT_FILE, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    private void addRoom(Room Room, boolean increment) throws IOException {
        File RoomFile = new File(ROOM_FOLDER + Room.getRoomID() + SUFFIX);
        Writer wr = new FileWriter(RoomFile);
        wr.write(RoomToJson(Room));
        wr.close();

        if (increment) {
            incrementRoomCount();
        }
    }

    private Room jsonToRoom(String RoomString) {
        return gson.fromJson(RoomString, Room.class);
    }

    private String RoomToJson(Room Room) {
        return gson.toJson(Room);
    }
}
