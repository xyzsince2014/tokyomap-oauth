package tokyomap.oauth.application.room;

import java.time.LocalDate;
import java.util.List;
import tokyomap.oauth.domain.models.entities.ReservableRoom;
import tokyomap.oauth.domain.services.room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rooms")
public class RoomsController {
  // inject by field name (or property name for setter injection), unavailable for constructor injection, otherwise do injection by type
  @Autowired
  private RoomService roomService;

  // find and list reservable rooms on the path param date
  @GetMapping("/{date}")
  public String listRooms(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // converts `yyyy/mm/dd` to `yyyy-mm-dd` (ISO 8601)
      @PathVariable("date")
      LocalDate date,
      Model model
  ) {
    List<ReservableRoom> rooms = this.roomService.findReservableRooms(date);
    model.addAttribute("rooms", rooms);
    model.addAttribute("date", date);
    return "/room/listRooms";
  }

  // find and list today's reservable rooms
  @GetMapping
  public String index(Model model) {
    LocalDate today = LocalDate.now();
    model.addAttribute("date", today);
    return listRooms(today, model);
  }
}
