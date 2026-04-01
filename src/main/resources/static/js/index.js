import { sendRequest } from './apiUtil.js';
import { clearModalInput } from './modal.js';
import { toast } from '/js/uiManager.js';

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
    },
    button: {
        loginModal: document.getElementById('openLoginModal'),
        joinModal: document.getElementById('openJoinModal'),
        switchToJoin: document.getElementById('loginModalJoinBtn'),
        idCheck: document.getElementById('loginIdCheckBtn'),
        login: document.getElementById('loginBtn'),
        join: document.getElementById('joinBtn')
    }
};

const apiMessage = {
    login: (nickname) => `${nickname}님 오늘 목표를 확인해볼까요?`,
    join: "회원가입이 완료되었습니다!"
};

elements.button.loginModal.addEventListener('click', () => {
    clearModalInput();
    elements.modal.login.showModal();
});

elements.button.joinModal.addEventListener('click', () => {
    clearModalInput();
    elements.modal.join.showModal();
});

elements.button.switchToJoin.addEventListener('click', () => {
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
        await sendRequest('/join', 'POST', joinData);
        elements.modal.join.close();
        toast.success(apiMessage.join, "bottom");
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
        elements.modal.login.close();
        toast.success(apiMessage.login(apiResponse.nickname), "bottom");
        setTimeout(() => {
            location.replace('/dashboard');
        }, 1500);
    } catch (error) {
        elements.errorDiv.login.innerText = error.message;
    }
}

elements.button.idCheck.addEventListener('click', checkJoinId);
elements.button.login.addEventListener('click', login);
elements.button.join.addEventListener('click', join);