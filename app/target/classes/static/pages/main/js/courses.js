function findCourse(handler) {
    let findCourseTitle = document.getElementById("find_title_field").value;
    fetch(`/api/v1/course/title?title=${findCourseTitle}`, {
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
}

async function toAllCourses() {
    let courses = await fetch("/api/v1/course/all", {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick="getFinishedCourses(toCourseChange)">Показати завершені предмети</button>
    <button class="panel_element" onclick="getUnfinishedCourses(toCourseChange)">Показати незавершені предмети</button>
    <div class="panel_element" id="find_course">
      <label>Знайти предмети за назвою</label>
      <input type="text" id="find_title_field"/>
      <button id="find_course_button" onclick="findCourse(toCourseChange)">Знайти</button>
      <button onclick="toAddingCourse()">Додати предмет</button>
    </div>`;

    displayCourses(courses);
    for (let i = 0; i < courses.length; i++) {
        document.getElementById("table-content").children[i].addEventListener("click", () => toCourseChange(courses[i].id));
    }
}

async function toAddingCourse() {
    document.getElementById("content_container").innerHTML = `<p style="font-size: 20px;font-weight: bold;">Назва курсу:</p><input id="title" type="text" / >
            <p style="font-size: 20px;font-weight: bold;">Дата початку:</p> <input id="beginDate" type="date" />
            <p style="font-size: 20px;font-weight: bold;">Дата кінця:</p> <input  id="endDate" type="date" />`;

    let teachers = await fetch("/api/v1/user/all-teachers", {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());
    
    let options = "";

    for (let t of teachers) {
        options += `<option value=${t.id}>${t.lastname + " " + t.firstname}</option>`;
    }

    document.getElementById("content_container").innerHTML += `<select id="teachers">${options}</select>`;


    document.getElementById("content_container").innerHTML += `<button onclick="addCourse()">
        Додати предмет
    </button>`;
}

async function addCourse() {
    let title = document.getElementById("title").value;
    let teacherId = document.getElementById("teachers").options[document.getElementById("teachers").selectedIndex].value;
    let beginDate = document.getElementById("beginDate").value;
    let endDate = document.getElementById("endDate").value;

    let course = await fetch("/api/v1/course/create", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({title, teacherId, beginDate, endDate})
    });

    await fetch("/api/v1/material/create", {
        method: "POST",
        headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
                'Content-Type': 'application/json',
        },
        body: JSON.stringify({courseId: course.id})
    })

    toUsers();
}

function toFormatDate(date) {
    let d = new Date(date);
    let month = (d.getMonth() + 1) < 10 ? `${"0" + (d.getMonth() + 1)}` : d.getMonth() + 1 ;
    let day = (d.getDate()) < 10 ? `${"0" + (d.getDate())}` : d.getDate();
    return d.getFullYear() + "-" + month + "-" + day;
}

async function toCourseChange(courseId) {
    let course = await fetch(`/api/v1/course/${courseId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
        }})
        .then(response => response.json());

    let teacher = await fetch(`/api/v1/user/${course.teacherId}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
            }})
        .then(res => res.json());

    document.getElementById("content_container").innerHTML = `<p style="font-size: 20px;font-weight: bold;">Назва:</p><input id="name" type="text" value=${'"' + course.title + '"'} />
            <p style="font-size: 20px;font-weight: bold;">Вчитель:</p>`;


    let teachers = await fetch("/api/v1/user/all-teachers", {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());
    let options = "";

    for (let t of teachers) {
        options += `<option value=${t.id} ${(t.id == teacher.id) ? "selected": ""}>${t.lastname + " " + t.firstname}</option>`;
    }

    document.getElementById("content_container").innerHTML += `<select id="teachers">${options}</select>`;

    document.getElementById("content_container").innerHTML += `<p style="font-size: 20px;font-weight: bold;">Початок:</p> <input id="beginDate" type="date" value=${'"' + toFormatDate(course.beginDate) + '"'} />
            <p style="font-size: 20px;font-weight: bold;">Кінець:</p> <input id="endDate" type="date" value=${'"' + toFormatDate(course.endDate) + '"'} />`;

    document.getElementById("content_container").innerHTML += `<button onclick=${"changeCourse("+courseId+")"}>
      Оновити предмет
    </button>`;
}

function changeCourse(courseId) {
    let teacherId = document.getElementById("teachers").options[document.getElementById("teachers").selectedIndex].value;
    let beginDate = document.getElementById("beginDate").value;
    let endDate = document.getElementById("endDate").value;
    let title = document.getElementById("name").value;

    fetch("/api/v1/course/update", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({id: courseId, teacherId, beginDate, endDate, title})
    })

}


async function getUsersCourses() {
    return await fetch("/api/v1/course/my-courses", {
             method: 'GET',
             headers: {
                 'Authorization': `Bearer ${Cookies.get("token")}`,
                 'Accept': 'application/json',
                 'Content-Type': 'application/json',
         }})
         .then((response) => {
             return response.json();
         });
}


async function toCourses() {
    let courses = await getUsersCourses();

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick="getFinishedCourses(getCourseInfo)">Показати завершені предмети</button>
    <button class="panel_element" onclick="getUnfinishedCourses(getCourseInfo)">Показати незавершені предмети</button>
    <div class="panel_element" id="find_course">
      <label>Знайти предмети за назвою</label>
      <input type="text" id="find_title_field"/>
      <button id="find_course_button" onclick="findCourse(getCourseInfo)">Знайти</button>
    </div>`;

    displayCourses(courses);
    for (let i = 0; i < courses.length; i++) {
        document.getElementById("table-content").children[i]
                .addEventListener("click", () => getCourseInfo(courses[i].id));
    }
}

function getFinishedCourses(handler) {
    fetch("/api/v1/course/my-courses/finished", {
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
}

function getUnfinishedCourses(handler) {
    fetch("/api/v1/course/my-courses/unfinished", {
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
}

function displayCourses(courses) {
    document.getElementById("content_container").innerHTML = `<table id="table-content"></table>`;
    let number = 1;
    for (let course of courses) {
        let row = document.createElement("tr");
        row.className = "course";
        row.innerHTML = `<td>${number++}</td><td>${course.title}</td>`;
        document.getElementById("table-content").appendChild(row);
    }
}

function getCourseInfo(id) {
    fetch(`/api/v1/course/${id}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
    }})
    .then((response) => {
        return response.json();
    })
    .then(async (res) => {
        let container = document.getElementById("content_container");
        container.innerHTML = '';
        let title_info = document.createElement("a");
        title_info.innerHTML = `Предмет: ${res.title}`
        container.appendChild(title_info);
        let teacher = await fetch(`/api/v1/user/${res.teacherId}`, {
                 method: 'GET',
                 headers: {
                       'Authorization': `Bearer ${Cookies.get("token")}`,
                       'Accept': 'application/json',
                       'Content-Type': 'application/json',
                 }})
                 .then(res => res.json());
        let teacher_info = document.createElement("a");
        teacher_info.innerHTML = `Викладач: ${teacher.firstname + " " + teacher.lastname}`;
        container.appendChild(teacher_info);

        let button_assessments = document.createElement("button");
        let button_tasks = document.createElement("button");
        let button_chats = document.createElement("button");
        let button_materials = document.createElement("button");

        button_assessments.innerText = "Переглянути оцінки";
        button_tasks.innerText = "Перейти до завдань";
        button_chats.innerText = "Перейти до чату";
        button_materials.innerText = "Перейти до навчальних матеріалів";

        if (Cookies.get("role") == "STUDENT") {
            button_assessments.addEventListener("click", () => toAssessments(res.id));
        } else if (Cookies.get("role") == "TEACHER") {
            button_assessments.addEventListener("click", () => getStudentsByCourse(res.id));

        }

        button_tasks.addEventListener("click", () => toTasksByCourse(res.id));
        button_chats.addEventListener("click", () => goToChat(res.id));
        button_materials.addEventListener("click", () => toMaterials(res.id));

        container.appendChild(button_assessments);
        container.appendChild(button_tasks);
        container.appendChild(button_chats);
        container.appendChild(button_materials);
    });
}

function getFinishedTeacherCourses(handler) {
    fetch("/api/v1/course/my-courses/finished", {
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
}

function getUnfinishedTeacherCourses(handler) {
    fetch("/api/v1/course/my-courses/unfinished", {
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
}