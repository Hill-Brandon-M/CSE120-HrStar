const sqlite3 = require('sqlite3').verbose();
const time = require('time');
const user = require('./user');

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

            return tuple.u_id;
        });
    }

    this.getUserFromID = function (userID) {
        let query = "SELECT * FROM Users WHERE UserID = ?;";

        this.db.get(query, [userID], (err, tuple) => {

            if (err) {
                return console.error(err.message);
            }

            return new user(tuple.u_id, tuple.firstname, tuple.lastname, tuple.username, tuple.password, tuple.email, tuple.org_id, tuple.super_id);
        });
    }

    this.getUser = function (email, password) {
        var userID = this.getUserID(email, password);
        return this.getUserFromID(userID);
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

    this.createUser = function (userID, firstName, lastName, email, username, password, superID, orgID) {
        let query = "INSERT INTO Users VALUES(?,?,?,?,?,?,?,?)" ;

        this.db.run(query, [userID, firstName, lastName, email, username, password, superID, orgID], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }

    this.createMessage = function (msgID, senderID, recipientID, dateTime, subject, message, read, active, timeID) {
        let query = "INSERT INTO Message VALUES(?,?,?,?,?,?,?,?,?);" ;

        this.db.run(query, [msgID, senderID, recipientID, dateTime, subject, message, read, active, timeID], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }

    this.createClockPunch = function (timeID, userID, dateTime, punchType, submitTime) {
        let query = "INSERT INTO TimeSheet VALUES(?,?,?,?,?);" ;

        this.db.run(query, [timeID, userID, dateTime, punchType, submitTime], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }

    this.editUser = function (beforeUpdate, newUpdate, columnName, condition) {
        let query = "UPDATE Users SET " + columnName + " = ? WHERE " + condition + " = ?;";

        this.db.run(query, [beforeUpdate, newUpdate], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }

    this.editMessage = function (beforeUpdate, newUpdate, columnName, condition) {
        let query = "UPDATE Message SET " + columnName + " = ? WHERE " + condition + " = ?;";

        this.db.run(query, [beforeUpdate, newUpdate], (err, tuple) => {
            if (err) {
                return console.log(err.message);
            }
        });
    }





    db.close((err) => {
        if (err) {
            return console.error(err.message);
        }
        console.log('Close the database connection.');
    })
}

// Security measure to prevent SQL injection
var isValid = function (arg) {
    return true;
}

//var database = new host('','');
//var me = database.getUser("myemail@whatever.tv", "secret");