function openModal(target) {
    if(target) {
        target.showModal(); //show() 대신 showModal()을 써야 배경이 생김
    }
}

function clearModalInput() {
    document.querySelectorAll('.modalForm input').forEach(input => {
        if (input.type === 'radio') {
            input.checked = false;
        } else{
            input.value = '';
        }
    });
}