var commonLib = {
    /**
    * ajax 범용 기능
    * @param url : 요청 주소
    * @param successCallback : 성공시 처리 함수
    * @param failureCallback : 실패시 처리 함수
    * @param isText : false - json : text
    */
    ajaxLoad(url, successCallback, failureCallback, method = 'GET', body, header, isText = false) {
        // context path를 고려한 주소 변경
        let baseUrl = document.querySelector("meta[name='base_url']").content;
        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/'));
        url = baseUrl + url;

        const options = {
            method,
        }

        // body 데이터 처리, POST, PUT, PATH일때만 추가
        method = method.toUpperCase();
        if (typeof body === 'string') body = body.trim();

        if (['POST', 'PUT', 'PATH'].includes(method) && body) {
            options.body = body;
        }

        // 요청 헤더 처리
        header = header ?? {};

         // csrf 토큰 처리
        const csrfToken = document.querySelector("meta[name='csrf_token']").content;
        const csrfHeader = document.querySelector("meta[name='csrf_header']").content;
        header[csrfHeader] = csrfToken;
        options.headers = header;

        // ajax 요청 처리
        fetch(url, options)
            .then(res => isText ? res.text() : res.json())
            .then(data => {
                // 성공시 후속 처리
                if (typeof successCallback === 'function') {
                    successCallback(data);
                }
            })
            .catch(err => {
                // 실패시 후속 처리
                if (typeof failureCallback === 'function') {
                    failureCallback(err);
                }
            });
    }
}

/* window.alert를 SweetAlert2로 교체 */
window.alert = function(message, callback) {
    parent.Swal.fire({
      title: message,
      icon: "warning"
    }).then(() => {
        if (typeof callback === 'function') {
            callback();
        }
    })
};