function getFinishedTests(courseId) {
    fetch(`/api/v1/test-answer/course/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json())
    .then(res => {
        document.getElementById("content_container").innerHTML = `<table id="table-content"></table>`;
                    let number = 1;
                    for (let test of res) {
                        let row = document.createElement("tr");
                        row.innerHTML = `<td>${number++}</td><td>Тест</td><td>Оцінка: ${test.mark}</td><td>Час та дата: ${test.date}</td>`;
                        document.getElementById("table-content").appendChild(row);
                    }
        if (Cookies.get("role") == "STUDENT") {
            return;
        }
        for (let i = 0; i < res.length; i++) {
            document.getElementById("table-content").children[i].addEventListener("click", () => showTest(res[i].id));
        }
    });
}

async function getUnfinishedTests(courseId) {
    await fetch(`/api/v1/test/incomplete/course/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        }
    })
    .then(res => res.json())
    .then(res => {
        document.getElementById("content_container").innerHTML = `<h3>Натисніть на тест, щоб почати його проходити</h3><table id="table-content"></table>`;
                    let number = 1;
                    for (let test of res) {
                        let row = document.createElement("tr");
                        row.innerHTML = `<td>${number++}</td><td>Термін до: ${test.endDate}</td><td>Час на виконання: ${test.duration}</td>`;
                        document.getElementById("table-content").appendChild(row);
                    }
        for (let i = 0; i < res.length; i++) {
            document.getElementById("table-content").children[i].addEventListener("click", () => showTest(res[i].id));
        }
    });

    let course = await fetch(`/api/v1/course/${courseId}`).then(res => res.json());

    if (new Date() < new Date(course.endDate) && Cookies.get("role") == "TEACHER") {
        document.getElementById("table-content").innerHTML += `<button onclick=${"toCreationTest("+courseId+")"}>Створити новий тест</button>`;
    }
}

async function addTest(courseId) {
            let endDate = document.getElementById("test_end").value;
            let beginDate = new Date();
            let duration = document.getElementById("test_duration").value;

            await fetch("/api/v1/test/create", {
                      method: "POST",
                      headers: {
                          'Authorization': `Bearer ${Cookies.get("token")}`,
                          'Content-Type': 'application/json',
                      },
                      body: JSON.stringify({courseId, beginDate, endDate, duration})
                  })
                  .then(res => res.json())
                  .then(test => {

                        document.getElementById("content_container").innerHTML = `<a>Створення питання</a>
                                      <div id="content_question_area"></div>`;

                          document.getElementById("content_question_area").innerHTML += `<label>Введіть текст умови до питання</label>
                              <input type="text" id="question_condition"/>
                              <label>Введіть кількість балів за питання</label>
                              <input type="number" id="question_mark"/>
                              <label>Введіть варіанти відповідей</label>
                              <div id="options">
                                  <div>
                                      <input type="text" />
                                      <input type="checkbox" />
                                  </div>
                              </div>
                              <button onclick="addOption()">Додати варіант відповіді</button>
                              <label>Завантажити файл</label>
                              <input id="file" type="file" />
                              <button onclick=${"addQuestion("+test.id+")"}>Додати питання</button>
                              <button onclick=${"getUnfinishedTests("+courseId+")"}>Завершити створення</button>`;
                  });
}

function addOption() {
          document.getElementById("options").innerHTML += `<div><input type="text" /><input type="checkbox" /></div>`;
      };

async function addQuestion(testId) {
           let questionText = document.getElementById("question_condition").value;
           let mark = document.getElementById("question_mark").value;

           let options = [];
           let correct = [];

           for (let option of document.getElementById("options").children) {
               let optionValue = option.children[0].value;
               options.push(optionValue);
               if (option.children[1].checked) {
                   correct.push(optionValue);
               }
           }

           let testQuestion = await fetch("/api/v1/question/create", {
               method: "POST",
               headers: {
                   'Authorization': `Bearer ${Cookies.get("token")}`,
                   'Content-Type': 'application/json',
               },
               body: JSON.stringify({testId, questionText, correct, mark, options})
           })
           .then(res => res.json());

           let formData = new FormData();
           formData.append("file", document.getElementById("file").files[0]);
           formData.append("postId", testQuestion.id);
           if (document.getElementById("file").files[0]) {
               await fetch("/api/v1/file/upload", {
                   method: "POST",
                   headers: {
                       'Authorization': `Bearer ${Cookies.get("token")}`
                   },
                   body: formData
               });
           }

           document.getElementById("question_condition").value = "";
           document.getElementById("question_mark").value = "";
           document.getElementById("file").value = "";
           document.getElementById("options").innerHTML = `<div><input type="text" /><input type="checkbox" /></div>`;

           document.getElementById("content_container").innerHTML += `<div>
            <p>Умова: ${testQuestion.questionText}</p>
            <p>Бали: ${testQuestion.mark}</p>
            <p>Варіант відповідей: ${testQuestion.options}</p>
           </div>`;

      };

function toCreationTest(courseId) {
    let number = 0;
    let test;

    document.getElementById("content_container").innerHTML = `<a>Створення тесту</a>
          <label>Введіть термін закінчення тесту</label>
          <input type="date" id="test_end"/>
          <label>Введіть тривалість тесту</label>
          <input type="number" id="test_duration"/>
          <button onclick=${"addTest("+courseId+")"} id="create_test">Створити тест</button>
          <div id="number_question"></div>
          <div id="content_question_area"></div>`;

}

