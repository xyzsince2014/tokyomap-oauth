package tokyomap.oauth.web.domain.models.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @Embeddable ReservableRoomId, which is embedded into @EmbeddedId ReservableId.
 */
@Embeddable
public class ReservableRoomId implements Serializable {
  @Column(name = "room_id")
  private Integer roomId;

  @Column(name = "reserved_date", columnDefinition = "DATE")
  private LocalDate reservedDate;

  public ReservableRoomId() {}

  public ReservableRoomId(Integer roomId, LocalDate reservedDate) {
    this.roomId = roomId;
    this.reservedDate = reservedDate;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ((reservedDate == null) ? 0 : reservedDate.hashCode()); // todo: needed?
    result = PRIME * ((roomId == null) ? 0 : roomId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass())  return false;

    ReservableRoomId other = (ReservableRoomId) obj;

    if (reservedDate == null) {
      if (other.reservedDate != null) return false;
    } else if (!reservedDate.equals(other.reservedDate)) return false;

    if (roomId == null) {
      if (other.roomId != null) return false;
    } else if (!roomId.equals(other.roomId)) return false;

    return true;
  }

  public Integer getRoomId() {
    return roomId;
  }

  public void setRoomId(Integer roomId) {
    this.roomId = roomId;
  }

  public LocalDate getReservedDate() {
    return reservedDate;
  }

  public void setReservedDate(LocalDate reservedDate) {
    this.reservedDate = reservedDate;
  }
}
