create table if not exists t_client (
  client_id character varying(256) primary key
  , client_secret character varying(256)
  , client_name character varying(256)
  , token_endpoint_auth_method character varying(64) not null
  , client_uri character varying(256) not null
  , redirect_uris character varying(256) not null
  , grant_types character varying(256) not null
  , response_types character varying(256) not null
  , scope character varying(256) not null
  , registration_access_token character varying(256) not null
  , registration_client_uri character varying(256) not null
  , expires_at timestamp not null
  , created_at timestamp not null default current_timestamp
  , updated_at timestamp not null default current_timestamp
);
