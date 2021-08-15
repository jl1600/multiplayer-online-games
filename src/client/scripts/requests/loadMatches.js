if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", fetchMatches, false);

function fetchMatches() {
	xhr.open("GET", "http://localhost:8000/game/available-matches");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			JSON.parse(xhr.response).forEach(match => createCard(match, "JOIN"));
			listenForClicks();
		}
	}

	xhr.send();
}

function listenForClicks() {
	document.querySelectorAll("#cards-container .card .overlay .img-container .button").forEach(el => {
		el.addEventListener("click", () => {
			window.location = "http://localhost:8080/pages/play-match.html?matchId=" +
				el.parentElement.parentElement.parentElement.getAttribute("data-id");
		});
	});
}
