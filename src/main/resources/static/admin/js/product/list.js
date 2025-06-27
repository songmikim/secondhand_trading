window.addEventListener("DOMContentLoaded", function() {
    const frmList = document.forms['frmList'];
    if (!frmList) return;

    const editBtn = document.getElementById('status-edit-btn');
    const deleteBtn = document.getElementById('delete-btn');

    const getChecked = () => frmList.querySelectorAll("input[name='chk']:checked");

    if (editBtn) {
        editBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (getChecked().length === 0) {
                alert('처리할 상품을 선택하세요.');
                return;
            }
            frmList._method.value = 'PATCH';
            frmList.submit();
        });
    }

    if (deleteBtn) {
        deleteBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (getChecked().length === 0) {
                alert('삭제할 상품을 선택하세요.');
                return;
            }
            if (confirm('선택한 상품을 삭제하시겠습니까?')) {
                frmList._method.value = 'DELETE';
                frmList.submit();
            }
        });
    }
});