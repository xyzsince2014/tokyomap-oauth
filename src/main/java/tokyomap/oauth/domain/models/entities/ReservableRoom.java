package tokyomap.oauth.domain.models.entities;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "reservable_room")
public class ReservableRoom implements Serializable {
  @EmbeddedId
  private ReservableRoomId reservableRoomId;

  @ManyToOne
  @JoinColumn(name = "room_id", insertable = false, updatable = false)
  @MapsId("roomId")
  private MeetingRoom meetingRoom;

  public ReservableRoom() {}

  public ReservableRoom(ReservableRoomId reservableRoomId) {
    this.reservableRoomId = reservableRoomId;
  }

  public ReservableRoomId getReservableRoomId() {
    return reservableRoomId;
  }

  public void setReservableRoomId(ReservableRoomId reservableRoomId) {
    this.reservableRoomId = reservableRoomId;
  }

  public MeetingRoom getMeetingRoom() {
    return meetingRoom;
  }

  public void setMeetingRoom(MeetingRoom meetingRoom) {
    this.meetingRoom = meetingRoom;
  }
}
