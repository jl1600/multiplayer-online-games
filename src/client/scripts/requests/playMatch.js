if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", () => {
    const matchId = window.location.href.split("?matchId=")[1];
    if (!!matchId) joinMatch(matchId);
    createSocket();
}, false);

function joinMatch(matchID) {
    xhr.open("POST", "http://localhost:8000/game/join-match");

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            createSocket();
        } else if (xhr.readyState === XMLHttpRequest.Done && xhr.status === 400) {
            alert("You're already in a match");
        } else if (xhr.readyState === XMLHttpRequest.Done && xhr.status === 403) {
            alert("The match is ongoing or is already finished");
        } else if (xhr.readyState === XMLHttpRequest.Done && xhr.status === 404) {
            alert("Invalid userId or matchId");
        }
    };

    xhr.send(JSON.stringify({
        userID: sessionStorage.getItem("userId"),
        matchID
    }));
}

function createSocket() {
}

function leaveMatch() {
    xhr.open("POST", "http://localhost:8000/game/leave-match");

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.Done && xhr.status === 204) {
            alert("You're already in a match");
        } else if (xhr.readyState === XMLHttpRequest.Done && xhr.status === 400) {
            alert("You're not in this match");
        } else if (xhr.readyState === XMLHttpRequest.Done && xhr.status === 404) {
            alert("Invalid userId or matchId");
        }
    };

    xhr.send(JSON.stringify({
        userID: sessionStorage.getItem("user"),
        matchID
    }));
}
