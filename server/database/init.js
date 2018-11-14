const sqlite3 = require('sqlite3').verbose();
/*
var con = sqlite3.createConnection({
    host: "localhost",
    user: "yourusername",
    password: "yourpassword"
});

con.connect(function(err) {
    if (err) throw err;
    console.log("Connected!");
    con.query("CREATE DATABASE mydb", function (err, result) {
        if (err) throw err;
        console.log("Database created");
    });
});*/

var db = new sqlite3.Database('hr.db');

db.run("CREATE TABLE Users (\n" +
    "    UserID       STRING       PRIMARY KEY\n" +
    "                              NOT NULL\n" +
    "                              UNIQUE,\n" +
    "    FirstName    STRING       NOT NULL,\n" +
    "    LastName     STRING       NOT NULL,\n" +
    "    Email        STRING       NOT NULL,\n" +
    "    Username     VARCHAR (20) NOT NULL\n" +
    "                              UNIQUE,\n" +
    "    Password     VARCHAR (20) NOT NULL,\n" +
    "    SupervisorID STRING       NOT NULL\n" +
    "                              REFERENCES Users (UserID),\n" +
    "    OrgID        STRING       REFERENCES Organization (OrgID) \n" +
    "                              NOT NULL\n" +
    ");");

db.run("CREATE TABLE Message (\n" +
    "    MessageID STRING        PRIMARY KEY\n" +
    "                            UNIQUE\n" +
    "                            NOT NULL,\n" +
    "    Sender    STRING        NOT NULL\n" +
    "                            REFERENCES Users (UserID),\n" +
    "    Recipient STRING        NOT NULL\n" +
    "                            REFERENCES Users (UserID),\n" +
    "    Timestamp DATETIME      NOT NULL,\n" +
    "    Subject   VARCHAR (255),\n" +
    "    Content   TEXT,\n" +
    "    Read      BOOLEAN       NOT NULL,\n" +
    "    Active    BOOLEAN       NOT NULL,\n" +
    "    TimeID    STRING        REFERENCES TimeSheet (TimeID) \n" +
    ");");

db.run("CREATE TABLE TimeSheet (\n" +
    "    TimeID     STRING   PRIMARY KEY\n" +
    "                        UNIQUE\n" +
    "                        NOT NULL,\n" +
    "    UserID     STRING   REFERENCES Users (UserID) \n" +
    "                        NOT NULL,\n" +
    "    Punchtime  DATETIME NOT NULL,\n" +
    "    PunchType  BOOLEAN  NOT NULL,\n" +
    "    SubmitTime DATETIME NOT NULL\n" +
    ");\n");

db.run("CREATE TABLE Organization (\n" +
    "    OrgID   STRING        PRIMARY KEY\n" +
    "                          UNIQUE\n" +
    "                          NOT NULL,\n" +
    "    OrgName VARCHAR (255) NOT NULL,\n" +
    "    Host\n" +
    ");\n");

db.run("CREATE TABLE Registration (\n" +
    "    RegKey       STRING PRIMARY KEY\n" +
    "                        UNIQUE\n" +
    "                        NOT NULL,\n" +
    "    SupervisorID STRING REFERENCES Users (UserID) \n" +
    "                        NOT NULL\n" +
    ");\n");

