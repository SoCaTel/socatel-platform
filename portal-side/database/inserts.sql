-- create user (if required)
#CREATE USER 'socatel'@'127.0.0.1' identified by '<new password>';
#GRANT ALL PRIVILEGES ON * TO 'socatel'@'127.0.0.1';

-- change the DB below (if required).
use socatel;

start transaction;

-- Languages --
INSERT INTO `so_language` (`language_id`, `language_code`, `language_name`) VALUES (1, 'en', 'English');
INSERT INTO `so_language` (`language_id`, `language_code`, `language_name`) VALUES (2, 'es', 'Spanish');
INSERT INTO `so_language` (`language_id`, `language_code`, `language_name`) VALUES (3, 'fi', 'Finnish');
INSERT INTO `so_language` (`language_id`, `language_code`, `language_name`) VALUES (4, 'hu', 'Hungarian');
INSERT INTO `so_language` (`language_id`, `language_code`, `language_name`) VALUES (5, 'ca', 'Catalan');
INSERT INTO `so_language` (`language_id`, `language_code`, `language_name`) VALUES (6, 'nl', 'Dutch');
INSERT INTO `so_language` (`language_id`, `language_code`, `language_name`) VALUES (7, 'fr', 'French');

-- Themes --
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (1, 'Accessibility');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (2, 'Activities');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (3, 'Administration');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (4, 'Communication');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (5, 'Digitalisation');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (6, 'Equipment');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (7, 'Food');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (8, 'Health');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (9, 'Mobility');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (10, 'Social');
INSERT INTO `so_theme` (`theme_id`, `theme_name`) VALUES (11, 'Wellness');

-- Skills --
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (1, 'Accounting');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (2, 'Analytics');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (3, 'Banking');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (4, 'Computer');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (5, 'Data');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (6, 'Design');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (7, 'Financial');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (8, 'Hardware');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (9, 'Healthcare');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (10, 'Information');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (11, 'Technology');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (12, 'Legal');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (13, 'Medical');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (14, 'Pharmaceutical');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (15, 'Science');
INSERT INTO `so_skill` (`skill_id`, `skill_name`) VALUES (16, 'Teaching');

-- Localities --
insert into so_locality (locality_id, locality_name, locality_parent_id) values (999, 'Paneuropean' , null);

insert into so_locality (locality_id, locality_name, locality_parent_id) values (1000, 'Spain' , null);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1001, 'Finland' , null);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1002, 'Hungary' , null);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1003, 'Ireland' , null);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1008, 'Netherlands' , null);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1013, 'France' , null);

insert into so_locality (locality_id, locality_name, locality_parent_id) values (1004, 'Vilanova' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1005, 'Tampere' , 1001);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1006, 'Budapest' , 1002);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1007, 'Dublin' , 1003);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1009, 'Eindhoven' , 1008);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1010, 'Tarragona' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1011, 'Barcelona' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1012, 'Madrid' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (1014, 'Nice' , 1013);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2001, 'Álava' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2002, 'Albacete' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2003, 'Alicante' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2004, 'Almería' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2005, 'Asturias' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2006, 'Ávila' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2007, 'Badajoz' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2008, 'Burgos' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2009, 'Cáceres' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2010, 'Cádiz' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2011, 'Cantabria' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2012, 'Castellón' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2013, 'Ciudad Real' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2014, 'Córdoba' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2015, 'La Coruña' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2016, 'Cuenca' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2017, 'Gerona' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2018, 'Granada' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2019, 'Guadalajara' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2020, 'Guipúzcoa' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2021, 'Huelva' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2022, 'Huesca' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2023, 'Islas Baleares' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2024, 'Jaén' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2025, 'León' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2026, 'Lérida' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2027, 'Lugo' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2028, 'Málaga' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2029, 'Murcia' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2030, 'Navarra' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2031, 'Orense' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2032, 'Palencia' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2033, 'Las Palmas' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2034, 'Pontevedra' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2035, 'La Rioja' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2036, 'Salamanca' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2037, 'Segovia' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2038, 'Sevilla' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2039, 'Soria' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2040, 'Santa Cruz de Tenerife' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2041, 'Teruel' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2042, 'Toledo' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2043, 'Valencia' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2044, 'Valladolid' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2045, 'Vizcaya' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2046, 'Zamora' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2047, 'Zaragoza' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2048, 'Ceuta' , 1000);
insert into so_locality (locality_id, locality_name, locality_parent_id) values (2049, 'Melilla' , 1000);

-- Documents --
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (1, null, null, null, 'face1.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (2, null, null, null, 'face2.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (3, null, null, null, 'face3.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (4, null, null, null, 'face4.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (5, null, null, null, 'face5.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (6, null, null, null, 'face6.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (7, null, null, null, 'face7.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (8, null, null, null, 'face8.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (9, null, null, null, 'face9.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (10, null, null, null, 'face10.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (11, null, null, null, 'face11.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (12, null, null, null, 'face12.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (13, null, null, null, 'face13.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (14, null, null, null, 'face14.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (15, null, null, null, 'face15.jpg', 'image/jpeg');
INSERT INTO socatel.so_document (document_id, post_id, message_id, group_id, document_name, document_type) VALUES (16, null, null, null, 'face16.jpg', 'image/jpeg');

-- run this command only if all is good (no errors above):
commit;

