package uz.platform.forestyapp.service.impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.platform.forestyapp.entity.Room;
import uz.platform.forestyapp.entity.User;
import uz.platform.forestyapp.entity.enums.PlanLimitName;
import uz.platform.forestyapp.payload.ApiResponse;
import uz.platform.forestyapp.payload.RoomDto;
import uz.platform.forestyapp.repository.EducationCenterRepo;
import uz.platform.forestyapp.repository.RoomRepo;
import uz.platform.forestyapp.service.RoomService;
import uz.platform.forestyapp.service.utils.PlanLimitService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    RoomRepo roomRepo;

    @Autowired
    EducationCenterRepo educationCenterRepo;

    @Autowired
    PlanLimitService planLimitService;

    @Override
    public ApiResponse addRoom(RoomDto roomDto, User currentUser) {
        if(planLimitService.checkPlanLimit(currentUser.getEducationCenter().getId(), PlanLimitName.ROOM)){
            return new ApiResponse("Xonalar soni limitdan kupayib ketdi!",false);
        }
        if(roomRepo.existsByRoomNumber(roomDto.getRoomNumber())){
            return new ApiResponse("Bunday raqamlik xona mavjud!",false);
        }
        Room room = Room.builder()
                .roomName(roomDto.getRoomName())
                .roomNumber(roomDto.getRoomNumber())
                .floor(roomDto.getFloor())
                .educationCenter(currentUser.getEducationCenter())
                .build();
        roomRepo.save(room);
        return new ApiResponse("Xona saqlandi!",true);
    }

    @Override
    public ApiResponse editRoom(UUID id,RoomDto roomDto, User currentUser) {
        Optional<Room> optionalRoom = roomRepo.findById(id);
        if(optionalRoom.isEmpty()){
            return new ApiResponse("Bunday idlik xona mavjud emas!",false);
        }
        Room room = optionalRoom.get();
        if(!room.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())){
            return new ApiResponse("Bunday idlik xona joriy uquv markazga tegishli emas!",false);
        }
        if(roomRepo.existsByRoomNumberAndIdNot(roomDto.getRoomNumber(),id)){
            return new ApiResponse("Bunday raqamlik xona mavjud!",false);
        }
        room.setRoomName(roomDto.getRoomName());
        room.setRoomNumber(roomDto.getRoomNumber());
        room.setFloor(roomDto.getFloor());
        roomRepo.save(room);
        return new ApiResponse("Xona tahrirlandi!",true);
    }

    @Override
    public ApiResponse deleteRoom(UUID id, User currentUser) {
        Optional<Room> optionalRoom = roomRepo.findById(id);
        if(optionalRoom.isEmpty()){
            return new ApiResponse("Bunday idlik xona mavjud emas!",false);
        }
        Room room = optionalRoom.get();
        if(!room.getEducationCenter().getId().equals(currentUser.getEducationCenter().getId())){
            return new ApiResponse("Bunday idlik xona joriy uquv markazga tegishli emas!",false);
        }
        roomRepo.delete(room);
        return new ApiResponse("Xona o'chirildi!",true);
    }

    @Override
    public List<Room> getAllRooms(UUID educationCenterId) {
        return roomRepo.findAllByEducationCenterId(educationCenterId);
    }
}
