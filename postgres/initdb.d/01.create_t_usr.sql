create table if not exists t_usr (
  sub character varying(256) primary key
  , name character varying(256)
  , family_name character varying(256)
  , given_name character varying(256)
  , middle_name character varying(256)
  , nickname character varying(256)
  , preferred_username character varying(256)
  , profile character varying(256)
  , picture character varying(256)
  , website character varying(256)
  , zoneinfo character varying(256)
  , locale character varying(256)
  , password character varying(256) not null
  , email character varying(256)
  , email_verified boolean not null default false
  , address character varying(256)
  , phone character varying(256)
  , phone_number_verified boolean not null default false
  , scope character varying(256) not null
  , created_at timestamp without time zone not null default current_timestamp
  , updated_at timestamp without time zone not null default current_timestamp
);

-- insert dummy users
begin;

insert into
  t_usr (sub, name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture, website, zoneinfo, locale, password, email, email_verified, address, phone, phone_number_verified, scope, created_at, updated_at)
values
  ('9XE3-JI34-99999A', 'John Doe', 'Doe', 'John', 'middle name', 'nickname', 'preferred username', 'profile', 'picture', 'website', 'zoneinfo', 'locale', 'password', 'john.doe@yahoo.ac.uk', true, '{"streetAddress":"1234 Main Street", "locality": "Anyton", "region": "IL", "postalCode": "60609", "country": "US"}', '+14844608082', true, 'read write delete openid profile email address phone fruit veggies meats', now(), now());

insert into
  t_usr (sub, name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture, website, zoneinfo, locale, password, email, email_verified, address, phone, phone_number_verified, scope, created_at, updated_at)
values
  ('00u1sneigDs6Rolmu2p6', 'Ken Vinson', 'Vinson', 'Ken', 'hoge', 'kevin', 'kb', 'profile', null, null, 'Thailand/Bangkok', 'en-US', 'password', 'ken.vinson@looker.com', true, '{"streetAddress":"999 Patpong Street", "locality": "unknown", "region": "Central Thailand", "postalCode": "10###", "country": "Thailand"}', '+66977830174', true, 'read write openid email address', now(), now());

insert into
  t_usr (sub, name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture, website, zoneinfo, locale, password, email, email_verified, address, phone, phone_number_verified, scope, created_at, updated_at)
values
  ('1ZT5-OE63-57383B', 'Bob Saget', 'Saget', 'Bob', 'fuga', null, 'bob', 'Robert Lane Saget (May 17, 1956 – January 9, 2022) was an American stand-up comedian, actor, and television host.', 'https://en.wikipedia.org/wiki/Bob_Saget#/media/File:Bob_Saget,_Behind_The_Velvet_Rope_TV_.05.jpg', 'https://en.wikipedia.org/wiki/Bob_Saget', 'Japan/Tokyo', 'ja-JP', 'secret', 'bob_saget@gmail.com', false, '{"streetAddress": "2-4 Ikebukuro", "locality": "Toshima", "region": "Kanto", "postalCode": "1710014", "country": "JP"}', '+8180123345678', true, 'read delete openid address phone', now(), now());

insert into
  t_usr (sub, name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture, website, zoneinfo, locale, password, email, email_verified, address, phone, phone_number_verified, scope, created_at, updated_at)
values
  ('F5Q1-L6LGG-959FS', 'Donald Anderson', 'Anderson', 'Donald', null, 'donald', 'D.A.', null, null, null, null, null, 'password!', 'donald@yahoo.co.uk', true, '{"streetAddress": "9999 Main Street", "locality": "Unknown", "region": "NY", "postalCode": "10101", "country": "UK"}', '+0000000000', false, 'read openid profile email address', now(), now());

commit;
