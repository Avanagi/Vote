async function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const role = document.getElementById("role").value;
    const errorMessage = document.getElementById("error-message");

    const apiUrl = role === "student"
        ? "http://localhost:8080/students/loadStudent"
        : "http://localhost:8080/teachers/loadTeacher";

    try {
        const response = await fetch(apiUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            throw new Error("Ошибка авторизации");
        }

        const user = await response.json();
        user.role = role;
        localStorage.setItem("user", JSON.stringify(user));
        window.location.href = "../src/html/Profile.html";
    } catch (error) {
        console.error(error);
        errorMessage.style.display = "block";
    }
}