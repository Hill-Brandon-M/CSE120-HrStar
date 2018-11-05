//Dependencies
const io = require('socket.io-client');

// Data cosntants
var host = 'https://localhost'

module.exports = {

    // IO instance
    this.socket = io(window.location, {
        autoConnect: true;
    });

    // User authenticator
    this.login = function (email, password) {

        // Send login data via socket.io-auth
        socket.emit('authentication', {
            email: email,
            password: password
        });

        socket.on('authenticated', function () {
            // Placeholder...
        });
    };

    this.punch = function (type, time) {
        // TODO: Punch routine
        this.socket.emit('punch', {
            type: type,
            time: time,
        });
    };

    this.punchIn = function (time = new Date()) {
        this.punch('IN', time);
    };

    this.punchOut = function (time = new Date()) {
        this.punch('OUT', time);
    };

};
