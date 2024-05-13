insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('string2','123@gmail.com', '$2a$10$tYr9aE53aTLWEN3rZZ4F8ejpwiu./6q9V3Sl6udx0/.KCLE3.cYPu', 'string', '2022-05-04','MALE', 'string2', '2024-04-28T00:30:00', 'BALANCE', 'SHOOT', 0, 0, 0.0,  true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('string3','jona@gmail.com', '$2a$10$tYr9aE53aTLWEN3rZZ4F8ejpwiu./6q9V3Sl6udx0/.KCLE3.cYPu', '밥', '2024-04-28','FEMALE', '별명2',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0,  true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability,total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('string4','kim@gmail.com', '$2a$10$tYr9aE53aTLWEN3rZZ4F8ejpwiu./6q9V3Sl6udx0/.KCLE3.cYPu', '밥', '2024-04-28','MALE', '별명3',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0,  true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability,total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('string5','lee@gmail.com', '$2a$10$tYr9aE53aTLWEN3rZZ4F8ejpwiu./6q9V3Sl6udx0/.KCLE3.cYPu', '밥', '2024-04-28','FEMALE', '별명4',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0, true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability,total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('test1','test1@gmail.com', '1234', 'test', '2024-04-28','MALE', '별명5',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0, true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability,total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('test2','test2@gmail.com', '1234', '밥', '2024-04-28','FEMALE', '별명6',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0, true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('test3','test3@gmail.com', '1234', 'test', '2024-04-28','MALE', '별명7',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0, true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability,total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('test4','test4@gmail.com', '1234', 'test', '2024-04-28','MALE', '별명8',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0, true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('test5','test5@gmail.com', '1234', 'test', '2024-04-28','MALE', '별명9',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0, true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, total_ratings, total_ratings_count, double_average_rating, email_auth)
values ('test6','test6@gmail.com', '1234', 'test', '2024-04-28','FEMALE', '별명10',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', 0, 0, 0.0, true);


-- member_roles 테이블 데이터 삽입
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ROLE_OWNER');
INSERT INTO user_roles (user_id, roles) VALUES (1, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (2, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (3, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (4, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (5, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (6, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (7, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (8, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (9, 'ROLE_USER');
INSERT INTO user_roles (user_id, roles) VALUES (10, 'ROLE_USER');

INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address,latitude,longitude, city_name, match_format, user_id)
VALUES
(4, 'Example Game', 'This is an example game content.', 10, 'INDOOR', 'MALEONLY', '2024-05-04 10:00:00', '2024-05-04 08:00:00', NULL, TRUE, '인천 문학 경기장2',1.0, 1.0, 'SEOUL', 'FIVEONFIVE', 4);


INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address,latitude,longitude, city_name, match_format, user_id)
VALUES
(5, 'Example Game', 'This is an example game content.', 10, 'INDOOR', 'MALEONLY', '2024-05-14 10:00:00', '2024-05-04 08:00:00', NULL, TRUE, '인천 문학 경기장2',1.0, 1.0, 'SEOUL', 'FIVEONFIVE', 4);

INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(6, 'Example Game', 'This is an example game content 2.', 10, 'INDOOR', 'MALEONLY', '2024-05-11 10:00:00', '2024-05-08 08:00:00', NULL, TRUE, '서울 abc 경기장',1.0, 1.0, 'SEOUL', 'THREEONTHREE', 2);

INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(10, 'Example Game', 'This is an example game content 2.', 10, 'INDOOR', 'FEMALEONLY', '2024-05-10 11:00:00', '2024-05-08 08:00:00', NULL, TRUE, '서울 abc 경기장',1.0, 1.0, 'SEOUL', 'THREEONTHREE', 2);


INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(7, 'Example Game', 'This is an example game content 3.', 10, 'INDOOR', 'MALEONLY', '2024-05-15 11:00:00', '2024-05-07 08:00:00', NULL, TRUE, '인천 문학 경기장',1.0, 1.0, 'SEOUL', 'FIVEONFIVE', 3);


INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(8, 'Example Game', 'This is an example game content 3.', 6, 'INDOOR', 'ALL', '2024-05-11 10:00:00', '2024-05-09 08:00:00', NULL, TRUE, '서울 a 경기장',1.0, 1.0, 'INCHEON', 'THREEONTHREE', 2);


INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(9, 'Example Game', 'This is an example game content 3.', 10, 'INDOOR', 'MALEONLY', '2024-05-12 10:00:00', '2024-05-09 08:00:00', '2024-05-09 08:00:00', TRUE, '삭제된 서울 a 경기장',1.0, 1.0, 'INCHEON', 'FIVEONFIVE', 3);

INSERT INTO participant_game (participant_id, status, created_date_time, accepted_date_time, rejected_date_time, canceled_date_time, withdrew_date_time, kickout_date_time, deleted_date_time, game_id, user_id)
VALUES
    (5, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 2),
    (6, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 1),
    (7, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 4),
    (8, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 5),
    (9, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 6),
    (10, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 7),
    (11, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 8),
    (12, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 9),
    (14, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 10),
    (15, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 1),
    (16, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 3),
    (17, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 4),
    (18, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 5),
    (19, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 6),
    (20, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 7),
    (21, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 8),
    (22, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 9),
    (23, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 5, 10),
    (25, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 4, 1),
    (26, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 4, 3),
    (27, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 4, 4),
    (28, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 4, 5),
    (29, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 4, 6),
    (30, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 4, 7),
    (31, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 4, 8),
    (32, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 6, 1),
    (33, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 7, 1),
    (34, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 7, 2),
    (35, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 7, 3),
    (36, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 7, 4),
    (37, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 7, 5),
    (38, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 7, 6),
    (39, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 7, 7);

INSERT INTO reports (id, content, user_user_id, reported_user_user_id, created_date_time, black_list_start_date_time)
VALUES
    (5, '11111111111111111111111111111111111111', 2, 3,'2024-05-06T10:00:00',null),
    (6, '11111111111111111111111111111111111111', 3, 4,'2024-05-06T10:00:00',null),
    (7, '11111111111111111111111111111111111111', 4, 5,'2024-05-06T10:00:00',null),
    (8, '11111111111111111111111111111111111111', 6, 7,'2024-05-06T10:00:00',null);

INSERT INTO friend (friend_id, status, created_date_time, accepted_date_time, rejected_date_time, canceled_date_time, deleted_date_time, user_id, friend_user_id)
VALUES (5, 'ACCEPT', '2024-05-10T12:00:00', '2024-05-10T13:00:00', null, null, null, 2, 3),
       (6, 'ACCEPT', '2024-05-10T12:00:00', '2024-05-10T13:00:00', null, null, null, 3, 2),
       (7, 'APPLY', '2024-05-10T12:00:00', '2024-05-10T13:00:00', null, null, null, 2, 4),
       (8, 'APPLY', '2024-05-10T12:00:00', '2024-05-10T13:00:00', null, null, null, 6, 5),
       (9, 'APPLY', '2024-05-10T12:00:00', '2024-05-10T13:00:00', null, null, null, 4, 3);

INSERT INTO invite (invite_id, invite_status, requested_date_time, canceled_date_time, accepted_date_time, rejected_date_time, deleted_date_time, sender_user_id, receiver_user_id, game_id)
VALUES (5, 'REQUEST', '2024-05-10T12:00:00', null, null, null, null, 2, 3, 8),
       (6, 'CANCEL', '2024-05-10T12:00:00', '2024-05-10T12:30:00', null, null, null, 2, 6, 10),
       (7, 'REQUEST', '2024-05-10T12:00:00', null, null, null, null, 6, 5, 8),
       (8, 'REQUEST', '2024-05-10T12:00:00', null, null, null, null, 7, 8, 8),
       (9, 'REQUEST', '2024-05-10T12:00:00', null, null, null, null, 5, 2, 8);