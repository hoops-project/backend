insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('bob','bob@gmail.com', '1234', '밥', '2024-04-28','MALE', '별명1', '2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('jona','jona@gmail.com', '1234', '밥', '2024-04-28','FEMALE', '별명2',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('kim','kim@gmail.com', '1234', '밥', '2024-04-28','MALE', '별명3',
'2024-04-28T00:30:00', 'AGGRESSIVE', 'SHOOT', true);

insert into users(id, email, password, name, birthday, gender, nick_name, created_date_time, play_style, ability, email_auth)
values ('lee','lee@gmail.com', '1234', '밥', '2024-04-28','FEMALE', '별명4',
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