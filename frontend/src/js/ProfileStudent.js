const POLLS_PER_PAGE = 3;
const ANSWERS_PER_PAGE = 3;
let allPolls = [];
let allAnswers = [];
let currentPage = 1;
let currentAnswerPage = 1;
let userIdGlobal = null;
let studentGroupGlobal = null;


async function loadStudentPolls(userId, studentGroup) {
    try {
        userIdGlobal = userId;
        studentGroupGlobal = studentGroup;
        const response = await fetch(`http://localhost:8080/polls/available?userId=${userId}&group=${studentGroup}`);
        if (!response.ok) throw new Error("Ошибка загрузки опросов");

        allPolls = await response.json();

        document.getElementById("student-polls-count").textContent = `${allPolls.length}`;
        currentPage = 1;
        renderPollsPage();
        renderPaginationControls();

        document.getElementById("pagination-controls").style.display = allPolls.length <= POLLS_PER_PAGE ? "none" : "flex";

    } catch (error) {
        console.error("Ошибка загрузки опросов:", error);
        document.getElementById("polls-list").innerHTML = "<p>Не удалось загрузить опросы</p>";
    }
}

function renderPollsPage() {
    const pollsList = document.getElementById("polls-list");
    pollsList.classList.remove('fade-in');
    pollsList.classList.add('fade-out');

    function onTransitionEnd(event) {
        if (event.propertyName === 'opacity' && pollsList.classList.contains('fade-out')) {
            pollsList.removeEventListener('transitionend', onTransitionEnd);
            pollsList.innerHTML = "";

            const startIndex = (currentPage - 1) * POLLS_PER_PAGE;
            const endIndex = startIndex + POLLS_PER_PAGE;
            const pollsToShow = allPolls.slice(startIndex, endIndex);

            pollsToShow.forEach(poll => {
                const pollItem = document.createElement("div");
                pollItem.classList.add("poll-item");
                pollItem.textContent = poll.question;

                const optionsContainer = document.createElement("div");
                optionsContainer.classList.add("options-container");

                poll.options.forEach(option => {
                    const optionButton = document.createElement("button");
                    optionButton.classList.add("option-button");
                    optionButton.textContent = option.optionText;
                    optionButton.addEventListener("click", () => vote(userIdGlobal, poll.id, option.id, option.optionText));
                    optionsContainer.appendChild(optionButton);
                });

                pollItem.appendChild(optionsContainer);
                pollItem.addEventListener("click", () => pollItem.classList.toggle("open"));
                pollsList.appendChild(pollItem);
            });

            pollsList.classList.remove('fade-out');
            pollsList.classList.add('fade-in');
        }
    }

    pollsList.addEventListener('transitionend', onTransitionEnd);
}

function renderPaginationControls() {
    const pagination = document.getElementById("pagination-controls") || document.createElement("div");
    pagination.id = "pagination-controls";
    pagination.style.marginTop = "15px";
    pagination.style.display = "flex";
    pagination.style.justifyContent = "center";
    pagination.style.gap = "10px";
    document.getElementById("polls-list").insertAdjacentElement('afterend', pagination);
    pagination.innerHTML = "";

    const totalPages = Math.ceil(allPolls.length / POLLS_PER_PAGE);

    const prevBtn = document.createElement("button");
    prevBtn.textContent = "Назад";
    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => {
        currentPage--;
        renderPollsPage();
        renderPaginationControls();
    };
    pagination.appendChild(prevBtn);

    const nextBtn = document.createElement("button");
    nextBtn.textContent = "Вперед";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.onclick = () => {
        currentPage++;
        renderPollsPage();
        renderPaginationControls();
    };
    pagination.appendChild(nextBtn);
}

