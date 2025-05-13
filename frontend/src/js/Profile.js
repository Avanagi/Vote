document.addEventListener("DOMContentLoaded", () => {
    loadUserData();
    loadPolls();
});

async function loadUserData() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) {
        window.location.href = "index.html";
        return;
    }

    document.getElementById("name").textContent = user.name || "Неизвестно";
    document.getElementById("surname").textContent = user.surname || "Неизвестно";
    document.getElementById("lastName").textContent = user.lastName || "Неизвестно";
    document.getElementById("sex").textContent = user.sex || "Неизвестно";
    document.getElementById("age").textContent = user.age || "Неизвестно";
    document.getElementById("email").textContent = user.email || "Неизвестно";
    document.getElementById("role").textContent = user.role === "student" ? "Студент" : "Преподаватель";
}

async function loadPolls() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) {
        window.location.href = "../index.html";
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/polls/available?userId=${user.id}`);
        if (!response.ok) throw new Error("Ошибка загрузки опросов");

        const polls = await response.json();
        document.getElementById("polls-count").textContent = `Доступно ${polls.length} опросов`;
        const pollsList = document.getElementById("polls-list");
        pollsList.innerHTML = polls.length === 0 ? "<p>Нет доступных опросов</p>" : "";

        polls.forEach(poll => {
            const pollItem = document.createElement("div");
            pollItem.classList.add("poll-item");
            pollItem.textContent = poll.question;

            pollItem.addEventListener("click", () => {
                const existing = pollItem.querySelector(".options-container");
                if (existing) {
                    existing.classList.toggle("hidden");
                    return;
                }

                const optionsContainer = document.createElement("div");
                optionsContainer.classList.add("options-container");

                poll.options.forEach(option => {
                    const optionButton = document.createElement("button");
                    optionButton.classList.add("option-button");
                    optionButton.textContent = option.optionText;

                    optionButton.addEventListener("click", () => {
                        vote(option.id, option.optionText, poll.id);
                    });

                    optionsContainer.appendChild(optionButton);
                });

                pollItem.appendChild(optionsContainer);
            });

            pollsList.appendChild(pollItem);
        });
    } catch (error) {
        console.error("Ошибка загрузки опросов:", error);
        document.getElementById("polls-list").innerHTML = "<p>Не удалось загрузить опросы</p>";
    }
}

async function vote(optionId, optionText, pollId) {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) {
        alert("Пользователь не найден!");
        return;
    }

    const transaction = {
        studentId: user.id.toString(),
        pollId: pollId,
        optionId: optionId,
        timestamp: new Date().getTime()
    };


    try {
        const blockchainResponse = await fetch(`http://localhost:8079/blockchain/submitTransaction`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(transaction)
        });

        if (!blockchainResponse.ok) {
            throw new Error("Ошибка добавления транзакции в блокчейн");
        }

        const voteResponse = await fetch(`http://localhost:8080/polls/${pollId},${user.id}/vote`, {
            method: "POST"
        });

        if (!voteResponse.ok) {
            throw new Error("Ошибка голосования");
        }

        alert(`Ваш голос за "${optionText}" успешно сохранён!`);
        window.location.reload();
    } catch (error) {
        console.error("Ошибка:", error);
        alert("Произошла ошибка при голосовании.");
    }
}

function logout() {
    localStorage.removeItem("user");
    window.location.href = "../index.html";
}
