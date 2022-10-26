create table if not exists "USERS"
(
    user_id    INTEGER               not null,
    user_email CHARACTER VARYING(60) not null,
    login      CHARACTER VARYING(60) not null,
    name       CHARACTER VARYING(60),
    birthday   DATE,
    constraint "Users_pk"
        primary key (user_id)
);

create table if not exists "FILMS"
(
    film_id      INTEGER                not null,
    film_name    CHARACTER VARYING(60)  not null,
    description  CHARACTER VARYING(300),
    release_date DATE,
    duration     INTEGER,
    rating       INTEGER,
    mpa          INTEGER,
    constraint "Films_pk"
        primary key (film_id)
);

create table if not exists "FILMS_LIKES"
(
    film_id INTEGER not null,
    user_id INTEGER not null,
    constraint "Films_likes_pk"
        primary key (user_id, film_id),
    constraint "Films_likes_Films_fk"
        foreign key (film_id) references "FILMS",
    constraint "Films_likes_Users_fk"
        foreign key (user_id) references "USERS"
);

create table if not exists "FILMS_GENRES"
(
    film_id INTEGER not null,
    genre_id INTEGER not null,
    constraint "Films_genres_pk"
        primary key (film_id, genre_id),
    constraint "Films_genres_Films_fk"
        foreign key (film_id) references "FILMS"
);

create table if not exists "USERS_FRIENDS"
(
    user_id     INTEGER not null,
    friend_id   INTEGER not null,
    is_accepted BOOLEAN not null,
    constraint "Users_friends_pk"
        primary key (user_id, friend_id),
    constraint "Users_friends_friend_id_Users_user_id_fk"
        foreign key (friend_id) references "USERS",
    constraint "Users_friends_user_id_Users_fk"
        foreign key (user_id) references "USERS"
);

create table if not exists "FILMS"
(
    film_id      INTEGER                not null,
    film_name    CHARACTER VARYING(60)  not null,
    description  CHARACTER VARYING(300),
    release_date DATE,
    duration     INTEGER,
    rating       INTEGER,
    mpa_id       INTEGER,
    genre_id     INTEGER,
    constraint "Films_pk"
        primary key (film_id)
);
