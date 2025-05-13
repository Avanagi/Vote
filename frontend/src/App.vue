<template>
  <div class="container">
    <div v-if="!isLoggedIn" class="login-box">
      <h2>{{ isRegister ? "Регистрация" : "Вход" }}</h2>
      <form @submit.prevent="handleSubmit">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="email" required />

        <label for="password">Пароль:</label>
        <input type="password" id="password" v-model="password" required />

        <label for="role">Роль:</label>
        <select id="role" v-model="role">
          <option value="student">Студент</option>
          <option value="teacher">Преподаватель</option>
        </select>

        <button type="submit">{{ isRegister ? "Зарегистрироваться" : "Войти" }}</button>
      </form>

      <p @click="toggleMode" class="switch-mode">
        {{ isRegister ? "Уже есть аккаунт? Войти" : "Нет аккаунта? Зарегистрироваться" }}
      </p>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
    </div>

    <div v-else class="user-box">
      <h2>Добро пожаловать, {{ user.name }}!</h2>
      <p><strong>Email:</strong> {{ user.email }}</p>
      <p><strong>Фамилия:</strong> {{ user.surname }}</p>
      <p><strong>Возраст:</strong> {{ user.age }}</p>
      <p><strong>Пол:</strong> {{ user.sex }}</p>
      <button @click="logout">Выйти</button>
    </div>
  </div>
</template>

<script src="js/AppVue.js"></script>

<style src="css/AppVue.css"></style>