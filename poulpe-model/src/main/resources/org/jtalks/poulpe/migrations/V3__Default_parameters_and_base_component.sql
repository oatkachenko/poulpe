--
-- Copyright (C) 2011  JTalks.org Team
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public
-- License as published by the Free Software Foundation; either
-- version 2.1 of the License, or (at your option) any later version.
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
-- Lesser General Public License for more details.
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
--

CREATE TABLE `BASE_COMPONENTS` (
  `COMPONENT_TYPE` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`COMPONENT_TYPE`)
);

INSERT INTO `BASE_COMPONENTS` (`COMPONENT_TYPE`) VALUES ('FORUM');
INSERT INTO `BASE_COMPONENTS` (`COMPONENT_TYPE`) VALUES ('ARTICLE');
INSERT INTO `BASE_COMPONENTS` (`COMPONENT_TYPE`) VALUES ('ADMIN_PANEL');


CREATE TABLE `DEFAULT_PROPERTIES` (
  `PROPERTY_ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `UUID` VARCHAR(255) NOT NULL,
  `BASE_COMPONENT_TYPE` VARCHAR(20) DEFAULT NULL,
  `NAME` VARCHAR(255) NOT NULL,
  `VALUE` VARCHAR(255) DEFAULT NULL,
  `VALIDATION_RULE` VARCHAR(64) NOT NULL DEFAULT '',
  
  PRIMARY KEY (`PROPERTY_ID`),
  UNIQUE KEY `UUID` (`UUID`),
  KEY `FK_BASE_COMPONENT` (`BASE_COMPONENT_TYPE`),
  CONSTRAINT `FK_BASE_COMPONENT` FOREIGN KEY (`BASE_COMPONENT_TYPE`) REFERENCES `BASE_COMPONENTS` (`COMPONENT_TYPE`)
);

INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES 
	('7149f814-3421-43f0-b890-b79b74253cd6', 'FORUM', 'jcommune.name', 'JCommune', '');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES 
	('f989f4d4-877d-44f8-a0aa-ed26196c93d7', 'FORUM', 'jcommune.caption', 'The best forum engine ever', '');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES 
	('d34c48eb-8afb-4c28-b61a-82c81c33cd26', 'FORUM', 'jcommune.post_preview_size', '200', '/[0-9]+/');
INSERT INTO `DEFAULT_PROPERTIES` (`UUID`, `BASE_COMPONENT_TYPE`, `NAME`, `VALUE`, `VALIDATION_RULE`) VALUES 
	('3f48867c-3a9d-410f-94c0-40945e26154e', 'FORUM', 'jcommune.session_timeout', '24', '/[0-9]+/');
	