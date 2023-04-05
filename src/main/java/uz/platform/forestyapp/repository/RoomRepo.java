package uz.platform.forestyapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.platform.forestyapp.entity.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepo extends JpaRepository<Room, UUID> {
    boolean existsByRoomNumber(long roomNumber);

    boolean existsByRoomNumberAndIdNot(long roomNumber,UUID id);

    List<Room> findAllByEducationCenterId(UUID educationCenterId);

    long countByEducationCenterId(UUID id);

}
