package system.gateways;

import shared.exceptions.use_case_exceptions.InvalidIDException;
import system.entities.room.Room;

import java.io.IOException;
import java.util.Set;

public interface RoomDataGateway {
    /**
     * Adds the input room to the database and increments the room count by 1.
     *
     * @param room the room to add
     * @throws IOException if there is a problem saving to the database
     */
    void addRoom(Room room) throws IOException;

    /**
     * Updates the input room in the database.
     *
     * @param room room to update.
     * @throws IOException            if the database is not found
     * @throws InvalidIDException if the room does not exist
     */
    void updateRoom(Room room) throws IOException, InvalidIDException;

    /**
     * Deletes the room with the specified roomId from the database.
     *
     * @param roomId roomId of the room to delete
     * @throws IOException if there is a problem deleting the file
     */
    void deleteRoom(String roomId) throws IOException;

    /**
     * Returns a set of all rooms in the database.
     *
     * @return a set of all rooms in the database
     * @throws IOException if there is a problem reading from the database
     */
    Set<Room> getAllRooms() throws IOException;

    /**
     * Returns the total number of rooms ever created by the program.
     * <p>
     * This number does not decrease when a room is deleted.
     *
     * @return the total number of rooms ever created
     * @throws IOException if there is a problem reading from the database
     */
    int getRoomCount() throws IOException;

    /**
     * Increases the number of rooms created by 1
     * @throws IOException if there is a problem reading from the database
     */
    void incrementRoomCount() throws IOException;
}
