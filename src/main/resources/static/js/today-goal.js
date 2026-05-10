import { sendRequest } from '/js/api-util.js';
import { toast } from '/js/ui-manager.js';
import { goalApi, GOAL_API_MESSAGES } from '/js/goal-api.js';

const GOAL_RESULT_MESSAGES = {
    SUCCESS: "목표 달성! 다음 목표도 파이팅 해봐요✨",
    FAILED: "저장 완료. 이번의 아쉬움은 다음에 채워봐요💪"
};

const GOAL_RESULT_CONFIG = {
    SUCCESS: {
        label: '달성',
        badge: 'badge-success'
    },
    FAILED: {
        label: '미달성',
        badge: 'badge-error'
    },
    NONE: {
        label: '미선택',
        badge: 'badge-warning'
    }
};

let currentFilter = '';
let currentGoalListPage = 0;
let currentType = '';

function openDrawer() {
    const container = document.getElementById('drawer-container');
    const checkbox = document.getElementById('goal-list-drawer');

    checkbox.checked = true;
    container.classList.add('drawer-open');
}

function closeDrawer() {
    const container = document.getElementById('drawer-container');
    const checkbox = document.getElementById('goal-list-drawer');

    container.classList.remove('drawer-open');
}

const buildUrl = (type, result = 'ALL', page = 0) => {
    const params = new URLSearchParams();
    params.append('type', type);

    if (result && result !== 'ALL') {
        params.append('result', result);
    }

    params.append('page', page);

    return `/goals?${params.toString()}`;
};

const renderGoalList = async (type, url) => {
    try {
        const apiResponse = await sendRequest(url, 'GET');
        const template = document.getElementById('goal-item-template');
        const fragment = document.createDocumentFragment();
        const container = document.getElementById('goal-list-container');

        let startDate = '';
        let dueDate = '';
        const isDaily = (type === 'DAILY');
        const isLastPage = apiResponse.last;

        apiResponse.content.forEach(g => {
            const clone = template.content.cloneNode(true);

            if (startDate == g.period.startDate && dueDate == g.period.dueDate) {
                clone.querySelector('.goal-period').remove();
            } else {
                startDate = g.period.startDate;
                dueDate = g.period.dueDate;

                if (isDaily) {
                    clone.querySelector('.goal-date').textContent = dueDate;

                } else {
                    clone.querySelector('.goal-date').textContent = `${startDate} ~ ${dueDate}`;
                }
            }

            clone.querySelector('.goal-result').textContent = GOAL_RESULT_CONFIG[g.result].label;
            clone.querySelector('.goal-result').classList.add(GOAL_RESULT_CONFIG[g.result].badge);
            clone.querySelector('.goal-task').textContent = g.task;

            clone.querySelector('.goal-content').dataset.id = g.id;
            clone.querySelector('.goal-content').dataset.task = g.task;
            clone.querySelector('.goal-content').dataset.result = g.result;
            clone.querySelector('.goal-content').dataset.resultDate = g.resultDate;

            fragment.appendChild(clone);
        });
        container.appendChild(fragment);
        lucide.createIcons();

        if (isLastPage) {
            document.getElementById('more-btn-container').classList.add('hidden');
        } else {
            document.getElementById('more-btn-container').classList.remove('hidden');
        }
    } catch (error) {
        console.log(error.message);
    }
}

const resetGoalList = (isAppend) => {
    if (isAppend) {
        return;
    }
    const container = document.getElementById('goal-list-container');

    container.innerHTML = '';
    currentGoalListPage = 0;
};

const resetGoalResult = () => {
    const radios = document.querySelectorAll('input[name="result"]');

    radios.forEach(r => {
        r.checked = false;
    });

    document.querySelector('input[type="date"]').value = '';
    document.getElementById('drawer-goal-task').textContent = '';
};

const showGoalListDrawer = async (type, isAppend = false) => {
    currentFilter = 'ALL';
    resetGoalList(isAppend);
    resetGoalResult();

    const allRadio = document.querySelector('input[name="goalResultFilter"][value="ALL"]');
    allRadio.checked = true;

    const url = buildUrl(type);
    await renderGoalList(type, url);
    openDrawer();
};

