create table if not exists t_rsa_public_key (
  kid character varying(64) primary key
  ,rsa_public_key bytea not null
  , created_at timestamp not null default current_timestamp
  , updated_at timestamp not null default current_timestamp
);
