package tokyomap.oauth.application.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tokyomap.oauth.domain.services.reservation.*;
import tokyomap.oauth.domain.models.entities.ReservableRoom;
import tokyomap.oauth.domain.services.room.RoomService;
import tokyomap.oauth.domain.services.user.ReservationUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import tokyomap.oauth.domain.models.entities.ReservableRoomId;
import tokyomap.oauth.domain.models.entities.Reservation;
import tokyomap.oauth.domain.models.entities.User;
import tokyomap.oauth.domain.services.reservation.AlreadyReservedException;
import tokyomap.oauth.domain.services.reservation.ReservationService;
import tokyomap.oauth.domain.services.reservation.UnavailableReservationException;

@Controller
@RequestMapping("/reservations/{date}/{roomId}")
@SessionAttributes(types = ReservationForm.class)
public class ReservationsController {
  private final RoomService roomService;
  private final ReservationService reservationService;

  @Autowired // inject by type
//  @Qualifier("string standing for beans's use case") // inject by name with @Autowired
  public ReservationsController(RoomService roomService, ReservationService reservationService) {
    this.roomService = roomService;
    this.reservationService = reservationService;
  }

  // store the returned object to the Model before a handler is called
  // the object is stored in the http session at the same time with name "reservationForm" thanks to `@SessionAttributes(types = ReservationForm.class)`
  @ModelAttribute("reservationForm")
  public ReservationForm setUpForm() {
    ReservationForm form = new ReservationForm();
    form.setStartTime(LocalTime.of(9,0));
    form.setFinishTime(LocalTime.of(10,0));
    return form;
  }

  @PostMapping("/cancel")
  public String cancel(
      @AuthenticationPrincipal ReservationUserDetails reservationUserDetails,
      @RequestParam("reservationId") Integer reservationId,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
      @PathVariable("roomId") Integer roomId,
      Model model // the interface holding the data to be delivered to the view
  ) {
    User user = reservationUserDetails.getUser();

    try {
      this.reservationService.cancel(reservationId, user);
    } catch (AccessDeniedException e) {
      model.addAttribute("error", e.getMessage());
      return reserveForm(date, roomId, model);
    }

    return "redirect:/reservations/{date}/{roomId}";
  }

  @PostMapping
  public String reserve(
      @Validated ReservationForm reservationForm, // fetch the form from the Model, and validate it
      BindingResult bindingResult, // store binding and input-check errors for the request
      @AuthenticationPrincipal ReservationUserDetails reservationUserDetails,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
      @PathVariable("roomId") Integer roomId,
      Model model,
      SessionStatus sessionStatus
  ) {
    if(bindingResult.hasErrors()) {
      return reserveForm(date, roomId, model);
    }

    ReservableRoom reservableRoom = new ReservableRoom(new ReservableRoomId(roomId, date));
    Reservation reservation = new Reservation();
    reservation.setStartTime(reservationForm.getStartTime());
    reservation.setFinishTime(reservationForm.getFinishTime());
    reservation.setReservableRoom(reservableRoom);
    reservation.setUser(reservationUserDetails.getUser());

    try {
      this.reservationService.reserve(reservation);
      sessionStatus.setComplete(); // the session is marked to be completed, and is destroyed after this handler is completed
    } catch (UnavailableReservationException | AlreadyReservedException e) {
      model.addAttribute("error", e.getMessage());
      return reserveForm(date, roomId, model);
    }

    return "redirect:/reservations/{date}/{roomId}";
  }

  @GetMapping
  public String reserveForm(
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
      @PathVariable("roomId") Integer roomId,
      Model model
  ) {
    ReservableRoomId reservableRoomId = new ReservableRoomId(roomId, date);
    List<Reservation> reservations = this.reservationService.findReservations(reservableRoomId);
    List<LocalTime> timeList = Stream
        .iterate(LocalTime.of(0, 0), t -> t.plusMinutes(30)).limit(24 * 2)
        .collect(Collectors.toList());
    model.addAttribute("room", this.roomService.findMeetingRoom(roomId));
    model.addAttribute("roomId", roomId);
    model.addAttribute("reservations", reservations);
    model.addAttribute("date", date);
    model.addAttribute("timeList", timeList);
    return "/reservation/reserveForm"; // returns the view name
  }

}
