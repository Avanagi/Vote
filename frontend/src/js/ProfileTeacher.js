let pollItems = [];
let currentIndex = 0;
const itemsPerPage = 3;
let renderPage;
let teacherPollsContainer = null;

async function createPoll() {
    const question = document.getElementById("poll-question").value;
    const optionsText = document.getElementById("poll-options").value
        .split("\n")
        .filter(option => option.trim() !== "");
    const groupsInput = document.getElementById("poll-groups").value.trim();
    const pollCreationMessage = document.getElementById("poll-creation-message");
    const pollCreationError = document.getElementById("poll-creation-error");

    if (!question || optionsText.length < 2) {
        pollCreationError.style.display = "block";
        pollCreationError.textContent = "Пожалуйста, введите вопрос и не менее двух вариантов ответа.";
        return;
    }
    pollCreationError.style.display = "none";

    const options = optionsText.map(text => ({ optionText: text }));
    const visibleFor = groupsInput
        ? groupsInput.split(",").map(g => g.trim()).filter(g => g !== "")
        : null;

    const user = JSON.parse(localStorage.getItem("user"));

    const pollData = {
        question: question,
        options: options,
        visibleFor: visibleFor,
        teacherId: user.id
    };

    try {
        const pollResponse = await fetch("http://localhost:8080/polls", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(pollData),
        });

        if (!pollResponse.ok) {
            const errorData = await pollResponse.json();
            throw new Error(errorData.message || "Ошибка при создании опроса");
        }

        pollCreationMessage.style.display = "block";
        pollCreationMessage.textContent = "Опрос успешно создан!";
        document.getElementById("poll-question").value = "";
        document.getElementById("poll-options").value = "";
        document.getElementById("poll-groups").value = "";

        loadTeacherPollsWithEdit();
        loadPollResults();

    } catch (error) {
        console.error("Ошибка создания опроса:", error);
        pollCreationError.style.display = "block";
        pollCreationError.textContent = "Ошибка создания опроса: " + error.message;
    }
}


async function loadTeacherPollsWithEdit() {
    const user = JSON.parse(localStorage.getItem("user"));
    teacherPollsContainer = document.getElementById("teacher-polls-list");
    teacherPollsContainer.innerHTML = "<p>Загрузка ваших опросов...</p>";

    const oldControls = document.querySelector(".pagination-controls");
    if (oldControls) oldControls.remove();

    try {
        const response = await fetch("http://localhost:8080/polls");
        const polls = await response.json();
        const myPolls = polls.filter(p => p.teacherId === user.id);
        pollItems = [];

        for (const poll of myPolls) {
            const pollDiv = await createPollDiv(poll);
            pollItems.push(pollDiv);
        }

        const controls = document.createElement("div");
        controls.className = "pagination-controls";

        const prevBtn = document.createElement("button");
        prevBtn.textContent = "Назад";

        const nextBtn = document.createElement("button");
        nextBtn.textContent = "Вперёд";

        renderPage = () => {
            teacherPollsContainer.innerHTML = "";
            pollItems.forEach((el, i) => {
                el.style.display = (i >= currentIndex && i < currentIndex + itemsPerPage) ? "block" : "none";
                teacherPollsContainer.appendChild(el);
            });

            prevBtn.disabled = currentIndex === 0;
            nextBtn.disabled = currentIndex + itemsPerPage >= pollItems.length;
        };

        prevBtn.onclick = () => {
            if (currentIndex > 0) {
                currentIndex -= itemsPerPage;
                renderPage();
            }
        };

        nextBtn.onclick = () => {
            if (currentIndex + itemsPerPage < pollItems.length) {
                currentIndex += itemsPerPage;
                renderPage();
            }
        };

        controls.appendChild(prevBtn);
        controls.appendChild(nextBtn);
        teacherPollsContainer.after(controls);

        currentIndex = 0;
        renderPage();

    } catch (err) {
        console.error("Ошибка загрузки опросов преподавателя:", err);
        teacherPollsContainer.innerHTML = "<p>Не удалось загрузить опросы.</p>";
    }
}

