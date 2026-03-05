export const sendRequest = async (url, method, data) => {
    const options = {
        method: method,
        headers: {'Content-Type': 'application/json'},
    }

    if (method !== 'GET' && data) {
        options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);

    const text = await response.text();
    const result = text ? JSON.parse(text) : null;

    if (!response.ok) {
        throw new Error(result.message);
    }

    return result;
}