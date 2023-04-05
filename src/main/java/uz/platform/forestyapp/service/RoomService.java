package uz.platform.forestyapp.service;

import uz.platform.forestyapp.entity.EducationCenter;
import uz.platform.forestyapp.entity.Room;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.RoomDto;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    ApiResponse addRoom(RoomDto roomDto, User currentUser);
    ApiResponse editRoom(UUID id,RoomDto roomDto,User currentUser);
    ApiResponse deleteRoom(UUID id,User currentUser);
    List<Room> getAllRooms(UUID educationCenterId);
}
