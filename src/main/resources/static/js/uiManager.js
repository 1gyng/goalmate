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

export const toast = {
    success: (msg, gravity) => {
        Toastify({
            text: msg,
            duration: 3000,
            gravity: gravity ? gravity : "top",
            position: "right",
            style: {
                background: "#E6F1EC",
                color: "#2F3E3A"
            }
        }).showToast();
    },

    error: (msg) => {
        Toastify({
            text: msg,
            duration: 5000,
            gravity: "bottom",
            position: "right",
            style: {
                background: "#F3E8E8",
                color: "#4A3A3A"
            }
        }).showToast();
    }
};