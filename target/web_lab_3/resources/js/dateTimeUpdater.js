{
    window.onload = function () {
        let time = $("#time");
        let date = $("#date");
        date.html(new Date().toLocaleDateString());
        time.html(new Date().toLocaleTimeString());
        window.setInterval(function () {
            date.html(new Date().toLocaleDateString());
            time.html(new Date().toLocaleTimeString());
        }, 12000);
    };
}