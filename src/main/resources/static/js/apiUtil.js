export const sendRequest = async (url, method, data) => {
    const options = {
        method: method,
        headers: {'Content-Type': 'application/json'},
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