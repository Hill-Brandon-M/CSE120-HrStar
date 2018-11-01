const sqlite3 = require('sqlite3').verbose();
const uuid = require('uuid/v4');
const time = require('time');
const fs = require('fs');

const SESSION_TIMEOUT = 300; // Timeout in seconds (5 minutes)

var user = function (u_id, firstname, lastname, username, password, email, o_id, s_id) {
    this.u_id = u_id;

    this.firstname = firstname;
    this.lastname = lastname;

    this.username = username;
    this.password = password;

    this.email = email;

    this.org_id = o_id;
    this.super_id = s_id;
};

/*
 * host
 *
 * Database interface abstraction.
 */
var host = function (ip, dbPath) {

    this.ip = ip;
    this.db = new sqlite3.Database(dbPath);

    var getUserID = function (email, password) {
        let query = "SELECT u_id FROM users WHERE email = ? AND password = ?;";

        this.db.get(query, [email, password], (err, tuple) => {
            if (err) {
                return console.error(err.message);
            }

            return tuple.u_id;
        });
    };

    var getUserFromID = function (userID) {
        let query = "SELECT * FROM users WHERE u_id = ?;";

        this.db.get(query, [userID], (err, tuple) => {

            if (err) {
                return console.error(err.message);
            }

            return new user(tuple.u_id, tuple.firstname, tuple.lastname, tuple.username, tuple.password, tuple.email, tuple.org_id, tuple.super_id);
        });
    };

    this.getUser = function (email, password) {
        var userID = getUserID(email, password);
        return getUserFromID(userID);
    };

    this.getPunches = function (userID) {
        let query = "SELECT * FROM punches WHERE u_id = ?;";

        var output = [];

        this.db.each(query, [userID], (err, tuple) => {

            if(err) {
                throw err;
            }

            output.push(tuple);

        });

        return output;
    };
};
