async function toMaterials() {
    let courses = await getUsersCourses();

    document.getElementById("panel").innerHTML = `<button class="panel_element" onclick="getFinishedCourses(getMaterials)">Показати завершені предмети</button>
    <button class="panel_element" onclick="getUnfinishedCourses(getMaterials)">Показати незавершені предмети</button>
    <div class="panel_element" id="find_course">
      <label>Знайти предмети за назвою</label>
      <input type="text" id="find_title_field"/>
      <button id="find_course_button" onclick="findCourse(getMaterials)">Знайти</button>
    </div>`;

    displayCourses(courses);
    for (let i = 0; i < courses.length; i++) {
        document.getElementById("table-content").children[i]
                .addEventListener("click", () => getMaterials(courses[i].id));
    }
}

async function addMaterial(courseId) {
    const file = document.getElementById("file").files[0];

    if (!file) {
        return;
    }

    const materialId = await fetch(`/api/v1/material/course/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());

    let formData = new FormData();
    formData.append("file", file);
    formData.append("postId", materialId);
    await fetch("/api/v1/file/upload", {
        method: "POST",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`
        },
        body: formData
    });

    toMaterials(courseId);
}

async function getMaterials(courseId) {
    let fileIds = await fetch(`/api/v1/material/files/course/${courseId}`, {
        method: "GET",
        headers: {
            'Authorization': `Bearer ${Cookies.get("token")}`,
        }
    }).then(res => res.json());

    document.getElementById("content_container").innerHTML = `<table id="table-content"></table>`;

    for (let fileId of fileIds) {
        let attachment = await fetch(`/api/v1/file/${fileId}`).then(res => res.json());
        document.getElementById("content_container").innerHTML += `<a href=${"/api/v1/file/download/" + fileId} target="_blank">${attachment.title}</a>`;
    }

    if (Cookies.get("role") == "TEACHER") {
        document.getElementById("content_container").innerHTML += `<input type="file" id="file" />
        <button onclick=${"addMaterial(" + courseId + ")"}>Додати новий матеріал</button>`;
    }
}
