CREATE TABLE Users (
    UserID       STRING       PRIMARY KEY
                              NOT NULL
                              UNIQUE,
    FirstName    STRING       NOT NULL,
    LastName     STRING       NOT NULL,
    Email        STRING       NOT NULL,
    Username     VARCHAR (20) NOT NULL
                              UNIQUE,
    Password     VARCHAR (20) NOT NULL,
    SupervisorID STRING       NOT NULL
                              REFERENCES Users (UserID),
    OrgID        STRING       REFERENCES Organization (OrgID)
                              NOT NULL
);

CREATE TABLE Message (
    MessageID STRING        PRIMARY KEY
                            UNIQUE
                            NOT NULL,
    Sender    STRING        NOT NULL
                            REFERENCES Users (UserID),
    Recipient STRING        NOT NULL
                            REFERENCES Users (UserID),
    Timestamp DATETIME      NOT NULL,
    Subject   VARCHAR (255),
    Content   TEXT,
    Read      BOOLEAN       NOT NULL,
    Active    BOOLEAN       NOT NULL,
    TimeID    STRING        REFERENCES TimeSheet (TimeID)
);

CREATE TABLE TimeSheet (
    TimeID     STRING   PRIMARY KEY
                        UNIQUE
                        NOT NULL,
    UserID     STRING   REFERENCES Users (UserID)
                        NOT NULL,
    Punchtime  DATETIME NOT NULL,
    PunchType  BOOLEAN  NOT NULL,
    SubmitTime DATETIME NOT NULL
);

CREATE TABLE Organization (
    OrgID   STRING        PRIMARY KEY
                          UNIQUE
                          NOT NULL,
    OrgName VARCHAR (255) NOT NULL,
    Host
);

CREATE TABLE Registration (
    RegKey       STRING PRIMARY KEY
                        UNIQUE
                        NOT NULL,
    SupervisorID STRING REFERENCES Users (UserID)
                        NOT NULL
);
