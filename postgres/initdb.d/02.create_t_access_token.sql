create table if not exists t_access_token (
  access_token character varying(2048) primary key
  , created_at timestamp not null default current_timestamp
  , updated_at timestamp not null default current_timestamp
);
