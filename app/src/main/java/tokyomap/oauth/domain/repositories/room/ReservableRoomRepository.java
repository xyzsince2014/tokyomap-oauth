package tokyomap.oauth.domain.repositories.room;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.LockModeType;
import tokyomap.oauth.domain.models.entities.ReservableRoom;
import tokyomap.oauth.domain.models.entities.ReservableRoomId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

/**
 * Spring recommends that Repositories are implemented, and Spring Data is a FW to do it.
 * We use Spring Data JPA, whose APIs wrap Spring Data.
 *
 * Spring Data JPA, on the DI container initialisation, creates proxy objects for Repository interfaces extending JpaRepository,
 * which are registered into the DI container as Beans, then the application executes DIs of the proxies, and uses their functions to call the entity manager's APIs.
 */
@Repository
public interface ReservableRoomRepository extends JpaRepository<ReservableRoom, ReservableRoomId> {
  @Lock(LockModeType.OPTIMISTIC)
  List<ReservableRoom> findAll();

  List<ReservableRoom> findByReservableRoomId_reservedDateOrderByReservableRoomId_roomIdAsc(LocalDate reservedDate);

  ReservableRoom findByReservableRoomId_RoomIdAndReservableRoomId_ReservedDate(Integer roomId, LocalDate reservedDate);
}

