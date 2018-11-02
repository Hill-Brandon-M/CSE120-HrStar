// Dependencies
const express = require('express');

const session = require('express-session');
const fileStore = require('session-file-store')(session);
const uuid = require('uuid/v4');

const http = require('http');

const socket = require('socket.io');
const auth = require('socketio-auth');

const nconf = require('nconf');
const fs = require('fs');

// Data constants
const SESSION_TIMEOUT = 5 * 60 * 1000; // Timeout in milliseconds (5 minutes)

// Generic configuration object
var config = function(path) {
    // TODO: create generic configuration

}

var app = express();

// Session data configuration
app.use(session({

    genid: (req) => {
        var id = uuid();

        console.log("[" + req.sessionID + "] - Assuming a new ID.");
        console.log("[" + req.sessionID + "] --> [" + id + "]");

        return id;
    },

    store: new fileStore(),

    secret: uuid(),

    saveUninitialized: false,

    cookie: {
        maxAge: SESSION_TIMEOUT
    }
}));

var server = http.createServer(app);
var io = socket(server);
