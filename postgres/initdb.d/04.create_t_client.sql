create table if not exists t_client (
  client_id character varying(256) primary key
  , client_secret character varying(256)
  , client_name character varying(256)
  , token_endpoint_auth_method character varying(64) not null
  , client_uri character varying(256) not null
  , redirect_uris character varying(256) not null
  , grant_types character varying(256) not null
  , response_types character varying(256) not null
  , scopes character varying(256) not null
  , registration_access_token character varying(256) not null
  , registration_client_uri character varying(256) not null
  , expires_at timestamp not null
  , created_at timestamp not null default current_timestamp
  , updated_at timestamp not null default current_timestamp
);

-- insert a dummy client
begin;

insert into
  t_client (
    client_id, client_name
    , token_endpoint_auth_method
    , client_uri
    , redirect_uris
    , grant_types
    , response_types
    , scopes
    , registration_access_token
    , registration_client_uri
    , expires_at
  )
values
  (
    'sLoBOeuIkRtEH7rXmQeCjeuc8Iz4ub1t'
    , 'clientWebApp'
    , 'CLIENT_SECRET_BASIC'
    , 'http://localhost:9000/'
    , 'http://localhost:9000/callback http://localhost:9000/dummy'
    , 'AUTHORISATION_CODE REFRESH_TOKEN PASSWORD CLIENT_CREDENTIALS'
    , 'CODE TOKEN'
    , 'read write delete openid profile email address phone'
    , 'AygYFFcuIzQFmn7eWc932MMwqLuWkLJq'
    , 'http://localhost:9001/register/sLoBOeuIkRtEH7rXmQeCjeuc8Iz4ub1t'
    , '2099-01-21 20:26:09'
  );

commit;
