package tokyomap.oauth.web.domain.models.entities;

import java.util.Objects;
import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

@Entity
public class Reservation implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reservation_id")
  private Integer reservationId;

  @Column(name = "start_time")
  private LocalTime startTime;

  @Column(name = "finish_time")
  private LocalTime finishTime;

  @ManyToOne
  @JoinColumns({@JoinColumn(name = "reserved_date"), @JoinColumn(name = "room_id")})
  private ReservableRoom reservableRoom;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public Reservation() {}

  /**
   * check if the reservation is overlapped
   * @param reservation
   * @return
   */
  public boolean overlap(Reservation reservation) {
    if(!Objects.equals(this.reservableRoom.getReservableRoomId(), reservation.reservableRoom.getReservableRoomId())) {
      return false;
    }
    if(this.startTime.equals(reservation.startTime) && this.finishTime.equals(reservation.finishTime)) {
      return false;
    }
    return reservation.finishTime.isAfter(this.startTime) && this.finishTime.isAfter(reservation.startTime);
  }

  public Integer getReservationId() {
    return reservationId;
  }

  public void setReservationId(Integer reservationId) {
    this.reservationId = reservationId;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(LocalTime finishTime) {
    this.finishTime = finishTime;
  }

  public ReservableRoom getReservableRoom() {
    return reservableRoom;
  }

  public void setReservableRoom(ReservableRoom reservableRoom) {
    this.reservableRoom = reservableRoom;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
