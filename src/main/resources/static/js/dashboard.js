import { sendRequest } from '/js/api-util.js';
import { toast } from '/js/ui-manager.js';

export const EMOTION_ICON = {
    PROUD: "😎",
    EXCITED: "😆",
    GRATEFUL: "🥰",
    NEUTRAL: "😐",
    ANXIOUS: "😰",
    TIRED: "🫠",
    FRUSTRATED: "😖",
    SAD: "😭",
    UNRECORDED: "❔"
};

const API_MESSAGES = {
    ADD: "기록을 저장했습니다!",
    UPDATE: "기록을 수정했습니다!",
    DELETE: "기록을 삭제했습니다!"
};

const cal = new CalHeatmap();

let heatmapData = {};
let currentYear = new Date().getFullYear();

const showEmotionIcon = () => {
    const container = document.getElementById('emotion-container');
    const emotionSpan = container.querySelectorAll('.emotion-icon');

    emotionSpan.forEach(span => {
        const emoKey = span.dataset.emotion;
        span.innerText = EMOTION_ICON[emoKey];
    })
};

const countChar = () => {
    const input = document.getElementById('record-content');
    const countSpan = document.getElementById('char-count');

    input.addEventListener('input', () => {
        countSpan.innerText = input.value.length;
    });
}

const heatmapConfig = (year) => {
    const heatmapArray = Object.values(heatmapData);

    cal.paint({
        itemSelector: "#cal-heatmap",
        domain: {
            type: 'month',
            label: {
                text: 'MMM',
                position: 'bottom',
            },
            dynamicDimension: false
        },
        scale: {
            color: {
                type: 'threshold',
                range: ['#ebedf0', '#657899'],
                domain: [1]
            }
        },
        subDomain: {
            type: 'ghDay',
            radius: 2,
            width: 13,
            height: 13,
            gutter: 3
        },
        date: {
            start: new Date(`${year}-01-01`),
            highlight: [new Date()]
        },
        data: {
            source: heatmapArray,
            x: 'date',
            y: 'value'
        },
        range: 12
    });
}

const formatDateTime = (dateTime) => {
    const date = new Date(dateTime);

    const formatter = new Intl.DateTimeFormat('ko-KR', {
        year: '2-digit',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    });

    return formatter.format(date);
}

const changeUpdatedAt = (createdAt, updatedAt) => {
    const updateSpan = document.getElementById('updated-at-text');
    const isModified = createdAt && updatedAt && createdAt !== updatedAt;

    updateSpan.classList.toggle('hidden', !isModified);

    if (isModified) {
        const formattedUpdatedAt = formatDateTime(updatedAt);
        updateSpan.innerText = `${formattedUpdatedAt} 수정됨`;
    }
}

const getRecordDetail = async (recordDate) => {
    const id = heatmapData[recordDate]?.id;
    if (!id) {
        changeUpdatedAt(null, null);
        return;
    }
    const response = await sendRequest(`/api/daily-records/${id}`, 'GET');

    changeRecordForm(response.recordDate);
    showRecordDetail(response);
};

const loadHeatmap = async (year) => {
    const response = await sendRequest(`/api/daily-records?year=${year}`, 'GET');
    document.getElementById('year-span').innerText = `${year}년`;

    const heatmapArray = response.map(item => ({
        id: item.id,
        date: item.recordDate,
        value: 1
    }));

    heatmapData = Object.fromEntries(heatmapArray.map(item => [item.date, item]));
    heatmapConfig(year);
}

const saveDailyRecord = async () => {
    const selectedDate = document.getElementById('daily-record-container').dataset.recordDate;
    const recordId = heatmapData[selectedDate]?.id;

    const saveDailyRecordData = {
        emotion: document.querySelector('input[name="selectedEmotion"]:checked')?.value,
        content: document.getElementById('record-content').value,
    };

    try {
        if (!recordId) {
            saveDailyRecordData.recordDate = selectedDate;
        }

        const method = recordId ? 'PUT' : 'POST';
        const url = recordId ? `/api/daily-records/${recordId}` : '/api/daily-records'

        const response = await sendRequest(url, method, saveDailyRecordData);
        if (response.id) {
            heatmapData[selectedDate] = {
                id: response.id,
                date: selectedDate,
                value: 1
            };
        }

        heatmapConfig(currentYear);
        changeRecordForm(selectedDate);

        toast.success(recordId ? API_MESSAGES.UPDATE : API_MESSAGES.ADD);
    } catch (error) {
        toast.error(error.message);
    }
};

const showRecordDetail = (data) => {
    const emotionValue = data.emotion;
    const targetRadio = document.querySelector(`input[name="selectedEmotion"][value="${emotionValue}"]`);
    const content = data.content;
    const textInput = document.getElementById('record-content');

    targetRadio.checked = true;
    textInput.value = content;

    changeUpdatedAt(data.createdAt, data.updatedAt);
}

const changeRecordForm = (date) => {
    const isUpdate = !!heatmapData[date]?.id;

    document.getElementById('daily-record-container').dataset.recordDate = date;
    document.getElementById('record-date-span').innerText = date;

    document.getElementById('delete-btn').classList.toggle('hidden', !isUpdate);
    document.getElementById('save-btn').textContent = isUpdate ? '수정' : '추가';
}

const changeYear = async (btnType) => {
    const thisYear = new Date().getFullYear();

    if (btnType === 'LEFT') currentYear -= 1;
    else if (btnType === 'RIGHT') {
        if (currentYear >= thisYear) {
            toast.error(`${thisYear + 1}년은 아직 확인할 수 없어요!`)
            return;
        }
        currentYear += 1;
    }

    await loadHeatmap(currentYear);
}

const deleteRecord = async () => {
    const selectedDate = document.getElementById('daily-record-container').dataset.recordDate;
    const recordId = heatmapData[selectedDate]?.id;

    if (!recordId) return;

    if (!confirm(`해당 날짜(${selectedDate})의 기록을 정말 삭제하시겠습니까?`)) return;

    try {
        await sendRequest(`/api/daily-records/${recordId}`, 'DELETE');
        toast.success(API_MESSAGES.DELETE);

        document.querySelector('form').reset();
        delete heatmapData[selectedDate];
        heatmapConfig(currentYear);
    } catch (error) {
        toast.error(error.message);
    }
}

document.addEventListener('DOMContentLoaded', async function () {
    const today = new Date().toLocaleDateString('en-CA');

    changeRecordForm(today);
    await loadHeatmap(currentYear);
    await getRecordDetail(today);


    cal.on('click', async (event, timestamp) => {
        document.querySelector('form').reset();

        const selectedDate = new Date(timestamp).toLocaleDateString('en-CA');
        changeRecordForm(selectedDate);
        await getRecordDetail(selectedDate);
    })

    showEmotionIcon();
    countChar();
});

document.getElementById('save-btn').addEventListener('click', saveDailyRecord);
document.getElementById('year-left-btn').addEventListener('click', () => changeYear('LEFT'));
document.getElementById('year-right-btn').addEventListener('click', () => changeYear('RIGHT'));
document.getElementById('delete-btn').addEventListener('click', deleteRecord);