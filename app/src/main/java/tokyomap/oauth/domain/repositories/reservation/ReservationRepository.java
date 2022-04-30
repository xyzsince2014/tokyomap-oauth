package tokyomap.oauth.domain.repositories.reservation;

import java.util.List;
import tokyomap.oauth.domain.models.entities.ReservableRoomId;
import tokyomap.oauth.domain.models.entities.Reservation;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
  @Cacheable("reservations") // an Aspect which caches reservation for reservableRoomId
  List<Reservation> findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(ReservableRoomId reservableRoomId);

  @Cacheable("reservation") // returns reservation for reservationId if its cached
  Reservation findByReservationId(Integer reservationId);
}
