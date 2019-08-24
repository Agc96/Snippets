-- Database: Assessment Test
-- Author: Anthony Gutiérrez
-- DBMS: PostgreSQL
CREATE DATABASE assessment_test
    WITH OWNER = postgres
        ENCODING = 'UTF8'
        TABLESPACE = pg_default
        CONNECTION LIMIT = -1;


CREATE TABLE public.acl_user (
    user_id integer NOT NULL,
    user_full_name varchar(150) NOT NULL,
    user_email varchar(150) NOT NULL,
    user_password varchar(150) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    PRIMARY KEY (user_id)
);


CREATE TABLE public.acl_role (
    role_id integer NOT NULL,
    role_name varchar(150) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    PRIMARY KEY (role_id)
);


CREATE TABLE public.acl_group (
    group_id integer NOT NULL,
    group_name varchar(150) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    PRIMARY KEY (group_id)
);


CREATE TABLE public.acl_user_role (
    user_id integer NOT NULL,
    role_id integer NOT NULL,
    active boolean NOT NULL DEFAULT true
);

CREATE INDEX ON public.acl_user_role
    (user_id);
CREATE INDEX ON public.acl_user_role
    (role_id);


CREATE TABLE public.acl_user_group (
    group_id integer NOT NULL,
    user_id integer NOT NULL,
    active boolean NOT NULL DEFAULT true
);

CREATE INDEX ON public.acl_user_group
    (group_id);
CREATE INDEX ON public.acl_user_group
    (user_id);


CREATE TABLE public.acl_menu (
    menu_id integer NOT NULL,
    menu_name varchar(150) NOT NULL,
    active boolean NOT NULL DEFAULT true,
    PRIMARY KEY (menu_id)
);


CREATE TABLE public.acl_option (
    option_id integer NOT NULL,
    option_name varchar(150) NOT NULL,
    menu_id integer NOT NULL,
    active boolean NOT NULL DEFAULT true,
    PRIMARY KEY (option_id)
);

CREATE INDEX ON public.acl_option
    (menu_id);


CREATE TABLE public.acl_role_option (
    role_id integer NOT NULL,
    option_id integer NOT NULL,
    active boolean NOT NULL DEFAULT true
);

CREATE INDEX ON public.acl_role_option
    (role_id);
CREATE INDEX ON public.acl_role_option
    (option_id);


ALTER TABLE public.acl_user_role ADD CONSTRAINT FK_acl_user_role__user_id FOREIGN KEY (user_id) REFERENCES public.acl_user(user_id);
ALTER TABLE public.acl_user_role ADD CONSTRAINT FK_acl_user_role__role_id FOREIGN KEY (role_id) REFERENCES public.acl_role(role_id);
ALTER TABLE public.acl_user_group ADD CONSTRAINT FK_acl_user_group__group_id FOREIGN KEY (group_id) REFERENCES public.acl_group(group_id);
ALTER TABLE public.acl_user_group ADD CONSTRAINT FK_acl_user_group__user_id FOREIGN KEY (user_id) REFERENCES public.acl_user(user_id);
ALTER TABLE public.acl_role_option ADD CONSTRAINT FK_acl_role_option__role_id FOREIGN KEY (role_id) REFERENCES public.acl_role(role_id);
ALTER TABLE public.acl_role_option ADD CONSTRAINT FK_acl_role_option__option_id FOREIGN KEY (option_id) REFERENCES public.acl_option(option_id);
