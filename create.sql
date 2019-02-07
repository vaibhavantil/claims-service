create sequence hibernate_sequence start 1 increment 1
create table asset (id varchar(255) not null, date timestamp, user_id varchar(255), claims_id varchar(255), primary key (id))
create table association_value_entry (id int8 not null, association_key varchar(255) not null, association_value varchar(255), saga_id varchar(255) not null, saga_type varchar(255), primary key (id))
create table claim_entity (id varchar(255) not null, audiourl varchar(255), registration_date timestamp, reserve float8, state varchar(255), type varchar(255), user_id varchar(255), primary key (id))
create table data_item (id varchar(255) not null, date timestamp, name varchar(255), received boolean, title varchar(255), type int4, user_id varchar(255), value varchar(255), claims_id varchar(255), primary key (id))
create table domain_event_entry (global_index int8 not null, event_identifier varchar(255) not null, meta_data oid, payload oid not null, payload_revision varchar(255), payload_type varchar(255) not null, time_stamp varchar(255) not null, aggregate_identifier varchar(255) not null, sequence_number int8 not null, type varchar(255), primary key (global_index))
create table event (id varchar(255) not null, date timestamp, text varchar(255), type varchar(255), user_id varchar(255), claims_id varchar(255), primary key (id))
create table file_uploads (id int8 not null, claims_id uuid, content_type varchar(255), data bytea, name varchar(255), image_id uuid, meta_info varchar(255), size int8, user_id varchar(255), primary key (id))
create table note (id varchar(255) not null, date timestamp, fileurl varchar(255), text varchar(255), user_id varchar(255), claims_id varchar(255), primary key (id))
create table payment (id varchar(255) not null, amount float8, date timestamp, ex_gratia boolean, note varchar(255), payout_date timestamp, user_id varchar(255), claims_id varchar(255), primary key (id))
create table saga_entry (saga_id varchar(255) not null, revision varchar(255), saga_type varchar(255), serialized_saga oid, primary key (saga_id))
create table snapshot_event_entry (aggregate_identifier varchar(255) not null, sequence_number int8 not null, type varchar(255) not null, event_identifier varchar(255) not null, meta_data oid, payload oid not null, payload_revision varchar(255), payload_type varchar(255) not null, time_stamp varchar(255) not null, primary key (aggregate_identifier, sequence_number, type))
create table token_entry (processor_name varchar(255) not null, segment int4 not null, owner varchar(255), timestamp varchar(255) not null, token oid, token_type varchar(255), primary key (processor_name, segment))
create index IDXs2yi8bobx8dd4ee6t63dufs6d on association_value_entry (saga_id, association_key)
alter table domain_event_entry add constraint UK8s1f994p4la2ipb13me2xqm1w unique (aggregate_identifier, sequence_number)
alter table domain_event_entry add constraint UK_fwe6lsa8bfo6hyas6ud3m8c7x unique (event_identifier)
alter table snapshot_event_entry add constraint UK_e1uucjseo68gopmnd0vgdl44h unique (event_identifier)
alter table asset add constraint FKoj3861ahk7fpq9ct9lm56bj3k foreign key (claims_id) references claim_entity
alter table data_item add constraint FKk2sgsjbwuk5978pt74jx6934s foreign key (claims_id) references claim_entity
alter table event add constraint FKqghasbht3upglahg20baurs6p foreign key (claims_id) references claim_entity
alter table note add constraint FK23stuy899q9qf65l7dxgdfjsh foreign key (claims_id) references claim_entity
alter table payment add constraint FKeh893rj9wb3xgdwvk13wn7ktx foreign key (claims_id) references claim_entity