async function loadStudentAnswers(studentId) {
    const answersContainer = document.getElementById("student-poll-answers");
    answersContainer.innerHTML = "<p>Загрузка ответов...</p>";

    try {
        const response = await fetch(`http://localhost:8079/blockchain/studentAnswers?studentId=${studentId}`);
        if (!response.ok) throw new Error("Ошибка загрузки ответов");

        const answers = await response.json();
        const entries = Object.entries(answers);

        if (entries.length === 0) {
            answersContainer.innerHTML = "<p>Вы ещё не участвовали в опросах.</p>";
            renderAnswerPaginationControls();
            return;
        }

        allAnswers = [];
        for (const [pollId, optionId] of entries) {
            try {
                const pollRes = await fetch(`http://localhost:8080/polls/${pollId}`);
                const optRes = await fetch(`http://localhost:8080/options/${optionId}`);
                if (!pollRes.ok || !optRes.ok) throw new Error();

                const pollData = await pollRes.json();
                const optionData = await optRes.json();

                allAnswers.push({ question: pollData.question, answer: optionData.optionText, pollId });
            } catch {
                allAnswers.push({ question: `Не удалось загрузить вопрос для #${pollId}`, answer: "-", pollId });
            }
        }

        currentAnswerPage = 1;
        renderAnswersPage();
        renderAnswerPaginationControls();

    } catch (error) {
        console.error("Ошибка загрузки всех ответов:", error);
        answersContainer.innerHTML = "<p>Не удалось загрузить ответы.</p>";
    }
}

function renderAnswersPage() {
    const container = document.getElementById("student-poll-answers");
    container.classList.remove("fade-in");
    container.classList.add("fade-out");

    setTimeout(() => {
        container.innerHTML = "";
        const start = (currentAnswerPage - 1) * ANSWERS_PER_PAGE;
        const end = start + ANSWERS_PER_PAGE;
        const answersToShow = allAnswers.slice(start, end);

        answersToShow.forEach(({ question, answer }) => {
            const item = document.createElement("div");
            item.classList.add("student-answer-item");
            item.innerHTML = `<p><strong>Опрос:</strong> ${question}</p><p><strong>Ваш ответ:</strong> ${answer}</p>`;
            container.appendChild(item);
        });

        container.classList.remove("fade-out");
        container.classList.add("fade-in");
    }, 400);
}

async function vote(userId, pollId, optionId, optionText) {
    const transaction = {
        studentId: userId.toString(),
        pollId: pollId,
        optionId: optionId,
    };

    try {
        console.log(JSON.stringify(transaction));
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

        await loadStudentPolls(userIdGlobal);
        await loadStudentAnswers(userIdGlobal);

    } catch (error) {
        console.error("Ошибка:", error);
        alert("Произошла ошибка при голосовании.");
    }
}

function renderAnswerPaginationControls() {
    let controls = document.getElementById("answer-pagination-controls");

    if (!controls) {
        controls = document.createElement("div");
        controls.id = "answer-pagination-controls";
        controls.style.marginTop = "20px";
        controls.style.display = "flex";
        controls.style.justifyContent = "center";
        controls.style.gap = "10px";

        const container = document.getElementById("student-poll-answers");
        container.insertAdjacentElement("afterend", controls);
    }

    controls.innerHTML = ""; // Очищаем

    const totalPages = Math.ceil(allAnswers.length / ANSWERS_PER_PAGE);

    const prevBtn = document.createElement("button");
    prevBtn.textContent = "Назад";
    prevBtn.disabled = currentAnswerPage === 1;
    prevBtn.addEventListener("click", () => {
        if (currentAnswerPage > 1) {
            currentAnswerPage--;
            renderAnswersPage();
            renderAnswerPaginationControls();
        }
    });

    const nextBtn = document.createElement("button");
    nextBtn.textContent = "Вперёд";
    nextBtn.disabled = currentAnswerPage === totalPages || totalPages === 0;
    nextBtn.addEventListener("click", () => {
        if (currentAnswerPage < totalPages) {
            currentAnswerPage++;
            renderAnswersPage();
            renderAnswerPaginationControls();
        }
    });

    controls.appendChild(prevBtn);
    controls.appendChild(nextBtn);

    controls.style.display = totalPages <= 1 ? "none" : "flex";
}


setInterval(() => {
    if (userIdGlobal && studentGroupGlobal) {
        loadStudentPolls(userIdGlobal, studentGroupGlobal);
        loadStudentAnswers(userIdGlobal);
    }
}, 60000);