async function onClick() {
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;

    await fetch("/api/v1/auth/authenticate", {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username, password
        })
    })
    .then((res) => {
        return res.json();
    })
    .then((user) => {
        Cookies.set("token", user.token);
        Cookies.set("userId", user.userId);
        Cookies.set("username", user.username);
        Cookies.set("role", user.role);

        window.location.href = "/main";
    });
}

document.getElementById("login-submit").addEventListener("click", onClick);
