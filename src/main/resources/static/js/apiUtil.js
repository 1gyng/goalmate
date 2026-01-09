function requestPost(url, data) {
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