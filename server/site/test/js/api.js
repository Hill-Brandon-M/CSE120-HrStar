// Data cosntants
const site = window.location;

// Generate WebSocket URI
var protocol = "";
if (site.protocol === "https") {
    protocol = "wss";
} else {
    protocol = "ws";
}

const uri = protocol + "://" + site.host + site.pathname;

// IO instance
var socket = new WebSocket(uri);

socket.onopen = function () {
    console.log("Socket connection established...");
};

socket.onclose = function() {
    console.log("Socket connection closed...");
};

// User authenticator
var login = function (email, password) {

    console.log("Logging in...");

    // Send login data via websocket
    socket.send(JSON.stringify({
        event: 'authentication',
        email: email,
        password: password
    }));

    var old_handle = socket.onmessage;

    socket.onmessage = function (msg) {

        var data = JSON.parse(msg.data);

        if (data.event !== 'authenticated') {
            socket.onmessage = old_handle;

        } else if (data.success) {
            console.log('Login successful!');
            window.location.href = "../punch"; // Redirect to clock page
        } else {
            console.log('Login failed...')
        }
    };
};

var signup = function (regCode, firstName, lastName, email, username, password) {
    console.log("Creating account...");

    socket.send(JSON.stringify({
        event: 'signup',
        regCode: regCode,
        firstName: firstName,
        lastName: lastName,
        email: email,
        username: username,
        password: password
    }));

    var old_handle = socket.onmessage;

    socket.onmessage = function (msg) {
        var data = JSON.parse(msg.data);

        if (data.event !== 'signed') {
            socket.onmessage = old_handle;

        } else if (data.success) {
            console.log('Signup sucessful!');
            window.location.href = "../login";
        } else {
            console.log('Signup failed...');
        }
    }
};

var punch = function (type, time) {
    // TODO: Punch routine
    socket.send(JSON.stringify({
        event: 'punch',
        type: type,
        time: time,
    }));

    var old_handle = socket.onmessage;

    socket.onmessage = function (msg) {
        var data = JSON.parse(msg.data);

        if (data.event === 'punched') {
            if (data.success) {
                console.log('SUCCESS!');
            } else {
                console.log('FAILED!');
            }

        } else if (data.event === 'unauthorized') {
            console.log('Credentials are invalid, redirecting to login...');
            window.location.href = "../login";

        } else if (data.event === 'error') {
            console.log('Server error... Please contact site administrator for help...');
        } else {
            socket.onmessage = old_handle;
        }
    };
};

var punchIn = function (time = new Date()) {
    this.punch('IN', time);
};

var punchOut = function (time = new Date()) {
    this.punch('OUT', time);
};
