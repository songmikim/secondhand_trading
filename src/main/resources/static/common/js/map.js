var commonLib = commonLib ?? {}
commonLib.mapLib = {
    callback: null,
    // 카카오 맵 SDK를 동적 로딩
    init() {
        const headEl = document.head;
        // 카카오 맵 SDK 자바스크립트가 추가된 상태가 아니라면 추가
        if (!document.getElementById("kakao-map-sdk")) {
            const scriptEl = document.createElement("script");
            scriptEl.src = "//dapi.kakao.com/v2/maps/sdk.js?appkey=c353cbff16c035545fd3661e0c9019ed";
            scriptEl.id = "kakao-map-sdk";
            headEl.prepend(scriptEl);

            scriptEl.onload = () => {
                if (typeof this.callback === 'function') {
                    this.callback();
                }
            };


        }
    },
    /**
    * 지도 출력

    * @param el : 지도를 출력할 Canvas document 객체
    * @param items : 지도 데이터
    * @param center : 지도 출력시 처음에 위치할 가운데 좌표
    * @param width, height : 지도의 너비, 높이
    */
    load(el, items, center, width, height) {
        // 너비 또는 높이가 설정되어 있는 경우
        if (width) {
            el.style.width = width;
        }

        if (height) {
            el.style.height = height;
        }

        if (center) {
            this.showMap(el, items, center);

        } else { // 가운데 좌표가 없는 경우는 현재 위치기반에서 좌표 가져옴
            navigator.geolocation.getCurrentPosition((pos) => {
              const { latitude: lat, longitude: lon } = pos.coords;
              this.showMap(el, items, {lat, lon});
            },
            (err) => console.error('위치 정보 가져오기 실패:', err),
            { enableHighAccuracy: true }
            );
        }
    },
    showMap(el, items, center) {
        const mapOptions = {
            center: new kakao.maps.LatLng(center.lat, center.lon),
            level: 2,
        };

        const map = new kakao.maps.Map(el, mapOptions);

        // 마커 표기
        if (!items || items.length === 0) return;
        items.forEach(({lat, lon, name, roadAddress}) => {
            const marker = new kakao.maps.Marker({
                position: new kakao.maps.LatLng(lat, lon),
            });

            marker.setMap(map);


            // 인포 윈도우 표시
            const iwContent = `<div class='restaurant-name'>${name}</div>
                <div class='restaurant-address'>${roadAddress}</div>
            `;
            const iwPosition = new kakao.maps.LatLng(lat, lon);

            const infoWindow = new kakao.maps.InfoWindow({
                position: iwPosition,
                content: iwContent,
                removable: true,
            });

            infoWindow.open(map, marker);
        });
    }
};

window.addEventListener("DOMContentLoaded", function() {
   //commonLib.mapLib.init();
});