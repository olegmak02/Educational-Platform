function onClick() {
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;
    fetch(`/api/v1/auth/authenticate`, {
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
        .then((json) => {
            Cookies.set("token", json.token);
            Cookies.set("userId", json.userId);
            Cookies.set("username", json.username);
        });
    }

    document.getElementById("login-submit").addEventListener("click", onClick);

    function toCourses() {
    fetch("/course", {
        method: 'GET',
        redirect: 'follow',
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }})
        .then((response) => {
            return response.text();
        })
        .then((html) => {
            document.write(html);
            document.close;
        });
    }

    document.getElementById("chat_button").addEventListener("click", redir);;


    function redir() {
    fetch("/chat", {
        method: 'GET',
        redirect: 'follow',
        cache: "no-store",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Access-Control-Allow-Origin': '*'
        }})
        .then(function(response) {
            console.log(response);
            if (response.ok) {
                window.location.href = response.url;
            }
        });
    }

    document.getElementById("course_button").addEventListener("click", toCourses);;
