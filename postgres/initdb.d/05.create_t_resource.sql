create table if not exists t_resource (
  resource_id character varying(256) primary key
  , resource_secret character varying(256)
  , created_at timestamp not null default current_timestamp
  , updated_at timestamp not null default current_timestamp
);

begin;
insert into t_resource (resource_id, resource_secret) values ('protected-resource-1', 'protected-resource-secret-1');
commit;