async function showNextGoalListPage() {
    currentGoalListPage++;

    const url = buildUrl(currentType, currentFilter, currentGoalListPage);
    resetGoalList(true);
    await renderGoalList(currentType, url);
}

document.getElementById('goal-more-btn').addEventListener('click', showNextGoalListPage);

document.querySelectorAll('.goal-list-btn').forEach(button => {
    button.addEventListener('click', (e) => {
        currentType = e.currentTarget.dataset.type;
        showGoalListDrawer(currentType);
    });
});

document.getElementById('goal-list-drawer').addEventListener('change', (e) => {
    if (!e.target.checked) {
        closeDrawer();
    }
});

document.querySelectorAll('#goal-filter-container input[type="radio"]').forEach(radio => {
    radio.addEventListener('change', async (e) => {
        currentFilter = e.target.value;
        resetGoalList();

        const url = buildUrl(currentType, currentFilter, currentGoalListPage);
        await renderGoalList(currentType, url);
    });
});

const handleDelete = async (id, row) => {
    if (!confirm('해당 목표를 정말 삭제하시겠습니까?')) return;

    const isSuccess = await goalApi.delete(id);

    if (isSuccess) {
        toast.success(GOAL_API_MESSAGES.DELETE);
        row.remove();
    } else {
        toast.error(GOAL_API_MESSAGES.ERROR);
    }
}

document.addEventListener('click', async (e) => {
    const drawer = document.getElementById('drawer-result');
    const row = e.target.closest('.goal-content');

    if (!row) return;

    const deleteBtn = e.target.closest('.goal-delete-btn');
    const goalId = row.dataset.id;

    if (deleteBtn) {
        await handleDelete(goalId, row);
    }

    const btn = e.target.closest('.goal-item-btn');

    document.querySelectorAll('.goal-content.bg-gray-100').forEach(btn => {
        btn.classList.remove('bg-gray-100');
    });

    const task = row.dataset.task;
    const result = row.dataset.result;
    const resultDate = row.dataset.resultDate;
    const resultRadio = document.querySelector(`input[name="result"][value="${result}"]`);
    const resultBtn = document.getElementById('result-update-btn');

    drawer.classList.remove('hidden');
    row.classList.add('bg-gray-100');

    drawer.dataset.id = goalId;
    drawer.dataset.result = result;
    drawer.dataset.resultDate = resultDate;

    resetGoalResult();

    const hasResult = (result !== 'NONE');
    if (hasResult) {
        resultRadio.checked = true;
        document.querySelector('input[type="date"]').value = resultDate;

    }

    drawer.querySelector('span').textContent = task;
    resultBtn.textContent = hasResult ? '수정' : '추가';
});

document.getElementById('result-update-btn').addEventListener('click', async (e) => {
    const drawer = document.getElementById('drawer-result');
    const goalId = drawer.dataset.id;

    const savedResult = drawer.dataset.result;
    const savedResultDate = drawer.dataset.resultDate;

    const selectedResult = drawer.querySelector('input[name="result"]:checked').value;
    const selectedResultDate = drawer.querySelector('input[type="date"]').value;

    if (savedResult === selectedResult && savedResultDate === selectedResultDate) {
        return;
    }

    const updateResultData = {
        result: selectedResult,
        resultDate: selectedResultDate
    };

    try {
        const apiResponse = await sendRequest(`/goals/${goalId}/result`, 'PATCH', updateResultData);
        toast.success(GOAL_RESULT_MESSAGES[apiResponse.result]);

        const targetRow = document.querySelector(`.goal-content[data-id="${goalId}"]`);
        const targetResult = targetRow.querySelector('.goal-result');

        targetResult.classList.forEach(className => {
            if (className.startsWith('badge-') && className !== 'badge-soft') {
                targetResult.classList.remove(className);
            }
        })

        targetResult.classList.add(GOAL_RESULT_CONFIG[selectedResult].badge);
        targetResult.textContent = GOAL_RESULT_CONFIG[selectedResult].label;

        targetRow.dataset.result = selectedResult;
        targetRow.dataset.resultDate = selectedResultDate;
    } catch (error) {
        toast.error(error.message);
    }
});