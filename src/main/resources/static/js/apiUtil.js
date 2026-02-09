export function requestPost(url, data) {
    return fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            return response.json().then(json => {
                return {
                    isSuccess: response.ok,
                    status: response.status,
                    message: json.message || "",
                    data: json.data || json
                };
            });
        });
}

export const sendRequest = async (url, method, data) => {
    const options = {
        method: method,
        headers: { 'Content-Type': 'application/json' },
    }

    if (method !== 'GET' && data) {
        options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);

    const result = await response.json();

    if (!response.ok) {
        throw new Error(result.message);
    }

    return result;
}