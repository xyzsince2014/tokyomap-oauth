package tokyomap.oauth.domain.services.room;

import java.time.LocalDate;
import java.util.List;

import tokyomap.oauth.domain.models.entities.MeetingRoom;
import tokyomap.oauth.domain.models.entities.ReservableRoom;
import tokyomap.oauth.domain.repositories.room.MeetingRoomRepository;
import tokyomap.oauth.domain.repositories.room.ReservableRoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {
  private final ReservableRoomRepository reservableRoomRepository;
  private final MeetingRoomRepository meetingRoomRepository;

  @Autowired
  public RoomService(ReservableRoomRepository reservableRoomRepository, MeetingRoomRepository meetingRoomRepository) {
    this.reservableRoomRepository = reservableRoomRepository;
    this.meetingRoomRepository = meetingRoomRepository;
  }

  /**
   * find reservable rooms by reservedDate
   * @param reservedDate
   * @return List<ReservableRoom>
   */
  public List<ReservableRoom> findReservableRooms(LocalDate reservedDate) {

    // FetchType.EAGER: fetch immediately
    List<ReservableRoom> reservableRoomList = this.reservableRoomRepository.findByReservableRoomId_reservedDateOrderByReservableRoomId_roomIdAsc(reservedDate);

    // FetchType.LAZY: fetch when needed
    // ReservableRoomId reservableRoomId = reservableRoomList.get(0).getReservableRoomId();
    // System.out.println("reservableRoomId = " + reservableRoomId);

    return reservableRoomList;
  }

  /**
   * find meeting room by roomId
   * @param roomId
   * @return MeetingRoom
   */
  public MeetingRoom findMeetingRoom(Integer roomId) {
    return this.meetingRoomRepository.findOneByRoomId(roomId);
  }
}
