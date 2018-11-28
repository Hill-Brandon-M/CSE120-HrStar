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

// Generic configuration object
const config = function(path) {
    // TODO: create generic configuration

};

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

app.ws('/login', function (ws, req) {

    ws.on('message', function (msg) {

        var data = JSON.parse(msg);

        if (data.event !== 'authentication') {
            console.log("[" + req.sessionID + "] - Unexpected message recieved: " + data.event);
            return;
        }

        req.session.user = db.getUser(data.email, data.password);

        var result = false;

        if (req.session.user) {
            console.log("[" + req.sessionID + "] - Authenticated as user " + req.session.user.u_id);
            result = true;
        } else {
            console.log("[" + req.sessionID + "] - Login attempt failed. Credentials: " + msg);
            result = false;
        }

        ws.send(JSON.stringify({
            event: 'authenticated',
            success: result
        }));
    });
});

// Run server
app.listen(DEFAULT_PORT);
