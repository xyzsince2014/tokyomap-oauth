package tokyomap.oauth.web.domain.services.room;

import java.time.LocalDate;
import java.util.List;

import tokyomap.oauth.web.domain.models.entities.MeetingRoom;
import tokyomap.oauth.web.domain.models.entities.ReservableRoom;
import tokyomap.oauth.web.domain.repositories.room.MeetingRoomRepository;
import tokyomap.oauth.web.domain.repositories.room.ReservableRoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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
   * @return
   */
  public List<ReservableRoom> findReservableRooms(LocalDate reservedDate) {
    return this.reservableRoomRepository.findByReservableRoomId_reservedDateOrderByReservableRoomId_roomIdAsc(reservedDate);
  }

  public MeetingRoom findMeetingRoom(Integer roomId) {
    return this.meetingRoomRepository.findOneByRoomId(roomId);
  }
}
