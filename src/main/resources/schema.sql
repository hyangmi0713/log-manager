DO '
    begin
        if not exists (select schema_name from information_schema.schemata where schema_name=''log_manager'') THEN
            create schema log_manager;
            alter schema log_manager owner to rssadmin;

            create table log_manager.user
            (
                id            integer generated by default as identity
                    constraint user_pkey
                        primary key,
                access_at     timestamp,
                password      varchar(255),
                refresh_token varchar(255),
                roles         text[],
                update_at     timestamp,
                username      varchar(255)
                    constraint uk_sb8bbouer5wak8vyiiy4pf2bx
                        unique
            );

            alter table log_manager.user
                owner to rssadmin;

            end if;
        if not exists (select schema_name from information_schema.schemata where schema_name=''cras'') THEN
            create schema cras;
            alter schema cras owner to rssadmin;

            create table cras.cras_option
            (
                id     integer      not null
                    constraint cras_option_pkey
                        primary key,
                cras   varchar(255) not null,
                option varchar(255) not null,
                value  varchar(255) not null
            );

            alter table cras.cras_option
                owner to rssadmin;
            end if;
    end;
'  LANGUAGE plpgsql