async function createPollDiv(poll) {
    const pollDiv = document.createElement("div");
    pollDiv.classList.add("poll-item");
    pollDiv.innerHTML = `<strong>${poll.question}</strong>`;

    const votesResponse = await fetch(`http://localhost:8079/blockchain/results?pollId=${poll.id}`);
    const votesData = await votesResponse.json();
    const totalVotes = Object.values(votesData).reduce((sum, val) => sum + val, 0);

    if (totalVotes === 0) {
        const editBtn = document.createElement("button");
        editBtn.textContent = "Редактировать";
        editBtn.classList.add("submit-btn");
        editBtn.onclick = () => openEditModal(poll);
        pollDiv.appendChild(editBtn);

        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "Удалить опрос";
        deleteBtn.classList.add("close-btn");
        deleteBtn.style.marginTop = "10px";
        deleteBtn.onclick = async () => {
            if (confirm("Вы уверены, что хотите удалить этот опрос?")) {
                try {
                    const response = await fetch(`http://localhost:8080/polls/${poll.id}`, {
                        method: "DELETE"
                    });
                    if (response.ok) {
                        pollDiv.remove();
                        loadTeacherPollsWithEdit();
                        loadPollResults();
                    } else {
                        alert("Ошибка при удалении опроса");
                    }
                } catch (err) {
                    console.error("Ошибка при удалении опроса:", err);
                    alert("Произошла ошибка при удалении опроса.");
                }
            }
        };
        pollDiv.appendChild(deleteBtn);
    } else {
        const note = document.createElement("p");
        note.textContent = "Редактирование недоступно (уже есть голоса)";
        note.style.color = "gray";
        pollDiv.appendChild(note);
    }

    return pollDiv;
}

async function loadPollResults() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || !user.id) {
        console.error("Пользователь не авторизован");
        return;
    }

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
        const pollsResultsContainer = document.getElementById("polls-results");
        pollsResultsContainer.innerHTML = "";
        const oldControls = document.querySelector(".poll-results-pagination-controls");
        if (oldControls) oldControls.remove();

        const pollsResponse = await fetch("http://localhost:8080/polls");
        if (!pollsResponse.ok) throw new Error("Failed to fetch polls");
        const polls = await pollsResponse.json();

        const teacherPolls = polls.filter(poll => poll.teacherId === user.id);

        if (teacherPolls.length === 0) {
            pollsResultsContainer.innerHTML = "<p>У вас нет созданных опросов для отображения результатов.</p>";
            return;
        }

        const itemsPerPage = 3;
        let currentIndex = 0;
        const pollResultItems = [];

        for (const poll of teacherPolls) {
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
                    const optionText = optionsMap.get(parseInt(optionId));
                    const optionResult = document.createElement("div");
                    optionResult.classList.add("option-result");
                    optionResult.textContent = `${optionText || `Option ${optionId}`}: `;

                    optionBar(totalVotes, votes, optionResult, optionsResultsContainer);
                }

                const totalVotesText = document.createElement("p");
                totalVotesText.style.color = "#aaa";
                totalVotesText.textContent = `Всего проголосовало: ${totalVotes}`;
                optionsResultsContainer.appendChild(totalVotesText);

                const groupsText = document.createElement("p");
                groupsText.style.color = "#aaa";
                let groups = [];

                if (Array.isArray(poll.visibleFor)) {
                    groups = poll.visibleFor;
                } else if (typeof poll.visibleFor === "string" && poll.visibleFor.trim() !== "") {
                    groups = poll.visibleFor.split(",").map(g => g.trim());
                }

                if (groups.length === 0) {
                    groupsText.textContent = "Опрос отправлен всем";
                } else {
                    groupsText.textContent = `Опрос отправлен группам: ${groups.join(", ")}`;
                }

                optionsResultsContainer.appendChild(groupsText);
            } else {
                optionsResultsContainer.innerHTML = "<p>Пока нет голосов по этому опросу.</p>";
            }

            pollResultItem.appendChild(optionsResultsContainer);
            pollResultItem.addEventListener("click", () => {
                optionsResultsContainer.style.display =
                    optionsResultsContainer.style.display === "none" ? "block" : "none";
            });

            pollResultItems.push(pollResultItem);
        }

        const renderPage = () => {
            pollsResultsContainer.innerHTML = "";
            pollResultItems.forEach((el, i) => {
                el.style.display = (i >= currentIndex && i < currentIndex + itemsPerPage) ? "block" : "none";
                pollsResultsContainer.appendChild(el);
            });

            prevBtn.disabled = currentIndex === 0;
            nextBtn.disabled = currentIndex + itemsPerPage >= pollResultItems.length;
        };

        const controls = document.createElement("div");
        controls.className = "poll-results-pagination-controls";

        const prevBtn = document.createElement("button");
        prevBtn.textContent = "Назад";
        prevBtn.disabled = true;
        prevBtn.onclick = () => {
            if (currentIndex > 0) {
                currentIndex -= itemsPerPage;
                renderPage();
            }
        };

        const nextBtn = document.createElement("button");
        nextBtn.textContent = "Вперёд";
        nextBtn.onclick = () => {
            if (currentIndex + itemsPerPage < pollResultItems.length) {
                currentIndex += itemsPerPage;
                renderPage();
            }
        };

        controls.appendChild(prevBtn);
        controls.appendChild(nextBtn);
        pollsResultsContainer.after(controls);

        currentIndex = 0;
        renderPage();

    } catch (error) {
        console.error("Ошибка при загрузке результатов опросов:", error);
        document.getElementById("polls-results").innerHTML = "<p>Ошибка загрузки результатов.</p>";
    }
}

