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

const host = require('./host');
const user = require('./user');

// Data constants
const DEFAULT_SESSION_TIMEOUT = 5 * 60 * 1000; // Timeout in milliseconds (5 minutes)
const DEFAULT_PORT = 8080;
const DEFAULT_DB_PATH = './database/hr.db';

var SESSION_TIMEOUT = DEFAULT_SESSION_TIMEOUT;

var server = new host({}, DEFAULT_DB_PATH);

// Generic configuration object
const config = function(path) {
    // TODO: create generic configuration

};

var app = express();

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

    saveUninitialized: false,

    cookie: {
        maxAge: SESSION_TIMEOUT
    }
}));

var server = http.createServer(app);
var io = socket(server);

// Authentication handler
auth(io, {
    authenticate: function (socket, data, {}) {
        var email = data.email;
        var password = data.password;

        return (server.getUserID(email, password) !== -1);
    },

    postAuthenticate: function (socket, data) {
        socket.request.session.user = server.getUser(data.email, data.password);
    },

    disconnect: function (socket, data) {
        console.log("[" + socket.request.sessionID + "] disconnected...");
    }

});

// Run server
server.listen(DEFAULT_PORT);
