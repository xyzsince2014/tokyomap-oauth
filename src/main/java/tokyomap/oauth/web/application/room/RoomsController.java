package tokyomap.oauth.web.application.room;

import java.time.LocalDate;

import java.util.List;

import tokyomap.oauth.web.domain.models.entities.ReservableRoom;
import tokyomap.oauth.web.domain.services.room.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/web/rooms")
public class RoomsController {
  private final RoomService roomService;

  @Autowired
  public RoomsController(RoomService roomService) {
    this.roomService = roomService;
  }

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
