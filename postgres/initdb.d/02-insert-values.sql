begin;

copy meeting_room (room_name) from '/var/tmp/meeting-room.csv' header csv delimiter ',';

insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE, 1);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE + 1, 1);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE - 1, 1);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE + 2, 2);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE, 2);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE - 2, 2);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE, 3);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE - 3, 3);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE + 3, 3);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE, 4);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE + 4, 4);
insert into reservable_room (reserved_date, room_id) values (CURRENT_DATE - 4, 4);

copy usr (user_id, given_name, family_name, role, password) from '/var/tmp/usr.csv' header csv delimiter ',';

commit;
