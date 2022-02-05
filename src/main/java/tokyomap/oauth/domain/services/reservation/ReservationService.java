package tokyomap.oauth.domain.services.reservation;

import java.util.List;

import java.util.Objects;

import tokyomap.oauth.domain.models.entities.Reservation;
import tokyomap.oauth.domain.models.entities.ReservableRoom;
import tokyomap.oauth.domain.models.entities.ReservableRoomId;
import tokyomap.oauth.domain.models.entities.Role;
import tokyomap.oauth.domain.models.entities.User;
import tokyomap.oauth.domain.repositories.reservation.ReservationRepository;
import tokyomap.oauth.domain.repositories.room.ReservableRoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final ReservableRoomRepository reservableRoomRepository;

  @Autowired
  public ReservationService(ReservationRepository reservationRepository, ReservableRoomRepository reservableRoomRepository) {
    this.reservationRepository = reservationRepository;
    this.reservableRoomRepository = reservableRoomRepository;
  }

  /**
   * find reservations by reservableRoomId
   * @param reservableRoomId
   * @return
   */
  public List<Reservation> findReservations(ReservableRoomId reservableRoomId) {
    return this.reservationRepository.findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(reservableRoomId);
  }

  /**
   * make a reservation
   * @param reservation
   * @return
   */
  public Reservation reserve(Reservation reservation) {
    ReservableRoomId reservableRoomId = reservation.getReservableRoom().getReservableRoomId();

    // check if the room is available
    ReservableRoom reservableRoom = this.reservableRoomRepository.findByReservableRoomId_RoomIdAndReservableRoomId_ReservedDate(reservableRoomId.getRoomId(), reservableRoomId.getReservedDate());
    if (reservableRoom == null) {
      throw new UnavailableReservationException("Reservation Unavailable.");
    }

    // check duplicates
    boolean isOverlapped = this.reservationRepository
        .findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(reservableRoomId).stream()
        .anyMatch(x -> x.overlap(reservation));
    if (isOverlapped) {
      throw new AlreadyReservedException("The Reservation is duplicate.");
    }

    this.reservationRepository.saveAndFlush(reservation);

    // return the reservation for further use
    return reservation;
  }

  /**
   * cancel a reservation
   * @param reservationId
   * @param user
   */
  public void cancel(Integer reservationId, User user) {
    Reservation reservation = this.reservationRepository.findByReservationId(reservationId);

    if(user.getRole() != Role.ADMIN && !Objects.equals(reservation.getUser().getUserId(), user.getUserId())) {
      throw new AccessDeniedException("The cancellation is not permitted.");
    }

    this.reservationRepository.delete(reservation);
  }

}
