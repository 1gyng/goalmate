import {clearModalInput} from '/js/modal.js';
import {sendRequest} from '/js/apiUtil.js';
import {toast} from '/js/uiManager.js';

let selectedDate = '';
let calendar;

const elements = {
    modal: {
        saveGoal: document.getElementById('saveGoalModal'),
        title: document.getElementById('modalTitle')
    },
    input: {
        task: document.getElementById('taskInput'),
        startDate: document.getElementById('startDateInput'),
        dueDate: document.getElementById('dueDateInput'),
        type: {
            daily: document.querySelector('input[value="DAILY"]')
        },
        types: document.querySelectorAll('input[name="typeInput"]'),
        getCheckedType: () => document.querySelector('input[name="typeInput"]:checked')
    },
    button: {
        saveGoal: document.getElementById('saveGoalBtn'),
        openAddGoal: document.getElementById('openAddGoalBtn'),
        delete: document.getElementById('deleteBtn')
    }
};

const apiMessage = {
    add: "목표가 추가되었습니다!",
    update: "목표가 수정되었습니다!",
    delete: "목표가 삭제되었습니다!"
}

document.addEventListener('DOMContentLoaded', function () {
    const calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,listWeek'
        },
        handleWindowResize: true,
        aspectRatio: 1.0,
        nowIndicator: true,
        fixedWeekCount: false,
        editable: true,
        eventOrder: 'priority',
        height: 'auto',
        dayMaxEvents: 3,
        locale: 'ko',
        buttonText: {
            today: '오늘',
            month: '월간',
            week: '주간',
            day: '일간',
            list: '목록'
        },
        events: async function (info, successCallback, failureCallback) {
            try {
                const apiResponse = await sendRequest('/goals', 'GET');

                const goals = apiResponse.map(goal => {
                    let dueDate = new Date(goal.dueDate);
                    dueDate.setDate(dueDate.getDate() + 1);

                    return {
                        id: goal.id,
                        title: goal.task,
                        start: goal.startDate,
                        end: dueDate.toLocaleDateString('en-CA'),
                        classNames: ['event-' + goal.type.toLowerCase()],
                        priority: determinePriority(goal.type),
                        extendedProps: {
                            type: goal.type
                        }
                    };
                });
                successCallback(goals);

            } catch (error) {
                console.error(error.message);
                failureCallback(error);
            }
        },
        dateClick: function (info) {
            selectedDate = info.dateStr;
            elements.input.startDate.value = selectedDate;
            elements.input.type.daily.checked = true;

            const event = new Event('change');
            elements.input.startDate.dispatchEvent(event);

            openGoalModal();
        },
        eventClick: function (info) {
            elements.modal.saveGoal.dataset.id = info.event.id;
            elements.input.startDate.value = info.event.startStr;
            elements.input.task.value = info.event.title;

            let date = new Date(info.event.end);
            date.setDate(date.getDate() - 1);
            elements.input.dueDate.value = date.toLocaleDateString('en-CA');

            elements.input.types.forEach(input => {
                if (input.value === info.event.extendedProps.type)
                    input.checked = true;
            });

            openGoalModal();
        }
    });
    calendar.render();
});

elements.button.saveGoal.addEventListener('click', async () => {
    const goalId = elements.modal.saveGoal.dataset.id;

    const addGoalData = {
        task: elements.input.task.value,
        type: elements.input.getCheckedType()?.value,
        startDate: elements.input.startDate.value,
        dueDate: elements.input.dueDate.value
    };

    try {
        const method = goalId ? 'PUT' : 'POST';
        const url = goalId ? `/goals/${goalId}` : '/goals'

        await sendRequest(url, method, addGoalData);
        calendar.refetchEvents();
        elements.modal.saveGoal.close();
        toast.success(goalId ? apiMessage.update : apiMessage.add);
    } catch (error) {
        toast.error(error.message);
    }
});

elements.input.startDate.addEventListener('input', e => {
    selectedDate = e.target.value;
});

elements.input.startDate.addEventListener('change', e => {
    const selectedType = elements.input.getCheckedType()?.value;
    const dueDateInput = elements.input.dueDate;

    if (!selectedDate) return;

    let date = new Date(selectedDate);

    if (selectedType === 'DAILY') {
        dueDateInput.value = selectedDate;
        elements.input.startDate.value = dueDateInput.value;
    } else if (selectedType === 'WEEKLY') {
        const day = date.getDay();
        date.setDate(date.getDate() - day); //일요일
        e.target.value = date.toLocaleDateString('en-CA');

        date.setDate(date.getDate() + 6); //토요일
        dueDateInput.value = date.toLocaleDateString('en-CA');
    } else if (selectedType === 'MONTHLY') {
        date.setDate(1);
        e.target.value = date.toLocaleDateString('en-CA');

        date.setMonth(date.getMonth() + 1);
        date.setDate(0);
        dueDateInput.value = date.toLocaleDateString('en-CA');
    } else if (selectedType === 'YEARLY') {
        const year = e.target.value.split('-')[0];
        e.target.value = `${year}-01-01`;
        dueDateInput.value = `${year}-12-31`;
    } else {
        alert('유효한 선택이 아닙니다.');
    }
});

elements.input.types.forEach(input => {
    input.addEventListener('change', () => {
        const startDateInput = elements.input.startDate;

        if (startDateInput.value) {
            const event = new Event('change');
            startDateInput.dispatchEvent(event);
        }
    });
});

const openAddGoal = () => {
    selectedDate = '';
    elements.input.type.daily.checked = true;
    openGoalModal();
}

const determinePriority = (type) => {
    switch (type) {
        case 'DAILY':
            return 1;
        case 'WEEKLY':
            return 2;
        case 'MONTHLY':
            return 3;
        case 'YEARLY':
            return 4;
    }
}

const deleteGoal = async (id) => {
    console.log(id);
    if (!confirm("정말 삭제하시겠습니까?")) return;

    try {
        await sendRequest(`/goals/${id}`, 'DELETE');
        calendar.refetchEvents();
        elements.modal.saveGoal.close();
        toast.success(apiMessage.delete);
    } catch (error) {
        toast.error(error.message);
    }
}

const openGoalModal = () => {
    const modal = elements.modal.saveGoal;
    const saveBtn = elements.button.saveGoal;

    const isUpdate = !!modal.dataset.id; //값이 있으면

    elements.button.delete.classList.toggle('hidden', !isUpdate);

    elements.modal.title.textContent = isUpdate ? '목표 수정' : '새 목표 추가';
    saveBtn.textContent = isUpdate ? '수정' : '추가';

    modal.showModal();
};

elements.modal.saveGoal.addEventListener('close', () => {
    clearModalInput();
    delete elements.modal.saveGoal.dataset.id;
});

elements.button.delete.addEventListener('click', async () => {
    const goalId = elements.modal.saveGoal.dataset.id;

    if (goalId) {
        await deleteGoal(goalId);
    }
});
elements.button.openAddGoal.addEventListener('click', openAddGoal);
elements.input.dueDate.addEventListener('keydown', (e) => e.preventDefault());