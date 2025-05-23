document.addEventListener("DOMContentLoaded", () => {
    loadUserData();
});

async function loadUserData() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) {
        window.location.href = "../index.html";
        return;
    }

    document.getElementById("name").textContent = user.name || "Неизвестно";
    document.getElementById("surname").textContent = user.surname || "Неизвестно";
    document.getElementById("lastName").textContent = user.lastName || "Неизвестно";
    document.getElementById("sex").textContent = user.sex || "Неизвестно";
    document.getElementById("age").textContent = user.age || "Неизвестно";
    document.getElementById("email").textContent = user.email || "Неизвестно";
    document.getElementById("role").textContent = user.role === "student" ? "Студент" : "Преподаватель";

    if (user.role === "student") {
        document.getElementById("student-group-info").style.display = "block";
        document.getElementById("studentGroup").textContent = user.studentGroup || "Неизвестно";
        document.querySelector(".student-content").style.display = "block";
        document.querySelector(".teacher-content").style.display = "none";
        await loadStudentPolls(user.id, user.studentGroup);
        await loadStudentAnswers(user.id);
    } else if (user.role === "teacher") {
        document.querySelector(".student-content").style.display = "none";
        document.querySelector(".teacher-content").style.display = "block";
        await loadPollResults();
        await loadTeacherPollsWithEdit();
    }
}

function logout() {
    localStorage.removeItem("user");
    window.location.href = "../index.html";
}


document.addEventListener('DOMContentLoaded', () => {
    const icon = document.querySelector('.user-icon');
    const info = document.querySelector('.user-info');

    icon.addEventListener('click', (e) => {
        e.stopPropagation();
        info.classList.toggle('visible');
    });

    // Скрытие при клике вне блока
    document.addEventListener('click', (e) => {
        if (!e.target.closest('.user-container')) {
            info.classList.remove('visible');
        }
    });
});