package tokyomap.oauth.domain.repositories.reservation;

import java.util.List;

import tokyomap.oauth.domain.models.entities.ReservableRoomId;
import tokyomap.oauth.domain.models.entities.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  List<Reservation> findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(
      ReservableRoomId reservableRoomId);
  Reservation findByReservationId(Integer reservationId);
}
