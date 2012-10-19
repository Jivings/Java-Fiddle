# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table compiled_class_model (
  id                        varchar(255) not null,
  classdata                 varbinary(255),
  classname                 varchar(255),
  arguments                 varchar(255),
  constraint pk_compiled_class_model primary key (id))
;

create table project_model (
  id                        bigint not null,
  uuid                      varchar(255),
  code                      varchar(255),
  compilation_errors        varchar(255),
  arguments                 varchar(255),
  revision                  bigint,
  compiler_level            varchar(255),
  classname                 varchar(255),
  constraint pk_project_model primary key (id))
;

create table revision (
  uuid                      varchar(255) not null,
  compilation_errors        varchar(255),
  arguments                 varchar(255),
  revision                  bigint,
  compiler_level            varchar(255),
  classname                 varchar(255),
  constraint pk_revision primary key (uuid))
;

create table revision_model (
  uuid                      varchar(255),
  number                    bigint,
  diff                      varchar(255))
;

create sequence compiled_class_model_seq;

create sequence project_model_seq;

create sequence revision_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists compiled_class_model;

drop table if exists project_model;

drop table if exists revision;

drop table if exists revision_model;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists compiled_class_model_seq;

drop sequence if exists project_model_seq;

drop sequence if exists revision_seq;

