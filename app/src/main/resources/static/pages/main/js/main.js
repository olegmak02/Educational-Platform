function getFinishedTasks(handler, courseId) {
    fetch(`/api/v1/task/submitted/course/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json())
    .then(res => {
        displayCourses(res);
        for (let i = 0; i < res.length; i++) {
            document.getElementById("table-content").children[i].addEventListener("click", () => handler(res[i].id));
        }
    });
}

async function toCreationTask(courseId) {
    let userRole = await fetch("/api/v1/user/my-info", {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`
        }
    })
    .then(res => res.json())
    .then(info => info.role);

    if (userRole != "TEACHER") {
        window.location = "/main";
        return;
    }

    document.getElementById("content_container").innerHTML = `<a>Створення завдання</a>
          <label>Введіть назву завдання</label>
          <input type="text" id="answer_title"/>
          <label>Введіть термін закінчення</label>
          <input type="date" id="answer_end"/>
          <label>Введіть завдання</label>
          <input type="text" id="answer_task_text"/>
          <label>Прикріпити файл</label>
          <input type="file" id="files" multiple />
          <button style="width:90%" onclick=${"addNewTask("+courseId+")"}>Створити завдання</button>`;
}


async function addNewTask(courseId) {
    const task = {courseId};
    task.title = document.getElementById("answer_title").value;
    task.exposeTime = document.getElementById("answer_end").value;
    task.creationTime = new Date();
    task.taskText = document.getElementById("answer_task_text").value;

    const files = document.getElementById("files").files;

    const newTask = await fetch("/api/v1/task/create", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(task)
    }).then(res => res.json());

    for (let file of files) {
        let formData = new FormData();
        formData.append("file", file);
        formData.append("postId", newTask.id);
        await fetch("/api/v1/file/upload", {
            method: "POST",
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`
            },
            body: formData
        });
    }
}

async function getUnfinishedTasks(handler, courseId) {
    await fetch(`/api/v1/task/incomplete/course/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        }
    })
    .then(res => res.json())
    .then(res => {
        displayCourses(res);
        for (let i = 0; i < res.length; i++) {
            document.getElementById("table-content").children[i].addEventListener("click", () => handler(res[i].id));
        }
    });

    let course = await fetch(`/api/v1/course/${courseId}`).then(res => res.json());

    if (new Date() < new Date(course.endDate) && Cookies.get("role") == "TEACHER") {
        document.getElementById("table-content").innerHTML += `<button onclick=${"toCreationTask("+courseId+")"}>Створити нове завдання</button>`;
    }
}

async function toChats() {
    changeMenuElementColor(5);

    let courses = await getUsersCourses();
    displayCourses(courses);

    for (let i = 0; i < courses.length; i++) {
        document.getElementById("table-content").children[i]
                .addEventListener("click", () => goToChat(courses[i].id));
    }
}

function goToChat(courseId) {
    fetch("/chat", {
            method: 'GET',
            redirect: 'follow',
            cache: "no-store",
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
                'Access-Control-Allow-Origin': '*'
        }})
        .then(async function(response) {
            if (response.ok) {
                Cookies.set("chatId", courseId);
                let course = await fetch(`/api/v1/course/${courseId}`, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${Cookies.get("token")}`,
                    }}).then(res => res.json());
                Cookies.set("courseTitle", course.title);
                window.location.href = response.url;
            }
        });
}

function toChangePasswordPage() {
    document.getElementById("content_container").innerHTML = `<a>Зміна паролю</a>
    <form action="javascript:void(0);" style="display:flex; flex-direction: column; align-items:center;">
      <label>Введіть старий пароль</label>
        <input type="text" id="old_pswd">
      <label>Введіть новий пароль</label>
        <input type="text" id="new_pswd">
      <label>Повторіть новий пароль</label>
        <input type="text" id="rep_pswd">
      <button style="width:90%" onclick="toChangePassword()">Змінити пароль</button>
    </form>
    <a id="error_field" style="color:red"></a>`;
}

function toChangePassword() {
    let oldPassword = document.getElementById("old_pswd").value;
    let newPassword = document.getElementById("new_pswd").value;
    let repPassword = document.getElementById("rep_pswd").value;

    let errorField = document.getElementById("error_field");

    if (newPassword != repPassword) {
        errorField.text = "Нові паролі не збігаються, перевірте правильність вводу";
        return;
    }

    fetch("/api/v1/auth/change-password", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({oldPassword, newPassword})
    })
    .then(async res => {
        if (res.status != 200) {
            let data = await res.json();
            errorField.text = data.message;
            return;
        }
        toProfile();
    });
}

function toProfile() {
    changeMenuElementColor(0);
    fetch("/api/v1/user/my-info", {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${Cookies.get("token")}`
    }})
    .then(res => res.json())
    .then(res => {
        document.getElementById("content_container").innerHTML = `<p style="font-size: 20px;font-weight: bold;">Логін: ${res.username}</p>
        <p style="font-size: 20px;font-weight: bold;">Прізвище та ім'я: ${res.firstname + " " + res.lastname}</p>
      <p style="font-size: 20px;font-weight: bold;">Група: ${res.groupName != null ? res.groupName : '-'}</p>
      <button onclick="toChangePasswordPage()">
        Змінити пароль
      </button>`;
    });
}

