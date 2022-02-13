package tokyomap.oauth.web.domain.repositories.reservation;

import java.util.List;

import tokyomap.oauth.web.domain.models.entities.Reservation;
import tokyomap.oauth.web.domain.models.entities.ReservableRoomId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  List<Reservation> findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(
      ReservableRoomId reservableRoomId);
  Reservation findByReservationId(Integer reservationId);
}