async function showTest(testId) {
    await fetch("/api/v1/test-answer/create", {
                       method: "POST",
                       headers: {
                          'Authorization': `Bearer ${Cookies.get("token")}`,
                          'Content-Type': 'application/json',
                       },
                       body: JSON.stringify({testId, mark:0})
                   });

    let test = await fetch(`/api/v1/test/${testId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    })
    .then(res => res.json());

    document.getElementById("panel").innerHTML = "";

    let questions = await fetch(`/api/v1/question/test/${testId}`)
                    .then(res => res.json());

    document.getElementById("content_container").innerHTML =`<p>Залишилось часу:</p><p id="timer"></p>`;

    for (let question of questions) {
        let fileId = await fetch(`/api/v1/file/post/${question.id}`, {
            method: "GET",
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
            }
        }).then(res => res.json()).then(res => res[0]);

        document.getElementById("content_container").innerHTML += `<div class="question" id=${question.id} style="padding-bottom:40px">
            <p style="font-size: 20px;font-weight: bold;">Умова: ${question.questionText}</p></div>`;

        if (fileId) {
            document.getElementById(`${question.id}`).innerHTML += `<a style="display:flex" href=${"/api/v1/file/download/" + fileId} target="_blank">Файл</a>`;
        }

        for (let option of question.options) {
            document.getElementById(`${question.id}`).innerHTML += `<input style="margin-left:10px" type="checkbox" value=${'"' + option + '"'} /><label style="margin-fight:30px">${option}</label>`;
        }
    }

    const timerCounter = document.getElementById("timer");

    let leftTime = test.duration * 60;
    window.intervalTimer = setInterval(() => {
        if (leftTime <= 0) {
            finishTest(testId);
        } else {
            leftTime--;
            document.getElementById("timer").innerText = `${formatTime(leftTime)}`;
        }
    }, 1000);

    document.getElementById("content_container").innerHTML += `<button onclick=${"finishTest(" + testId + ")"}>Закінчити</button>`;

}

async function finishTest(testId) {
    clearInterval(window.intervalTimer);
    const testAnswer = await fetch(`/api/v1/test-answer/test/${testId}`, {
              method: "GET",
              headers: {
                  'Authorization': `Bearer ${Cookies.get("token")}`,
              }
          }).then(res => res.json());
    const questionAnswers = [];
    let answers = document.querySelectorAll("#content_container div");

    for (let answer of answers) {
        let questionAnswer = {testAnswerId: testAnswer.id, questionId: answer.id, answer:""};
        for (let answerOption of answer.querySelectorAll("input")) {
            if (answerOption.checked) {
                questionAnswer.answer += answerOption.value + "~";
            }
        }
        if (questionAnswer.answer.length > 0) {
            questionAnswer.answer.slice(0, -1);
        }
        questionAnswers.push(questionAnswer);
    }

    await fetch("/api/v1/test-answer/submit", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(questionAnswers)
    });

    toTests();
}

function formatTime(seconds) {
    let sec = seconds % 60;
    return Math.floor(seconds / 60) + ":" + (sec < 10 ? `0${sec}` : sec);
}

async function toTestsByCourse(courseId) {
    const role = Cookies.get("role");
    if (role == "STUDENT") {
        await getUnfinishedTests(courseId);
    } else {
        await fetch(`/api/v1/test/course/${courseId}`, {
            method: "GET",
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`,
            }
        }).then(res => res.json())
        .then(res => {
            document.getElementById("content_container").innerHTML = `<table id="table-content"></table>`;
            let number = 1;
            for (let test of res) {
                let row = document.createElement("tr");
                row.innerHTML = `<td>${number++}</td><td>Термін до: ${test.endDate}</td><td>Час на виконання: ${test.duration}</td>`;
                document.getElementById("table-content").appendChild(row);
            }
        });
    }

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick=${"getFinishedTests("+ courseId +")"}>Показати завершені завдання</button>
                <button class="panel_element" onclick=${"getUnfinishedTests("+ courseId +")"}>Показати незавершені завдання</button>
                <div class="panel_element" id="find_course">
                  <label>Знайти предмети за назвою</label>
                  <input type="text" id="find_title_field"/>
                  <button id="find_course_button" onclick=${"findTask(showTest,"+ courseId +")"}>Знайти</button>
                </div>`;

    let course = await fetch(`/api/v1/course/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());

    if (new Date() < new Date(course.endDate) && Cookies.get("role") == "TEACHER") {
        document.getElementById("table-content").innerHTML += `<button onclick=${"toCreationTest("+courseId+")"}>Створити новий тест</button>`;
    }
}

async function toTests() {
    let courses = await getUsersCourses();

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick="getFinishedCourses(toTestsByCourse)">Показати завершені предмети</button>
            <button class="panel_element" onclick="getUnfinishedCourses(toTestsByCourse)">Показати незавершені предмети</button>
            <div class="panel_element" id="find_course">
              <label>Знайти предмети за назвою</label>
              <input type="text" id="find_title_field"/>
              <button id="find_course_button" onclick="findCourse(toTestsByCourse)">Знайти</button>
            </div>`;

    displayCourses(courses);
    for (let i = 0; i < courses.length; i++) {
        document.getElementById("table-content").children[i]
                .addEventListener("click", () => toTestsByCourse(courses[i].id));
    }
}
