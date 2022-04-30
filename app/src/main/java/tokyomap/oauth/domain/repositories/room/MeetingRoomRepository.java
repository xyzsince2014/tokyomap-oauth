package tokyomap.oauth.domain.repositories.room;

import tokyomap.oauth.domain.models.entities.MeetingRoom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Integer> {
  MeetingRoom findOneByRoomId(Integer roomId);
}
