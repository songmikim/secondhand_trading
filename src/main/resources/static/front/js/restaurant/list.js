//window.addEventListener("DOMContentLoaded", function() {
//    const { mapLib } = commonLib;
//
//    const el = document.getElementById("map");
//    const inputKeyword = document.getElementById("searchKeyword");
//    const btnSearch = document.getElementById("btnSearch");
//
//    let curLat = 0, curLon = 0;
//
//    const search = () => {
//        const keyword = inputKeyword ? inputKeyword.value.trim() : '';
//        const params = new URLSearchParams();
//        if (keyword) params.append('skey', keyword);
//        if (curLat) params.append('lat', curLat);
//        if (curLon) params.append('lon', curLon);
//        params.append('cnt', 50);
//
//        fetch(`/restaurant/search?${params.toString()}`)
//            .then(res => res.json())
//            .then(items => {
//                if (!items || items.length === 0) {
//                    alert('검색 결과가 없습니다.');
//                }
//                mapLib.load(el, items, { lat: curLat, lon: curLon }, '100%', '500px');
//            });
//    };
//
//    // 현재 위치 설정 후 검색 실행
//    navigator.geolocation.getCurrentPosition((pos) => {
//        const { latitude: lat, longitude: lon } = pos.coords;
//        curLat = lat;
//        curLon = lon;
//        search();
//    }, null, { enableHighAccuracy: true });
//
//    if (btnSearch) {
//        btnSearch.addEventListener('click', search);
//    }
//
//    window.setBounds = function() {
//        navigator.geolocation.getCurrentPosition((pos) => {
//            const { latitude: lat, longitude: lon } = pos.coords;
//            curLat = lat;
//            curLon = lon;
//            if (inputKeyword) inputKeyword.value = "";
//            search();
//        }, null, { enableHighAccuracy: true });
//    };
//});


window.addEventListener("DOMContentLoaded", function() {
    const mapContainer = document.getElementById('map');
    const inputKeyword = document.getElementById('searchKeyword');
    const btnSearch = document.getElementById('btnSearch');
    const placesListEl = document.getElementById('placesList');

    const mapOption = {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 3
    };

    const map = new kakao.maps.Map(mapContainer, mapOption);
    const ps = new kakao.maps.services.Places();
    let markers = [];

    const removeMarker = () => {
        markers.forEach(marker => marker.setMap(null));
        markers = [];
        placesListEl.innerHTML = '';
    };
    const displayPlaces = (places) => {
        removeMarker();
        const bounds = new kakao.maps.LatLngBounds();
        places.forEach(place => {
            const position = new kakao.maps.LatLng(place.y, place.x);
            const marker = new kakao.maps.Marker({ position });
            marker.setMap(map);
            markers.push(marker);
            bounds.extend(position);

            const li = document.createElement('li');
            li.innerHTML = `<span class="place-name">${place.place_name}</span>` +
                `<span class="road-address">${place.road_address_name || place.address_name}</span>`;
            placesListEl.appendChild(li);
        });

        map.setBounds(bounds);
    };

    const searchPlaces = () => {
        const keyword = inputKeyword ? inputKeyword.value.trim() : '';
        if (!keyword) {
            alert('검색어를 입력하세요.');
            return;
        }

        ps.keywordSearch(keyword, (data, status) => {
            if (status === kakao.maps.services.Status.OK) {
                displayPlaces(data);
            } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
                alert('검색 결과가 없습니다.');
                removeMarker();
            } else {
                alert('검색 중 오류가 발생했습니다.');
            }
        });
    };

    if (btnSearch) {
        btnSearch.addEventListener('click', searchPlaces);
    }
});