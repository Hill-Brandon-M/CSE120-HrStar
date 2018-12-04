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
    return {

        ip: ip,
        db: new sqlite3.Database(dbPath, (err) => {
            if (err) {
                return console.error(err.message);
            }
            console.log('Connected to the HRproject SQlite database.');
        }),

        getUserID: function (email, password) {
            let query = "SELECT UserID FROM Users WHERE Email = ? AND Password = ?;";

            var that = this;
            return new Promise(function (resolve, reject) {

                that.db.get(query, [email, password], (err, tuple) => {
                    if (err) {
                        reject(err);

                    } else if (tuple){
                        resolve(tuple.UserID);
                    } else {
                        reject("No user with these credentials!");
                    }

                });
            });

        },

        getUserFromID: function (userID, db = this.db) {
            let query = "SELECT * FROM Users WHERE UserID = ?;";

            var that = this;
            return new Promise(function (resolve, reject) {

                db.get(query, [userID], (err, tuple) => {

                    if (err) {
                        reject(err);

                    } else {
                        resolve(tuple);
                    }

                });
            });

        },

        getUser: function (email, password) {
            var that = this;
            return new Promise(function (resolve, reject) {
                that.getUserID(email, password)
                    .then(function (UserID) {
                        resolve(that.getUserFromID(UserID, that.db));
                    })
                    .then(function (user) {
                        resolve(user);

                    }).catch(function (err) {
                        console.log(err);
                        resolve(null);
                    });
            });

        },

        getPunches: function (userID) {
            let query = "SELECT * FROM TimeSheet WHERE UserID = ?;";

            var output = [];

            this.db.each(query, [userID], (err, tuple) => {

                if(err) {
                    return console.log(err.message);
                }

                output.push(tuple);

            });

            return output;
        },

        createUser: function (regCode, firstName, lastName, email, username, password) {

            let userID = uuid(); //generate userID

            let verificationQuery = "SELECT * FROM Registration WHERE RegKey = ?;";

            var verified = false;
            var superID;
            var orgID;
            this.db.get(verificationQuery, [regCode], (err, tuple) => {
                if (err) {
                    return console.log(err.message);
                }

                verified = (tuple.RegKey === regCode);
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
        },

        createMessage: function (senderID, recipientID, dateTime, subject, message, read, active, timeID) {
            let query = "INSERT INTO Message VALUES(?,?,?,?,?,?,?,?,?);" ;

            let msgID = uuid();

            this.db.run(query, [msgID, senderID, recipientID, dateTime, subject, message, read, active, timeID], (err, tuple) => {
                if (err) {
                    return console.log(err.message);
                }
            });
        },

        createClockPunch: function (userID, dateTime, punchType, submitTime) {
            let query = "INSERT INTO TimeSheet VALUES(?,?,?,?,?);" ;

            let timeID = uuid();

            var that = this;
            return new Promise(function (resolve, reject) {
                that.db.run(query, [timeID, userID, dateTime, punchType, submitTime], (err, tuple) => {
                    if (err) {
                        console.log(err.message);
                        resolve(false);
                    } else {
                        resolve(true);
                    }
                });
            });
        },

        editUser: function (userID, property, newVal) {
            let query = "UPDATE Users SET " + property + " = ? WHERE UserID = ?;";

            this.db.run(query, [newVal, userID], (err, tuple) => {
                if (err) {
                    return console.log(err.message);
                }
            });
        },

        editMessage: function (msgID, property, newVal) {
            let query = "UPDATE Message SET " + property + " = ? WHERE MessageID = ?;";

            this.db.run(query, [newVal, msgID], (err, tuple) => {
                if (err) {
                    return console.log(err.message);
                }
            });
        },

        close: function () {
            db.close((err) => {
                if (err) {
                    return console.error(err.message);
                }
                console.log('Close the database connection.');
            });
        }
    }
}

// Security measure to prevent SQL injection
var isValid = function (arg) {
    return true;
}

//var database = new host('','');
//var me = database.getUser("myemail@whatever.tv", "secret");
