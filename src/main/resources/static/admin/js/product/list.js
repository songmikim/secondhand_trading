document.addEventListener("DOMContentLoaded", function () {

    /**
     * 공통 체크박스 컨트롤러
     */
    function initCheckController(checkAllSelector, checkboxesSelector) {
        const checkAll = document.getElementById(checkAllSelector);
        const checkboxes = document.querySelectorAll(checkboxesSelector);

        if (!checkAll || checkboxes.length === 0) {
            console.warn(`Check controller not initialized: ${checkAllSelector}, ${checkboxesSelector}`);
            return;
        }

        checkAll.addEventListener("change", function () {
            checkboxes.forEach(cb => cb.checked = checkAll.checked);
        });

        checkboxes.forEach(cb => {
            cb.addEventListener("change", function () {
                if (checkAll.checked) {
                    checkAll.checked = false;
                    this.checked = false;
                } else {
                    const checkedCount = Array.from(checkboxes).filter(c => c.checked).length;
                    const totalCount = checkboxes.length;
                    checkAll.checked = (checkedCount === totalCount);
                }
            });
        });
    }

    // 체크박스 컨트롤러 연결
    initCheckController("status-All", ".statusList");
    initCheckController("check-all", ".row-check");

    /**
     * 상태 select 변경 시 해당 행 체크
     */
    const statusSelects = document.querySelectorAll('select[name^="newStatus_"]');
    statusSelects.forEach(select => {
        select.addEventListener("change", function () {
            const tr = this.closest('tr');
            if (!tr) return;
            const rowCheck = tr.querySelector('.row-check');
            if (rowCheck) rowCheck.checked = true;
        });
    });

    /**
     * 일괄 상태 변경 / 삭제 처리
     */
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
