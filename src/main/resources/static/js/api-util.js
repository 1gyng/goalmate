export const sendRequest = async (url, method, data) => {
    const options = {
        method: method,
        headers: {'Content-Type': 'application/json'},
    }

    if (method !== 'GET' && data) {
        options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);
    const location = response.headers.get('Location');

    const text = await response.text();
    const result = text ? JSON.parse(text) : {};

    if (!response.ok) {
        throw new Error(result.message);
    }

    if (location) {
        result.id = location.split('/').pop();
    }

    return result;
}