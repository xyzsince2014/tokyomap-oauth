package tokyomap.oauth.web.domain.repositories.room;

import java.time.LocalDate;
import java.util.List;

import tokyomap.oauth.web.domain.models.entities.ReservableRoom;
import tokyomap.oauth.web.domain.models.entities.ReservableRoomId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservableRoomRepository extends JpaRepository<ReservableRoom, ReservableRoomId> {
  List<ReservableRoom> findByReservableRoomId_reservedDateOrderByReservableRoomId_roomIdAsc(
      LocalDate reservedDate);
  ReservableRoom findByReservableRoomId_RoomIdAndReservableRoomId_ReservedDate(Integer roomId,
      LocalDate reservedDate);
}