function findTask(handler, courseId) {
    let findTitle = document.getElementById("find_title_field").value;
    fetch(`/api/v1/task/course/${courseId}/title?title=${findTitle}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json())
    .then(res => {
        displayCourses(res);
        for (let i = 0; i < res.length; i++) {
            document.getElementById("table-content").children[i].addEventListener("click", () => handler(res[i].id));
        }
    });
}

async function toTasksByCourse(courseId) {
    await getUnfinishedTasks(showTask, courseId);

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick=${"getFinishedTasks(showTask,"+ courseId +")"}>Показати завершені завдання</button>
                <button class="panel_element" onclick=${"getUnfinishedTasks(showTask,"+ courseId +")"}>Показати незавершені завдання</button>
                <div class="panel_element" id="find_course">
                  <label>Знайти предмети за назвою</label>
                  <input type="text" id="find_title_field"/>
                  <button id="find_course_button" onclick=${"findTask(showTask,"+ courseId +")"}>Знайти</button>
                </div>`;
}

async function addTaskAnswer(taskId) {
    const answer = await fetch("/api/v1/answer/create", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({taskId})
    })
    .then(res => res.json());

    const files = document.getElementById("files").files;

    for (let file of files) {
            let formData = new FormData();
            formData.append("file", file);
            formData.append("postId", answer.id);
            await fetch("/api/v1/file/upload", {
                method: "POST",
                headers: {
                    'Authorization': `Bearer ${Cookies.get("token")}`
                },
                body: formData
            });
        }
}

