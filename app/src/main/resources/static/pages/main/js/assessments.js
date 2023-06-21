function toAssessments(course_id) {
    fetch(`/api/v1/assessment/my-assessments?courseId=${course_id}`, {
        method: 'GET',
        headers: {
             'Authorization': `Bearer ${Cookies.get("token")}`,
             'Accept': 'application/json',
             'Content-Type': 'application/json',
        }
    })
    .then(res => res.json())
    .then(async assessments => {
        document.getElementById("panel").innerHTML = "";
        document.getElementById("content_container").innerHTML = `<table id="table-content"></table>`;
        let number = 1;
        for (let assessment of assessments) {
            let row = document.createElement("tr");
            row.className = "course";
            let taskTitle = await fetch(`/api/v1/task/${assessment.taskId}`, {
                method: "GET",
                headers: {
                    'Authorization': `Bearer ${Cookies.get("token")}`,
                }
            })
            .then(res => res.json())
            .then(res => res.title)
            .catch(e => "Test");

            row.innerHTML = `<td>${number++}</td><td>${taskTitle}</td><td>${assessment.date}</td><td>${assessment.mark}</td>`;

            document.getElementById("table-content").appendChild(row);
        }
    });


}

async function toAllAssessments() {
    changeMenuElementColor(2);

    let courses = await getUsersCourses();

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick="getFinishedCourses(toAssessments)">Показати завершені предмети</button>
            <button class="panel_element" onclick="getUnfinishedCourses(toAssessments)">Показати незавершені предмети</button>
            <div class="panel_element" id="find_course">
              <label>Знайти предмети за назвою</label>
              <input type="text" id="find_title_field"/>
              <button id="find_course_button" onclick="findCourse(toAssessments)">Знайти</button>
            </div>`;

    displayCourses(courses);
    for (let i = 0; i < courses.length; i++) {
        document.getElementById("table-content").children[i]
                .addEventListener("click", () => toAssessments(courses[i].id));
    }
}

function toTeacherAssessment() {
    changeMenuElementColor(2);

    fetch(`/api/v1/course/teacher/${Cookies.get("userId")}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
                'Content-Type': 'application/json',
        }})
        .then((response) => {
            return response.json();
        })
        .then((res) => {
            document.getElementById("panel").innerHTML = `<button class="panel_element" onclick="getFinishedTeacherCourses(getStudentsByCourse)">Показати завершені предмети</button>
            <button class="panel_element" onclick="getUnfinishedTeacherCourses(getStudentsByCourse)">Показати незавершені предмети</button>
            <div class="panel_element" id="find_course">
              <label>Знайти предмети за назвою</label>
              <input type="text" id="find_title_field"/>
              <button id="find_course_button" onclick="findCourse(getStudentsByCourse)">Знайти</button>
            </div>`;

            displayCourses(res);
            for (let i = 0; i < res.length; i++) {
                document.getElementById("table-content").children[i].addEventListener("click", () => getStudentsByCourse(res[i].id));
            }
        });
}

async function getAssessmentsByStudentAndCourse(studentId, courseId) {
    document.getElementById("panel").innerHTML = "";
    document.getElementById("content_container").innerHTML = `<table id="table-content"></table>`;

    let assessments = await fetch(`/api/v1/assessment/course/${courseId}/student/${studentId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json())
    .then(async res => {
        console.log(res);
        let number = 1;
            for (let assessment of res) {
                let row = document.createElement("tr");
                row.className = "course";
                let taskTitle = await fetch(`/api/v1/task/${assessment.taskId}`, {
                    method: "GET",
                    headers: {
                        'Authorization': `Bearer ${Cookies.get("token")}`,
                    }
                })
                .then(res => res.json())
                .then(res => res.title)
                .catch(e => "Test");;

                row.innerHTML = `<td>${number++}</td><td>${taskTitle}</td><td>${assessment.date}</td><td><input type="text" value=${'"' + assessment.mark + '"'}></button></td><td><button onclick=${"updateAssessment(" + assessment.id + "," + (number - 1) + ")"}>Зберегти</button></td>`;
                document.getElementById("table-content").appendChild(row);
            }
    });
}

function updateAssessment(id, number) {
    let table = document.getElementById("table-content");
    let row = table.children[number - 1];
    let newMark = row.querySelector("input").value;

    fetch("/api/v1/assessment/update-mark", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',

        },
        body: JSON.stringify({id, mark: newMark})
    }).then(res => res.json());
}

async function addAssessment(studentId, taskId) {
    const mark = document.getElementById("assessment_field").value;
    await fetch(`/api/v1/assessment/create`, {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({mark, date: new Date(), studentId, taskId})
    });
}

