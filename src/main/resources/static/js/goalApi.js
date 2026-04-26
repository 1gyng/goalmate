import { sendRequest } from '/js/apiUtil.js';

export const GOAL_API_MESSAGES = {
    add: "목표가 추가되었습니다!",
    update: "목표가 수정되었습니다!",
    DELETE: "목표가 삭제되었습니다!",
    ERROR: "실패했습니다."
};

export const goalApi = {
    async delete(id) {
        try {
            await sendRequest(`/goals/${id}`, 'DELETE');
            return true;
        }
        catch (error) {
            console.error(error.message);
            return false;
        }
    }
}