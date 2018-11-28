const sqlite3 = require('sqlite3').verbose();
const time = require('time');
const user = require('./user');
const uuid = require('uuid/v4');

/*
 * host
 *
 * Database interface abstraction.
 */
module.exports = function (ip, dbPath) {

    this.ip = ip;
    this.db = new sqlite3.Database(dbPath, (err) => {
        if (err) {
            return console.error(err.message);
        }
        console.log('Connected to the HRproject SQlite database.');
    });

    this.getUserID = function (email, password) {
        let query = "SELECT UserID FROM Users WHERE Email = ? AND Password = ?;";

        this.db.get(query, [email, password], (err, tuple) => {
            if (err) {
                console.error(err.message);
                return null;
            }

            if (tuple) {
                return tuple.u_id;
            } else {
                return null;
            }

        });
    }

    this.getUserFromID = function (userID) {
        let query = "SELECT * FROM Users WHERE UserID = ?;";

        this.db.get(query, [userID], (err, tuple) => {

            if (err) {
                return console.error(err.message);
            }

            if (tuple) {
                return new user(tuple.u_id, tuple.firstname, tuple.lastname, tuple.username, tuple.password, tuple.email, tuple.org_id, tuple.super_id);
            } else {
                return null;
            }

        });
    }

    this.getUser = function (email, password) {
        var userID = this.getUserID(email, password);

        if (userID) {
            return this.getUserFromID(userID);
        } else {
            return null;
        }        
    }

    this.getPunches = function (userID) {
        let query = "SELECT * FROM TimeSheet WHERE UserID = ?;";

        var output = [];

        this.db.each(query, [userID], (err, tuple) => {

            if(err) {
                return console.log(err.message);
            }

            output.push(tuple);

        });

        return output;
    }

    this.createUser = function (regCode, firstName, lastName, email, username, password) {

        let userID = uuid(); //generate userID

        let verificationQuery = "SELECT * FROM regCodes WHERE regID = ?;";

        var verified = false;
        var superID;
        var orgID;
        this.db.get(verificationQuery, [regCode], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }

            verified = (tuple.regID === regCode);
            superID = tuple.SupervisorID;
            orgID = tuple.OrgID;
        });

        if (!verified) {
            return false;
        }

        let creationQuery = "INSERT INTO Users VALUES(?,?,?,?,?,?,?,?);";

        this.db.run(creationQuery, [userID, firstName, lastName, email, username, password, superID, orgID], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });

        return true;
    }

    this.createMessage = function (senderID, recipientID, dateTime, subject, message, read, active, timeID) {
        let query = "INSERT INTO Message VALUES(?,?,?,?,?,?,?,?,?);" ;

        let msgID = uuid();

        this.db.run(query, [msgID, senderID, recipientID, dateTime, subject, message, read, active, timeID], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }

    this.createClockPunch = function (userID, dateTime, punchType, submitTime) {
        let query = "INSERT INTO TimeSheet VALUES(?,?,?,?,?);" ;

        let timeID = uuid();

        this.db.run(query, [timeID, userID, dateTime, punchType, submitTime], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }

    this.editUser = function (userID, property, newVal) {
        let query = "UPDATE Users SET " + property + " = ? WHERE UserID = ?;";

        this.db.run(query, [newVal, userID], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }

    this.editMessage = function (msgID, property, newVal) {
        let query = "UPDATE Message SET " + property + " = ? WHERE MessageID = ?;";

        this.db.run(query, [newVal, msgID], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }

    this.close = function () {
        db.close((err) => {
            if (err) {
                return console.error(err.message);
            }
            console.log('Close the database connection.');
        });
    }
}

// Security measure to prevent SQL injection
var isValid = function (arg) {
    return true;
}

//var database = new host('','');
//var me = database.getUser("myemail@whatever.tv", "secret");
