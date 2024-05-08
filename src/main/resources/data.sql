insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('string2','123@gmail.com', '$2a$10$tYr9aE53aTLWEN3rZZ4F8ejpwiu./6q9V3Sl6udx0/.KCLE3.cYPu', 'string', '2022-05-04','MALE', 'string2', '2024-04-28T00:30:00', 'BALANCE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('string3','jona@gmail.com', '$2a$10$tYr9aE53aTLWEN3rZZ4F8ejpwiu./6q9V3Sl6udx0/.KCLE3.cYPu', '밥', '2024-04-28','FEMALE', '별명2',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('string4','kim@gmail.com', '$2a$10$tYr9aE53aTLWEN3rZZ4F8ejpwiu./6q9V3Sl6udx0/.KCLE3.cYPu', '밥', '2024-04-28','MALE', '별명3',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('string5','lee@gmail.com', '$2a$10$tYr9aE53aTLWEN3rZZ4F8ejpwiu./6q9V3Sl6udx0/.KCLE3.cYPu', '밥', '2024-04-28','FEMALE', '별명4',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('test1','test1@gmail.com', '1234', 'test', '2024-04-28','MALE', '별명5',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('test2','test2@gmail.com', '1234', '밥', '2024-04-28','FEMALE', '별명6',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('test3','test3@gmail.com', '1234', 'test', '2024-04-28','MALE', '별명7',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('test4','test4@gmail.com', '1234', 'test', '2024-04-28','MALE', '별명8',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('test5','test5@gmail.com', '1234', 'test', '2024-04-28','MALE', '별명9',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('test6','test6@gmail.com', '1234', 'test', '2024-04-28','FEMALE', '별명10',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);


-- member_roles 테이블 데이터 삽입
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
(5, 'Example Game', 'This is an example game content.', 10, 'INDOOR', 'MALEONLY', '2024-05-04 10:00:00', '2024-05-04 08:00:00', NULL, TRUE, '인천 문학 경기장2',1.0, 1.0, 'SEOUL', 'FIVEONFIVE', 1);

INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(6, 'Example Game', 'This is an example game content 2.', 10, 'INDOOR', 'MALEONLY', '2024-05-08 10:00:00', '2024-05-08 08:00:00', NULL, TRUE, '서울 abc 경기장',1.0, 1.0, 'SEOUL', 'THREEONTHREE', 2);

INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(10, 'Example Game', 'This is an example game content 2.', 10, 'INDOOR', 'FEMALEONLY', '2024-05-10 11:00:00', '2024-05-08 08:00:00', NULL, TRUE, '서울 abc 경기장',1.0, 1.0, 'SEOUL', 'THREEONTHREE', 2);


INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(7, 'Example Game', 'This is an example game content 3.', 10, 'INDOOR', 'MALEONLY', '2024-05-10 11:00:00', '2024-05-07 08:00:00', NULL, TRUE, '인천 문학 경기장',1.0, 1.0, 'SEOUL', 'FIVEONFIVE', 3);


INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(8, 'Example Game', 'This is an example game content 3.', 6, 'INDOOR', 'ALL', '2024-05-11 10:00:00', '2024-05-09 08:00:00', NULL, TRUE, '서울 a 경기장',1.0, 1.0, 'INCHEON', 'THREEONTHREE', 2);


INSERT INTO game (game_id, title, content, head_count, field_status, gender, start_date_time, created_date_time, deleted_date_time, invite_yn, address, latitude,longitude,city_name, match_format, user_id)
VALUES
(9, 'Example Game', 'This is an example game content 3.', 10, 'INDOOR', 'MALEONLY', '2024-05-12 10:00:00', '2024-05-09 08:00:00', '2024-05-09 08:00:00', TRUE, '삭제된 서울 a 경기장',1.0, 1.0, 'INCHEON', 'FIVEONFIVE', 3);

INSERT INTO participant_game (participant_id, status, created_date_time, accepted_date_time, rejected_date_time, canceled_date_time, withdrew_date_time, kickout_date_time, deleted_date_time, game_id, user_id)
VALUES
    (5, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 2),
    (6, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 3),
    (7, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 4),
    (8, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 5),
    (9, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 6),
    (10, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 7),
    (11, 'APPLY', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 8),
    (12, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 9),
    (13, 'ACCEPT', '2024-05-06T10:00:00', null,null,null,null,null,null, 8, 10);
