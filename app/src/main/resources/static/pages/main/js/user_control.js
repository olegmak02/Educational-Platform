async function toStudents() {
    changeMenuElementColor(7);

    let courses = await getUsersCourses();

        document.getElementById("panel").innerHTML = "<h2>Курси</h2>";

        displayCourses(courses);
        for (let i = 0; i < courses.length; i++) {
            document.getElementById("table-content").children[i]
                    .addEventListener("click", () => toStudentsByCourse(courses[i].id));
        }
}

async function toStudentsByCourse(courseId) {
    let students = await fetch(`/api/v1/course/students/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());

    document.getElementById("content_container").innerHTML = `<button onclick=${"addStudentsToCourse(" + courseId + ")"}>Додати користувачів</button><table id="table-content"></table>`;
    let number = 1;
    for (let student of students) {
        let row = document.createElement("tr");
        row.className = "course";
        row.innerHTML = `<td>${number++}</td><td>${student.lastname + " " + student.firstname + " " + student.groupName}</td><td><button onclick=${"removeStudentFromCourse(" + student.id + "," + courseId + ")"}>Видалити користувача з предмету</button></td>`;
        document.getElementById("table-content").appendChild(row);
    }
}

async function removeStudentFromCourse(studentId, courseId) {
    await fetch(`/api/v1/course/delete-student/course/${courseId}/student/${studentId}`, {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    });
}

async function addStudentsToCourse(courseId) {
    let students = await fetch(`/api/v1/course/absent-students/${courseId}`, {
            method: "GET",
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
            }
        }).then(res => res.json());

    document.getElementById("table-content").innerHTML = "";
    let number = 1;
    for (let student of students) {
        let row = document.createElement("tr");
        row.className = "course";
        row.innerHTML = `<td>${number++}</td><td>${student.lastname + " " + student.firstname + " " + student.groupName}</td><td><button onclick=${"addStudent(" + student.id + "," + courseId + ")"}>Додати користувача до предмету</button></td>`;
        document.getElementById("table-content").appendChild(row);
    }
}

async function addStudent(studentId, courseId) {
    await fetch("/api/v1/course/register-student", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({studentId, courseId})
    });
}

function toAddingUser() {
    document.getElementById("content_container").innerHTML = `<p style="font-size: 20px;font-weight: bold;">Логін:</p><input id="username" type="text" / >
            <p style="font-size: 20px;font-weight: bold;">Пароль:</p> <input id="password" type="text" />
            <p style="font-size: 20px;font-weight: bold;">Ім'я:</p> <input id="firstname" type="text" />
            <p style="font-size: 20px;font-weight: bold;">Прізвище:</p> <input  id="lastname" type="text" />
            <p style="font-size: 20px;font-weight: bold;">Група:</p> <input id="group" type="text" />
            <div id="role"><input type="radio" value="STUDENT" name="user_role" checked /><label>Студент</label>
            <input type="radio" value="TEACHER" name="user_role" /><label>Викладач</label></div>`;

        document.getElementById("content_container").innerHTML += `<button onclick="addUser()">
          Додати користувача
        </button>`;
}

async function addUser() {
    let role = document.querySelector('input[name="user_role"]:checked').value;
    let firstname = document.getElementById("firstname").value;
    let lastname = document.getElementById("lastname").value;
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;
    let groupName = document.getElementById("group").value;
    
    if (role == "TEACHER") {
        groupName = null;
    }

    await fetch("/api/v1/auth/register", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({firstname, lastname, username, password, groupName, role})
    });

    toUsers();
}

async function toUsers() {
    changeMenuElementColor(2);

    let users = await fetch("/api/v1/user/all").then(res => res.json());

    document.getElementById("panel").innerHTML = "<h2>Користувачі</h2>";
    document.getElementById("content_container").innerHTML = `<button onclick="toAddingUser()">Додати користувача</button>`;
    document.getElementById("content_container").innerHTML += `<table id="table-content"></table>`;

    document.getElementById("table-content").innerHTML = "";
    let number = 1;
    for (let user of users) {
        let row = document.createElement("tr");
        row.className = "course";
        row.innerHTML = `<td>${number++}</td><td>${user.lastname + " " + user.firstname}</td><td>${user.username}</td><td>${user.role}</td>`;
        document.getElementById("table-content").appendChild(row);
    }
    for (let i = 0; i < users.length; i++) {
        document.getElementById("table-content").children[i]
                .addEventListener("click", () => toUser(users[i].id));
    }
}

async function toUser(userId) {
    let user = await fetch(`/api/v1/user/${userId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());

    document.getElementById("content_container").innerHTML = `<p style="font-size: 20px;font-weight: bold;">Логін:</p><input id="username" type="text" value=${'"' + user.username + '"'}/ >
        <p style="font-size: 20px;font-weight: bold;">Ім'я:</p> <input id="firstname" type="text" value=${'"' + user.firstname + '"'}/ >
        <p style="font-size: 20px;font-weight: bold;">Прізвище:</p> <input  id="lastname" type="text" value=${'"' + user.lastname + '"'}/ >`;

    if (user.role == "STUDENT")
        document.getElementById("content_container").innerHTML += `<p style="font-size: 20px;font-weight: bold;">Група:</p> <input id="group" type="text" value=${'"' + user.groupName + '"'}/ >`;

    document.getElementById("content_container").innerHTML += `<button onclick=${"toChangeInfo("+userId+")"}>
      Змінити пароль
    </button>`;
}

function toChangeInfo(userId) {
    let groupName = null;
    if (document.getElementById("group"))
        groupName = document.getElementById("group").value;
    fetch("/api/v1/user/update", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
            },
        body: JSON.stringify({id: userId, username: document.getElementById("username").value, firstname: document.getElementById("firstname").value, lastname: document.getElementById("lastname").value, groupName})
    });
}