async function showTask(taskId) {
    let task = await fetch(`/api/v1/task/${taskId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json());

    document.getElementById("panel").innerHTML = "";

    let answer = await fetch(`/api/v1/answer/task/${taskId}/my-answer`)
                    .then(res => res.json())
                    .catch(e => null);

    document.getElementById("content_container").innerHTML = `<p style="font-size: 20px;font-weight: bold;">Назва завдання: ${task.title}</p>
          <p style="font-size: 20px;font-weight: bold;">Завдання: ${task.taskText}</p>
          <p style="font-size: 20px;font-weight: bold;">Термін здачі: ${task.exposeTime}</p>
          <p style="font-size: 20px;font-weight: bold;">Статус: ${calculateStatus(task, answer)}</p>
          <p style="font-size: 20px;font-weight: bold;" id="files_area">Прикріплені файли</p>`;

    let fileIds = await fetch(`/api/v1/file/post/${taskId}`).then(res => res.json());

    for (let fileId of fileIds) {
        let attachment = await fetch(`/api/v1/file/${fileId}`).then(res => res.json());
        document.getElementById("content_container").innerHTML += `<a href=${"/api/v1/file/download/" + fileId} target="_blank">${attachment.title}</a>`;
    }

    if (Cookies.get("role") == "STUDENT") {
        document.getElementById("content_container").innerHTML += `<button onclick=${"toCreationAnswer(" + taskId + ")"}>Створити відповідь</button>`;
    }
}

function toCreationAnswer(taskId) {
    document.getElementById("content_container").innerHTML = `<a>Створення завдання</a>
              <label>Прикріпити файл</label>
              <input type="file" id="files" multiple />
              <button style="width:90%" onclick=${"addTaskAnswer("+taskId+")"}>Створити завдання</button>`;
}

function calculateStatus(task, answer) {
    if (answer != null) {
        return "Здано;"
    }

    return new Date(task.exposeTime) < new Date() ? "Термін здачі вичерпано" : "Не здано";
}

async function getAttachment(id) {
    return await fetch(`/api/v1/file/download/${id}`);
}

async function toTasks() {
    changeMenuElementColor(4);

    let courses = await getUsersCourses();

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick="getFinishedCourses(toTasksByCourse)">Показати завершені предмети</button>
            <button class="panel_element" onclick="getUnfinishedCourses(toTasksByCourse)">Показати незавершені предмети</button>
            <div class="panel_element" id="find_course">
              <label>Знайти предмети за назвою</label>
              <input type="text" id="find_title_field"/>
              <button id="find_course_button" onclick="findCourse(toTasksByCourse)">Знайти</button>
            </div>`;

    displayCourses(courses);
    for (let i = 0; i < courses.length; i++) {
        document.getElementById("table-content").children[i]
                .addEventListener("click", () => toTasksByCourse(courses[i].id));
    }
}

function getStudentsByCourse(courseId) {
    fetch(`/api/v1/course/students/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json())
    .then(res => {
        document.getElementById("panel").innerHTML = "";
        document.getElementById("content_container").innerHTML = `<table id="table-content"></table>`;
        let number = 1;
        for (let student of res) {
            let row = document.createElement("tr");
            row.addEventListener("click", () => getAssessmentsByStudentAndCourse(student.id, courseId));
            row.className = "course";
            row.innerHTML = `<td>${number++}</td><td>${student.lastname + " " + student.firstname}</td>`;

            document.getElementById("table-content").appendChild(row);
        }
    });
}

async function toCheckingTask(taskId) {
    const answers = await fetch(`/api/v1/answer/task/${taskId}`, {
       method: "GET",
       headers: {
           'Authorization': `Bearer ${Cookies.get("token")}`,
       }
    }).then(res => res.json());

    document.getElementById("content_container").innerHTML = `<table id="table-content"></table>`;
    let number = 1;

    for (let i = 0; i < answers.length; i++) {
        let student = await fetch(`/api/v1/user/${answers[i].studentId}`).then(res => res.json());
        let row = document.createElement("tr");
        row.className = "course";
        row.innerHTML = `<td>${number++}</td><td>${student.lastname + " " + student.firstname}</td>`;
        document.getElementById("table-content").appendChild(row);
        document.getElementById("table-content").children[i].addEventListener("click", () => getAnswer(answers[i].taskId,student.id));
    }
}

async function getAnswer(taskId, studentId) {
    let answer = await fetch(`/api/v1/answer/task/${taskId}/student/${studentId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json());

    let student = await fetch(`/api/v1/user/${answer.studentId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json());

    document.getElementById("content_container").innerHTML = `<p style="font-size: 20px;font-weight: bold;">Студент: ${student.lastname + " " + student.firstname + " " + student.groupName}</p>
          <p style="font-size: 20px;font-weight: bold;">Відправлено: ${answer.postDate}</p>`;

    for (let fileId of answer.attachments) {
        let attachment = await fetch(`/api/v1/file/${fileId}`, {
            method: "GET",
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
            }
        }).then(res => res.json());

        document.getElementById("content_container").innerHTML += `<a href=${"/api/v1/file/download/" + fileId} target="_blank">${attachment.title}</a>`;
    }

    document.getElementById("content_container").innerHTML += `<label>Виставити оцінку</label>
                        <input type="text" id="assessment_field"/>
                        <button onclick=${"addAssessment(" + studentId + "," + taskId + ")"}>Поставити оцінку</button>`;
}

function changeMenuElementColor(index) {
    for (let element of document.querySelectorAll("#menu button")) {
        element.style.color = "black";
    }

    document.querySelectorAll("#menu button")[index].style.color = "red";
}

async function toCheckingTasksByCourse(courseId) {
    const tasks = await fetch(`/api/v1/task/course/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick=${"getFinishedTasks(toCheckingTask,"+ courseId +")"}>Показати завершені завдання</button>
                <button class="panel_element" onclick=${"getUnfinishedTasks(toCheckingTask,"+ courseId +")"}>Показати незавершені завдання</button>
                <div class="panel_element" id="find_course">
                  <label>Знайти предмети за назвою</label>
                  <input type="text" id="find_title_field"/>
                  <button id="find_course_button" onclick=${"findTask(toCheckingTask,"+ courseId +")"}>Знайти</button>
                </div>`;

    displayCourses(tasks);

    for (let i = 0; i < tasks.length; i++) {
            document.getElementById("table-content").children[i]
                    .addEventListener("click", () => toCheckingTask(tasks[i].id));
        }
}

async function toChecking() {
    changeMenuElementColor(8);

    let courses = await getUsersCourses();

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick="getFinishedCourses(toTasksByCourse)">Показати завершені предмети</button>
            <button class="panel_element" onclick="getUnfinishedCourses(toTasksByCourse)">Показати незавершені предмети</button>
            <div class="panel_element" id="find_course">
              <label>Знайти предмети за назвою</label>
              <input type="text" id="find_title_field"/>
              <button id="find_course_button" onclick="findCourse(toTasksByCourse)">Знайти</button>
            </div>`;

    displayCourses(courses);

    for (let i = 0; i < courses.length; i++) {
        document.getElementById("table-content").children[i]
                .addEventListener("click", () => toCheckingTasksByCourse(courses[i].id));
    }
}

(async function () {
    let user = await fetch("/api/v1/user/my-info", {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`
    }})
    .then(res => res.json());

    Cookies.set("role", user.role);
    if (Cookies.get("role") == "STUDENT") {
        document.getElementById("assessment_tab").addEventListener("click", toAllAssessments);
    } else if (Cookies.get("role") == "TEACHER") {
        let menu = document.getElementById("menu");
        menu.innerHTML += `<div class="line"></div>
            <button class="menu_element" id="student_tab" onclick="toStudents()">Студенти</button>
            <div class="line"></div>
            <button class="menu_element" id="check_tab" onclick="toChecking()">Перевірка завдань</button>`;
        document.getElementById("assessment_tab").addEventListener("click", toTeacherAssessment);
    } else if (Cookies.get("role") == "ADMIN") {
        document.getElementById("menu").innerHTML = `<button class="menu_element" id="profile_tab" onclick="toProfile()">Мій профіль</button>
           <div class="line"></div>
           <button class="menu_element" id="course_tab" onclick="toAllCourses()">Предмети</button>
           <div class="line"></div>
           <button class="menu_element" id="user_tab" onclick="toUsers()">Користувачі</button>`;

    }
})();