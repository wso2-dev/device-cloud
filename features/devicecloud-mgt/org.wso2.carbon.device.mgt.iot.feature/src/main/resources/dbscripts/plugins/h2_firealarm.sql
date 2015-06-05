
-- -----------------------------------------------------
-- Table `ARDUINO_DEVICE`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `FIREALARM_DEVICE` (
  `FIREALARM_DEVICE_ID` VARCHAR(45) NOT NULL ,
  `DEVICE_NAME` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`FIREALARM_DEVICE_ID`) );

-- -----------------------------------------------------
-- Table `ARDUINO_FEATURE`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ARDUINO_FEATURE` (
  `ID` INT NOT NULL AUTO_INCREMENT ,
  `CODE` VARCHAR(45) NOT NULL,
  `NAME` VARCHAR(100) NULL ,
  `DESCRIPTION` VARCHAR(200) NULL ,
  PRIMARY KEY (`ID`) );

