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
        loadStudentPolls(user.id);
    } else if (user.role === "teacher") {
        document.querySelector(".student-content").style.display = "none";
        document.querySelector(".teacher-content").style.display = "block";
        loadPollResults();
    }
}

async function loadStudentPolls(userId) {
    try {
        const response = await fetch(`http://localhost:8080/polls/available?userId=${userId}`);
        if (!response.ok) throw new Error("Ошибка загрузки опросов");

        const polls = await response.json();
        const pollsCountElement = document.getElementById("student-polls-count");
        pollsCountElement.textContent = `${polls.length}`;
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
                        vote(userId, poll.id, option.id, option.optionText);
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

async function vote(userId, pollId, optionId, optionText) {
    const transaction = {
        studentId: userId.toString(),
        pollId: pollId,
        optionId: optionId,
        timestamp: new Date().getTime()
    };

    try {
        const blockchainResponse = await fetch(`http://localhost:8079/blockchain/submitTransaction`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(transaction)
        });

        if (!blockchainResponse.ok) {
            throw new Error("Ошибка добавления транзакции в блокчейн");
        }

        const voteResponse = await fetch(`http://localhost:8080/polls/${pollId},${userId}/vote`, {
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

async function createPoll() {
    const question = document.getElementById("poll-question").value;
    const optionsText = document.getElementById("poll-options").value.split("\n").filter(option => option.trim() !== "");
    const pollCreationMessage = document.getElementById("poll-creation-message");
    const pollCreationError = document.getElementById("poll-creation-error");

    if (!question || optionsText.length < 2) {
        pollCreationError.style.display = "block";
        pollCreationError.textContent = "Пожалуйста, введите вопрос и не менее двух вариантов ответа.";
        return;
    }
    pollCreationError.style.display = "none";

    const options = optionsText.map(text => ({
        optionText: text
    }));

    const pollData = {
        question: question,
        options: options
    };

    try {
        const pollResponse = await fetch("http://localhost:8080/polls", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(pollData),
        });

        if (!pollResponse.ok) {
            const errorData = await pollResponse.json();
            throw new Error(errorData.message || "Ошибка при создании опроса");
        }

        const poll = await pollResponse.json();

        pollCreationMessage.style.display = "block";
        pollCreationMessage.textContent = "Опрос успешно создан!";
        document.getElementById("poll-question").value = "";
        document.getElementById("poll-options").value = "";

    } catch (error) {
        console.error("Ошибка создания опроса:", error);
        pollCreationError.style.display = "block";
        pollCreationError.textContent = "Ошибка создания опроса: " + error.message;
    }
}

async function loadPollResults() {
    function optionBar(totalVotes, votes, optionResult, optionsResultsContainer) {
        const percentage = totalVotes > 0 ? ((votes / totalVotes) * 100).toFixed(2) : 0;
        const percentageBar = document.createElement("div");
        percentageBar.classList.add("option-result-bar");
        percentageBar.style.width = `${Math.min(percentage, 100)}%`;
        percentageBar.textContent = `${percentage}%`;

        optionResult.appendChild(percentageBar);
        optionsResultsContainer.appendChild(optionResult);
    }

    try {
        const pollsResponse = await fetch("http://localhost:8080/polls");
        if (!pollsResponse.ok) throw new Error("Failed to fetch polls");
        const polls = await pollsResponse.json();
        const pollsResultsContainer = document.getElementById("polls-results");
        pollsResultsContainer.innerHTML = "";

        if (polls.length === 0) {
            pollsResultsContainer.innerHTML = "<p>Нет доступных опросов для отображения результатов.</p>";
            return;
        }

        for (const poll of polls) {
            const pollResultItem = document.createElement("div");
            pollResultItem.classList.add("poll-item");
            pollResultItem.textContent = poll.question;

            const resultsResponse = await fetch(`http://localhost:8079/blockchain/results?pollId=${poll.id}`);
            if (!resultsResponse.ok) throw new Error(`Failed to fetch results for poll ${poll.id}`);
            const resultsData = await resultsResponse.json();

            const optionsResultsContainer = document.createElement("div");
            optionsResultsContainer.classList.add("poll-options-results");
            optionsResultsContainer.style.display = "none";

            if (resultsData && Object.keys(resultsData).length > 0) {
                const totalVotes = Object.values(resultsData).reduce((sum, votes) => sum + votes, 0);

                const optionsResponse = await fetch(`http://localhost:8080/polls/${poll.id}`);
                if (!optionsResponse.ok) throw new Error(`Failed to fetch options for poll ${poll.id}`);
                const pollData = await optionsResponse.json();
                const optionsMap = new Map();
                if (pollData.options) {
                    pollData.options.forEach(option => {
                        optionsMap.set(option.id, option.optionText);
                    });
                }

                for (const optionId in resultsData) {
                    const votes = resultsData[optionId];
                    const optionText = optionsMap.get(parseInt(optionId)); // Get option text
                    if (optionText) {
                        const optionResult = document.createElement("div");
                        optionResult.classList.add("option-result");
                        optionResult.textContent = `${optionText}: `;

                        optionBar(totalVotes, votes, optionResult, optionsResultsContainer);
                    } else {
                        const optionResult = document.cёreateElement("div");
                        optionResult.classList.add("option-result");
                        optionResult.textContent = `Option ${optionId}: `;

                        optionBar(totalVotes, votes, optionResult, optionsResultsContainer);
                    }

                }
            } else {
                optionsResultsContainer.innerHTML = "<p>No votes have been cast for this poll yet.</p>";
            }

            pollResultItem.appendChild(optionsResultsContainer);
            pollResultItem.addEventListener("click", () => {
                optionsResultsContainer.style.display =
                    optionsResultsContainer.style.display === "none" ? "block" : "none";
            });
            pollsResultsContainer.appendChild(pollResultItem);
        }
    } catch (error) {
        console.error("Error loading poll results:", error);
        document.getElementById("polls-results").innerHTML = "<p>Failed to load poll results.</p>";
    }
}

function logout() {
    localStorage.removeItem("user");
    window.location.href = "../index.html";
}
