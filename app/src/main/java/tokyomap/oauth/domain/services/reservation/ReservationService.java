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
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@Transactional // an Aspect which begins a transaction, then commits or rollbacks it
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final ReservableRoomRepository reservableRoomRepository;
  private final PlatformTransactionManager transactionManager;

  @Autowired
  public ReservationService(
      ReservationRepository reservationRepository,
      ReservableRoomRepository reservableRoomRepository,
      PlatformTransactionManager transactionManager
  ) {
    this.reservationRepository = reservationRepository;
    this.reservableRoomRepository = reservableRoomRepository;
    this.transactionManager = transactionManager;
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
  @Async // execute cancellation with another thread
  public void cancel(Integer reservationId, User user) {
    // use a Programmatic transaction instead of declarative one
    DefaultTransactionDefinition txDef =  new DefaultTransactionDefinition();
    txDef.setName("CancelReservationTx");
    txDef.setReadOnly(false);
    txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

    // begins a transaction
    TransactionStatus txStatus = transactionManager.getTransaction(txDef);

    try {
      Reservation reservation = this.reservationRepository.findByReservationId(reservationId);

      if(user.getRole() != Role.ADMIN && !Objects.equals(reservation.getUser().getUserId(), user.getUserId())) {
        throw new AccessDeniedException("The cancellation is not permitted.");
      }

      this.reservationRepository.delete(reservation);
      transactionManager.commit(txStatus);

    } catch (Exception e) {
      transactionManager.rollback(txStatus);
      throw new DataAccessException("error in cancel()", e) {};
    }
  }
}
