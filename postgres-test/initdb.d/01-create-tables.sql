create table if not exists meeting_room (
  room_id serial primary key,
  room_name character varying(256) not null
);

create table if not exists reservable_room (
  reserved_date date,
  room_id int references meeting_room (room_id),
  primary key (reserved_date, room_id)
);

create table if not exists usr (
  user_id character varying(256) primary key,
  given_name character varying(256) not null,
  family_name character varying(256) not null,
  password character varying(256) not null,
  role character varying(256) not null
);

create table if not exists reservation (
  reservation_id serial primary key,
  start_time TIME not null,
  finish_time TIME not null,
  reserved_date DATE not null,
  room_id INT not null,
  user_id character varying(256) not null references usr (user_id)
);

alter table reservation add foreign key (reserved_date, room_id) references reservable_room (reserved_date, room_id);
