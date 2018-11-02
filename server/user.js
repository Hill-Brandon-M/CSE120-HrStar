exports = function (u_id, firstname, lastname, username, password, email, o_id, s_id) {
    this.u_id = u_id;

    this.firstname = firstname;
    this.lastname = lastname;

    this.username = username;
    this.password = password;

    this.email = email;

    this.org_id = o_id;
    this.super_id = s_id;
};
