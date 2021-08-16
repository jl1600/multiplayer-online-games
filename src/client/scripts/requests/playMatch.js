let serverSocket;

if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", () => {
	const matchId = window.location.href.split("?matchId=")[1];
	if (!!matchId) joinMatch(matchId);
	else document.getElementById("start-button").hidden = false;
	createSocket();
}, false);
document.getElementById("match-input").addEventListener("keyup", event => {
    if (event.keyCode === 13) sendInput();
});

function joinMatch(matchID) {
	xhr.open("POST", "http://localhost:8000/game/join-match");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			createSocket();
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
			alert("You're already in a match");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
			alert("The match is ongoing or is already finished");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
			alert("Invalid userID or matchID");
		}
	}

	xhr.send(JSON.stringify({
		userID: sessionStorage.getItem("userId"),
		matchID
	}));
}

function createSocket() {
	serverSocket = new WebSocket("ws://localhost:8888");

	serverSocket.onopen = function(event) {
		serverSocket.send(sessionStorage.getItem("userId"));
	}

	serverSocket.onmessage = function(event) {
		document.getElementById("match-content").textContent = JSON.parse(event.data).textContent;
		displayStats(JSON.parse(event.data));
	}
}

function displayStats(data) {
	document.getElementById("player-stats").textContent = `Player count: ${ data.numPlayers }\n\nPlayer stats:\n\n`;

	for (const [key, value] of Object.entries(data.playerStats)) {
		document.getElementById("player-stats").textContent += `${ key }: ${ value }\n`;
	}
}

function sendInput() {
	serverSocket.send(JSON.stringify({
		sysCommand: "",
		gameMove: document.getElementsByName("matchInput")[0].value
	}));
	document.getElementById("match-input").value = "";
    document.getElementById("match-input").focus();
}

function startMatch() {
	serverSocket.send(JSON.stringify({
		sysCommand: "start",
		gameMove: ""
	}));
	const elem = document.getElementById("start-button");
	elem.parentNode.removeChild(elem);
}

function leaveMatch() {
	xhr.open("POST", "http://localhost:8000/game/leave-match");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
			alert("You're already in a match");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
			alert("You're not in this match");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
			alert("Invalid userId or matchId");
		}
	}

	xhr.send(JSON.stringify({
		userID: sessionStorage.getItem("userId"),
		matchID
	}));
}
