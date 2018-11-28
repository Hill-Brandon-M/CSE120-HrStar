// Data cosntants
const site = window.location;

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
            window.location.href = site.protocol + "://" + site.host + "punch/" // Redirect to clock page
        } else {
            console.log('Login failed...')
        }
    };
};

var punch = function (type, time) {
    // TODO: Punch routine
    socket.send(JSON.stringify({
        event: 'punch',
        type: type,
        time: time,
    }));
};

var punchIn = function (time = new Date()) {
    this.punch('IN', time);
};

var punchOut = function (time = new Date()) {
    this.punch('OUT', time);
};
