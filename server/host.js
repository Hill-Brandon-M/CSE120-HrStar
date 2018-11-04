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
    this.db = new sqlite3.Database(dbPath);

    this.getUserID = function (email, password) {
        let query = "SELECT u_id FROM users WHERE email = ? AND password = ?;";

        this.db.get(query, [email, password], (err, tuple) => {
            if (err) {
                console.error(err.message);
                return -1;
            }

            return tuple.u_id;
        });
    }

    this.getUserFromID = function (userID) {
        let query = "SELECT * FROM users WHERE u_id = ?;";

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
        let query = "SELECT * FROM punches WHERE u_id = ?;";

        var output = [];

        this.db.each(query, [userID], (err, tuple) => {

            if(err) {
                throw err;
            }

            output.push(tuple);

        });

        return output;
    }
}

// Security measure to prevent SQL injection
var isValid = function (arg) {
    return true;
}

//var database = new host('','');
//var me = database.getUser("myemail@whatever.tv", "secret");
