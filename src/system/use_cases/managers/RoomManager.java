package system.use_cases.managers;

import system.entities.room.Room;
import system.gateways.RoomDataGateway;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private final Map<String, Room> rooms;
    private final RoomDataGateway gateway;
    private final IdManager idManager;

    public RoomManager(RoomDataGateway gateway) throws IOException {
        rooms = new HashMap<>();
        this.gateway = gateway;

        for (Room room : this.gateway.getAllRooms()) {
            rooms.put(room.getRoomID(), room);
        }
        this.idManager = new IdManager(gateway.getRoomCount() + 1);
    }

}
