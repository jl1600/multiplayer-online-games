document.addEventListener("DOMContentLoaded", () => {
    const matchId = window.location.href.split("?matchId=")[1];
    if (!!matchId) joinMatch(matchId);
    createSocket();
}, false);

function joinMatch(matchId) {
    xhr.open("GET", "http://localhost:8000/game/create-match");

    xhr.onreadystatechange = () => {
        if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 400) {
            alert("You're already in a match");
        } else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 403) {
            alert("The match is ongoing or is already finished");
        } else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 404) {
            alert("Invalid userId or matchId");
        }
    };

    xhr.send(JSON.stringify({
        userId: sessionStorage.getItem("user"),
        matchId
    }));
}

function createSocket() {
}

function leaveMatch() {
    xhr.open("GET", "http://localhost:8000/game/leave-match");

    xhr.onreadystatechange = () => {
        if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 204) {
            alert("You're already in a match");
        } else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 400) {
            alert("You're not in this match");
        } else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 404) {
            alert("Invalid userId or matchId");
        }
    };

    xhr.send(JSON.stringify({
        userId: sessionStorage.getItem("user"),
        matchId
    }));
}
