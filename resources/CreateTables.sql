CREATE TABLE Stations (
                          StationId varchar(64) NOT NULL,
                          StationName varchar(256) NOT NULL,
                          Latitude decimal(18,6) NOT NULL,
                          Longitude decimal(18,6) NOT NULL,
                          TimeZone varchar(64) NOT NULL
                              PRIMARY KEY (StationId)
);
CREATE TABLE Measurement (
                             StationId varchar(64) NOT NULL,
                             MeasureDate DateTime NOT NULL,
                             MeasureDateLocal DateTime NOT NULL
                                 PRIMARY KEY (StationId, MeasureDate)
);
CREATE TABLE MeasurementDate (
                                 StationId varchar(64) NOT NULL,
                                 MeasureDate DateTime NOT NULL,
                                 MeasureType varchar(64) NOT NULL,
                                 Value DateTime NOT NULL,
                                 PRIMARY KEY (StationId, MeasureDate, MeasureType)
);
CREATE TABLE MeasurementString (
                                   StationId varchar(64) NOT NULL,
                                   MeasureDate DateTime NOT NULL,
                                   MeasureType varchar(64) NOT NULL,
                                   Value varchar(1024) NOT NULL,
                                   PRIMARY KEY (StationId, MeasureDate, MeasureType)
);
CREATE TABLE MeasurementDecimal (
                                    StationId varchar(64) NOT NULL,
                                    MeasureDate DateTime NOT NULL,
                                    MeasureType varchar(64) NOT NULL,
                                    Value decimal(18,6) NOT NULL,
                                    PRIMARY KEY (StationId, MeasureDate, MeasureType)
);
INSERT INTO Stations(StationId, StationName, TimeZone)
VALUES('9EF7526AD5386598304A2D7BD1C0E400','Aulich (EasyWeatherPro_V5.0.9)','Europe/Berlin');
