// Dependencies
const express = require('express');

const session = require('express-session');
const fileStore = require('session-file-store')(session);
const uuid = require('uuid/v4');

const http = require('http');

const nconf = require('nconf');
const fs = require('fs');

const host = require('./host');
const user = require('./user');

// Data constants
const DEFAULT_SESSION_TIMEOUT = 5 * 60 * 1000; // Timeout in milliseconds (5 minutes)
const DEFAULT_PORT = 80;
const DEFAULT_DB_PATH = './database/hr.db';

var SITE_PATH = 'site/test';

var SESSION_TIMEOUT = DEFAULT_SESSION_TIMEOUT;

var db = new host({}, DEFAULT_DB_PATH);

var app = express();
var expressWs = require('express-ws')(app);

// Session data configuration
app.use(session({

    genid: (req) => {
        var id = uuid();

        console.log("[" + req.sessionID + "] - Assuming a new ID --> [" + id + "].");

        return id;
    },

    resave: false,

    store: new fileStore(),

    secret: uuid(),

    //TODO: perhaps change for security purposes
    saveUninitialized: true,

    cookie: {
        maxAge: SESSION_TIMEOUT
    }
}));

// TODO: App routing

app.use('/', express.static(SITE_PATH));

app.get('/logout', function(req, res) {
    req.session = null;

    console.log("[" + req.sessionID + "] - Logged out.");

    res.redirect('/');
});

app.ws('/login', function (ws, req) {

    ws.on('message', function (msg) {

        var data = JSON.parse(msg);

        if (data.event !== 'authentication') {
            console.log("[" + req.sessionID + "] - Unexpected message recieved: " + data.event);
            return;
        }

        var result = false;
        db.getUser(data.email, data.password).then(function (user) {

            if (user) {
                req.session.user = user;
                console.log("[" + req.sessionID + "] - Authenticated as user " + req.session.user.UserID);
                result = true;

                req.session.save(function (err) {
                    if (err) {
                        console.log(err.message);
                    }
                });

            } else {
                console.log("[" + req.sessionID + "] - Login attempt failed. Credentials: " + msg);
                result = false;
            }

            ws.send(JSON.stringify({
                event: 'authenticated',
                success: result
            }));

        }).catch(function (err) {
            console.log(err);
        });
    });
});

app.ws('/punch', function (ws, req) {
    ws.on('message', function (msg) {
        var data = JSON.parse(msg);

        if (data.event !== 'punch') {
            console.log("[" + req.sessionID + "] - Unexpected message recieved: " + data.event);
            return;
        }

        if (req.session.user == null) {
            console.log("[" + req.sessionID + "] - Attempted to clock in without credentials.");
            ws.send(JSON.stringify({event: 'unauthorized'}));
            return;
        }

        if (!(req.session.user.UserID)) {
            console.log("[" + req.sessionID + "] - Attempted to clock in without credentials.");
            ws.send(JSON.stringify({event: 'unauthorized'}));
            return;
        }

        db.createClockPunch(req.session.user.UserID, data.time, data.type, new Date().toISOString().slice(0, 19).replace('T', ' ')).then(function(success) {

            ws.send(JSON.stringify({
                event: 'punched',
                success: success
            }));

            console.log("[" + req.sessionID + "] - punched" + data.type + "at " + data.time + ".");
        }).catch(function (err) {
            console.log(err.message);
            ws.send(JSON.stringify({event: 'error'}));
        });


    });
});

app.ws('/signup', function(ws, req) {
    ws.on('message', function(msg) {
        var data = JSON.parse(msg);

        if (data.event !== 'signup') {
            console.log("[" + req.sessionID + "] - Unexpected message recieved: " + data.event);
            return;
        }

        db.createUser(data.regCode, data.firstName, data.lastName, data.email, data.username, data.password)
            .then(function (success) {
                ws.send(JSON.stringify({
                    event: 'signed',
                    success: success
                }));

                if (success) {
                    console.log("[" + req.session.sessionID + "] - created a new user: " + data.firstName + " " + data.lastName);

                } else {
                    console.log("[" + req.session.sessionID + "] - Account creation failed... data: " + JSON.stringify(data));
                }
            });

    });
})

// Run server
app.listen(DEFAULT_PORT);
