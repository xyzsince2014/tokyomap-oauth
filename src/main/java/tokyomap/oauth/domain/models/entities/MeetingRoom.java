package tokyomap.oauth.domain.models.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "meeting_room")
public class MeetingRoom implements Serializable {
  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  @Column(name = "room_id")
  private Integer roomId;

  @Column(name = "room_name")
  private String roomName;

  public MeetingRoom() {}

  public Integer getRoomId() {
    return this.roomId;
  }

  public void setRoomId(Integer roomId) {
    this.roomId = roomId;
  }

  public String getRoomName() {
    return this.roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }
}
