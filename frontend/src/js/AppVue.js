import axios from "axios";

export default {
    name: "LoginPage",
    data() {
        return {
            email: "",
            password: "",
            role: "student",
            isRegister: false,
            isLoggedIn: false,
            user: null,
            errorMessage: "",
        };
    },
    methods: {
        async handleSubmit() {
            if (this.isRegister) {
                alert("Регистрация пока не реализована.");
                return;
            }

            try {
                const response = await axios.post("http://localhost:8080/students/loadStudent", {
                    email: this.email,
                    password: this.password,
                });

                if (response.status === 200) {
                    this.user = response.data;
                    this.isLoggedIn = true;
                }
            } catch (error) {
                this.errorMessage = "Ошибка входа. Проверьте email и пароль.";
                console.error("Ошибка:", error);
            }
        },
        toggleMode() {
            this.isRegister = !this.isRegister;
        },
        logout() {
            this.isLoggedIn = false;
            this.user = null;
            this.email = "";
            this.password = "";
            this.errorMessage = "";
        },
    },
};