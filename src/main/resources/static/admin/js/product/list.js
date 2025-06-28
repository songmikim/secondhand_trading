document.addEventListener("DOMContentLoaded", function () {

    /**
     * 공통 체크박스 컨트롤러
     * @param {string} checkAllSelector - 전체 체크박스 ID 선택자
     * @param {string} checkboxesSelector - 개별 체크박스 공통 클래스 선택자
     */
    function initCheckController(checkAllSelector, checkboxesSelector) {
        const checkAll = document.getElementById(checkAllSelector);
        const checkboxes = document.querySelectorAll(checkboxesSelector);

        if (!checkAll || checkboxes.length === 0) {
            console.warn(`Check controller not initialized: ${checkAllSelector}, ${checkboxesSelector}`);
            return;
        }

        // 전체 체크 클릭 시
        checkAll.addEventListener("change", function () {
            checkboxes.forEach(cb => cb.checked = checkAll.checked);
        });

        // 개별 체크 클릭 시
        checkboxes.forEach(cb => {
            cb.addEventListener("change", function () {
                if (checkAll.checked) {
                    // 전체 선택 상태에서 개별 클릭 시 전체 + 해당 해제
                    checkAll.checked = false;
                    this.checked = false;
                } else {
                    const checkedCount = Array.from(checkboxes).filter(c => c.checked).length;
                    const totalCount = checkboxes.length;

                    if (checkedCount === totalCount) {
                        // 나머지 모두 선택되면 전체 자동 체크
                        checkAll.checked = true;
                    } else if (checkedCount === 0) {
                        checkAll.checked = false;
                    }
                }
            });
        });
    }

    // 상품 상태 검색 체크박스 컨트롤러 연결
    initCheckController("status-All", ".statusList");

    // 상품 목록 테이블 체크박스 컨트롤러 연결
    initCheckController("check-all", ".row-check");

    /**
     * 상태 select 변경 시 해당 행의 체크박스 자동 체크
     */
    const statusSelects = document.querySelectorAll('select[name^="newStatus_"]');
    statusSelects.forEach(select => {
        select.addEventListener("change", function () {
            // 해당 select가 있는 tr 내에서 .row-check 찾아 체크
            const tr = this.closest('tr');
            if (!tr) return;
            const rowCheck = tr.querySelector('.row-check');
            if (rowCheck) {
                rowCheck.checked = true;
            }
        });
    });
});