export function clearModalInput() {
    document.querySelectorAll('.modal-form input').forEach(input => {
        if (input.type === 'radio') {
            input.checked = false;
        }

        if (input.readOnly) {
            input.readOnly = false;
            input.style.backgroundColor = '';
        }

        if (input.type !== 'radio') {
            input.value = '';
        }
    });

    document.querySelectorAll('.errorMsg').forEach(div => {
        div.innerText = '';
    });
}