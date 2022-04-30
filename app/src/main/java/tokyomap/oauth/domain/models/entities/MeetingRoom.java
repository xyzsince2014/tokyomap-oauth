package tokyomap.oauth.domain.models.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "meeting_room") // map this entity to meeting_room table
public class MeetingRoom implements Serializable {

  @Id // room_id is the PK
  @GeneratedValue(strategy =  GenerationType.IDENTITY) // JPA generates values for room_id column of the table
  @Column(name = "room_id") // map roomId property to room_id column of the table
  private Integer roomId;

  @Column(name = "room_name") // map roomName to ROOMNAME column if it were not for the annotation
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
