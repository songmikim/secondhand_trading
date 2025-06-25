/**
* 파일 업로드 후속 처리
*
*/
function fileUploadCallback(items) {
    if (!items || items.length === 0) return;

    const { fileManager } = commonLib;

    // 메인 이미지
    const mainTarget = document.getElementById("main-images");

    // 목록 이미지
    const listTarget = document.getElementById("list-images");
    listTarget.innerHTML = "";

    // 이미지 업로드 시 치환될 템플릿
    const imageTpl = document.getElementById("image-tpl").innerHTML;

    const domParser = new DOMParser();

    for (const item of items) {
        let targetEl = null;
        let html = imageTpl;
        switch (item.location) {
            case "main": // 메인 이미지
                targetEl = mainTarget;
                break;
            case "list": // 목록 이미지
                targetEl = listTarget;
                break;
        }

        const { seq, fileName } = item;

        let baseUrl = document.querySelector("meta[name='base_url']").content;
        baseUrl = baseUrl && baseUrl.lastIndexOf("/") !== -1 ? baseUrl.substring(0, baseUrl.lastIndexOf("/")) : baseUrl;

        let imageUrl = `${baseUrl}/file/thumb?seq=${seq}&width=200&height=200&crop=true`;

        html = html.replace(/\[seq\]/g, seq)
                    .replace(/\[imageUrl\]/g, imageUrl)
                    .replace(/\[fileName\]/g, fileName);

        const dom = domParser.parseFromString(html, "text/html");
        const el = dom.querySelector("div");
        targetEl.append(el);

        // 삭제 처리 버튼 처리
        const removeEl = el.querySelector(".remove");
        if (removeEl) {
            removeEl.addEventListener("click", function() {
                alert('정말 삭제하겠습니까?', () => {
                    fileManager.delete(seq);
                });
            });
        }
    }
}