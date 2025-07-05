window.addEventListener("DOMContentLoaded", function() {
    const { mapLib } = commonLib;

    const el = document.getElementById("map");
    const inputKeyword = document.getElementById("searchKeyword");
    const btnSearch = document.getElementById("btnSearch");

    let curLat = 0, curLon = 0;

    const search = () => {
        const keyword = inputKeyword ? inputKeyword.value.trim() : '';
        const params = new URLSearchParams();
        if (keyword) params.append('skey', keyword);
        if (curLat) params.append('lat', curLat);
        if (curLon) params.append('lon', curLon);
        params.append('cnt', 50);

        fetch(`/restaurant/search?${params.toString()}`)
            .then(res => res.json())
            .then(items => {
                if (!items || items.length === 0) {
                    alert('검색 결과가 없습니다.');
                }
                mapLib.load(el, items, null, '100%', '500px');
            });
    };

    // 현재 위치 설정 후 검색 실행
    navigator.geolocation.getCurrentPosition((pos) => {
        const { latitude: lat, longitude: lon } = pos.coords;
        curLat = lat;
        curLon = lon;
        search();
    });

    if (btnSearch) {
        btnSearch.addEventListener('click', search);
    }
});