let editingPollId = null;
let existingOptionIds = [];

function openEditModal(poll) {
    editingPollId = poll.id;
    existingOptionIds = poll.options ? poll.options.map(option => option.id) : [];

    document.getElementById("edit-poll-question").value = poll.question || "";
    document.getElementById("edit-poll-options").value = poll.options
        ? poll.options.map(option => option.optionText).join("\n")
        : "";
    document.getElementById("edit-poll-groups").value = poll.visibleFor
        ? poll.visibleFor.join(", ")
        : "";

    document.getElementById("edit-poll-message").style.display = "none";
    document.getElementById("edit-poll-error").style.display = "none";

    document.getElementById("edit-modal").style.display = "flex";
}

function closeEditModal() {
    document.getElementById("edit-modal").style.display = "none";
    editingPollId = null;
    existingOptionIds = [];
}


async function submitPollEdit() {
    const question = document.getElementById("edit-poll-question").value;
    const optionsText = document.getElementById("edit-poll-options").value
        .split("\n")
        .map(o => o.trim())
        .filter(Boolean);
    const visibleFor = document.getElementById("edit-poll-groups").value
        .split(",")
        .map(g => g.trim())
        .filter(Boolean);

    const updatedOptions = optionsText.map((text, index) => ({
        id: existingOptionIds[index] || null,
        optionText: text
    }));

    const updatedPoll = {
        id: editingPollId,
        question,
        options: updatedOptions,
        visibleFor
    };

    try {
        const response = await fetch(`http://localhost:8080/polls/${editingPollId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedPoll)
        });

        if (!response.ok) throw new Error("Ошибка при сохранении изменений");

        document.getElementById("edit-poll-message").style.display = "block";
        setTimeout(() => {
            closeEditModal();
            loadTeacherPollsWithEdit();
        }, 1000);
    } catch (error) {
        console.error("Ошибка обновления опроса:", error);
        const errorEl = document.getElementById("edit-poll-error");
        errorEl.style.display = "block";
        errorEl.textContent = "Ошибка: " + error.message;
    }
}