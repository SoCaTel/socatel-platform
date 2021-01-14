use socatel;
start transaction;

-- Notifications --
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (1, 'notif_1', 1, DATE('2019-01-10 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (2, 'notif_2', 1, DATE('2019-01-11 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (3, 'notif_3', 1, DATE('2019-01-12 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (4, 'notif_4', 1, DATE('2019-01-13 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (5, 'notif_5', 1, DATE('2019-01-14 00:00:00'));

INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (6, 'notif_6', 1, DATE('2019-01-01 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (7, 'notif_7', 1, DATE('2019-01-12 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (8, 'notif_8', 1, DATE('2019-01-03 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (9, 'notif_9', 1, DATE('2019-01-04 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (10, 'notif_10', 1, DATE('2019-01-05 00:00:00'));

INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (11, 'notif_11', 1, DATE('2019-01-06 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (12, 'notif_12', 1, DATE('2019-01-07 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (13, 'notif_13', 1, DATE('2019-01-08 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (14, 'notif_14', 1, DATE('2019-01-09 00:00:00'));
INSERT INTO `so_notification` (`notif_id`, notif_text, `user_id`, `notif_time`) VALUES (15, 'notif_15', 1, DATE('2018-01-02 00:00:00'));

-- History --
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (1, 'history_1', 1, DATE('2019-01-10 00:00:00'));
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (2, 'history_2', 1, DATE('2019-01-11 00:00:00'));
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (3, 'history_3', 1, DATE('2019-01-12 00:00:00'));
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (4, 'history_4', 1, DATE('2019-01-13 00:00:00'));
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (5, 'history_5', 1, DATE('2019-01-14 00:00:00'));

INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (6, 'history_6', 1, DATE('2019-01-01 00:00:00'));
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (7, 'history_7', 1, DATE('2019-01-02 00:00:00'));
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (8, 'history_8', 1, DATE('2019-01-03 00:00:00'));
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (9, 'history_9', 1, DATE('2019-01-04 00:00:00'));
INSERT INTO `so_history` (history_id, history_text, user_id, history_time) VALUES (10, 'history_10', 1, DATE('2019-01-05 00:00:00'));

commit;