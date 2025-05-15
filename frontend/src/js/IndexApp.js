async function login() {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const role = document.getElementById('login-role').value;
    const errorMessage = document.getElementById('login-error-message');

    const apiUrl = role === 'student'
        ? 'http://localhost:8080/students/loadStudent'
        : 'http://localhost:8080/teachers/loadTeacher';

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            throw new Error('Ошибка авторизации');
        }

        const user = await response.json();
        user.role = role;
        localStorage.setItem('user', JSON.stringify(user));
        window.location.href = '../src/html/Profile.html';
    } catch (error) {
        console.error(error);
        errorMessage.style.display = 'block';
        errorMessage.textContent = 'Неверный email или пароль';
    }
}

async function registerUser() {
    const name = document.getElementById('register-name').value;
    const surname = document.getElementById('register-surname').value;
    const last_name = document.getElementById('register-last_name').value;
    const age = document.getElementById('register-age').value;
    const sex = document.getElementById('register-sex').value;
    const email = document.getElementById('register-email').value;
    const password = document.getElementById('register-password').value;
    const role = document.getElementById('register-role').value;
    const student_group = document.getElementById('register-student_group').value;
    const errorMessage = document.getElementById('register-error-message');

    const userData = {
        name: name,
        surname: surname,
        lastName: last_name,
        age: parseInt(age),
        sex: sex,
        email: email,
        password: password,
        role: role
    };

    let apiUrl = '';
    if (role === 'student') {
        userData.studentGroup = student_group;
        apiUrl = 'http://localhost:8080/students';
    } else if (role === 'teacher') {
        apiUrl = 'http://localhost:8080/teachers';
    }

    if (!name || !surname || !age || !sex || !email || !password || !role || (role === 'student' && !student_group)) {
        errorMessage.style.display = 'block';
        errorMessage.textContent = 'Пожалуйста, заполните все обязательные поля.';
        return;
    }

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error('Ошибка регистрации');
        }

        alert('Регистрация прошла успешно!');
        document.querySelector('.login-box').style.display = 'block';
        document.querySelector('.registration-box').style.display = 'none';
    } catch (error) {
        console.error('Ошибка регистрации:', error);
        errorMessage.style.display = 'block';
        errorMessage.textContent = 'Ошибка регистрации: ' + error.message;
    }
}

document.getElementById('show-register-form').addEventListener('click', function(event) {
    event.preventDefault();
    document.querySelector('.login-box').style.display = 'none';
    document.querySelector('.registration-box').style.display = 'block';
});

document.getElementById('show-login-form').addEventListener('click', function(event) {
    event.preventDefault();
    document.querySelector('.login-box').style.display = 'block';
    document.querySelector('.registration-box').style.display = 'none';
});

document.getElementById('register-role').addEventListener('change', function() {
    const studentGroupField = document.getElementById('register-student_group');
    if (this.value === 'student') {
        studentGroupField.style.display = 'block';
    } else {
        studentGroupField.style.display = 'none';
    }
});