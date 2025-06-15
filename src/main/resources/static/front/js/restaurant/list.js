window.addEventListener("DOMContentLoaded", function() {
    const { mapLib } = commonLib;

    const el = document.getElementById("map");

    // 현재 위치의 식당 정보 조회
    navigator.geolocation.getCurrentPosition((pos) => {
        const { latitude: lat, longitude: lon } = pos.coords;
        fetch(`/restaurant/search?lat=${lat}&lon=${lon}&cnt=50`)
                .then(res => res.json())
                .then(items => {
                    mapLib.load(el, items, null, '100%', '500px');
                });
    });

});