import { sendRequest } from './apiUtil.js';
import { clearModalInput } from './modal.js';

const elements = {
    modal: {
        login: document.getElementById('loginModal'),
        join: document.getElementById('joinModal')
    },
    joinInput: {
        id: document.getElementById('joinLoginIdInput'),
        password: document.getElementById('joinPasswordInput'),
        nickname: document.getElementById('joinNicknameInput')
    },
    loginInput: {
        id: document.getElementById('loginIdInput'),
        password: document.getElementById('passwordInput')
    },
    errorDiv: {
        join: document.getElementById('joinErrorMsg'),
        login: document.getElementById('loginErrorMsg')
    }
};

document.getElementById('openLoginModal').addEventListener('click', () => {
    clearModalInput();
    elements.modal.login.showModal();
});

document.getElementById('openJoinModal').addEventListener('click', () => {
    clearModalInput();
    elements.modal.join.showModal();
});

document.getElementById('loginModalJoinBtn').addEventListener('click', () => {
    elements.modal.login.close();
    elements.modal.join.showModal();
});

const checkJoinId = async () => {
    if (!elements.joinInput.id.value.trim()) {
        elements.errorDiv.join.innerText = "아이디를 입력해주세요.";
        return;
    }

    try {
        const apiResponse = await sendRequest(`/join/check-id/${elements.joinInput.id.value}`, 'GET');
        alert(apiResponse.message);

        elements.joinInput.id.value = apiResponse.loginId;
        elements.joinInput.id.readOnly = true;
        elements.joinInput.id.style.backgroundColor = '#e9ecef';
    } catch (error) {
        elements.errorDiv.join.innerText = error.message;
    }
}

const join = async () => {
    const joinData = {
        loginId: elements.joinInput.id.value,
        password: elements.joinInput.password.value,
        nickname: elements.joinInput.nickname.value
    };

    try {
        const apiResponse = await sendRequest('/join', 'POST', joinData);
        alert(apiResponse.message);
        location.replace('/');
    } catch (error) {
        elements.errorDiv.join.innerText = error.message;
    }
}

const login = async () => {
    const loginData = {
        loginId: elements.loginInput.id.value,
        password: elements.loginInput.password.value
    };

    try {
        const apiResponse = await sendRequest('/login', 'POST', loginData);
        location.replace('/calendar');
    } catch (error) {
        elements.errorDiv.login.innerText = error.message;
    }
}

document.getElementById('loginIdCheckBtn').addEventListener('click', checkJoinId);
document.getElementById('loginBtn').addEventListener('click', login);
document.getElementById('joinBtn').addEventListener('click', join);