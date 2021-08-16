if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", fetchAllGames, false);

function fetchAllGames() {
	xhr.open("GET", "http://localhost:8000/game/available-games?userid=" + sessionStorage.getItem("userId"),);

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			JSON.parse(xhr.response).forEach(game => createCard(game, "CREATE"));
			listenForClicks();
		}
	}

    xhr.send(JSON.stringify({
        userID: sessionStorage.getItem("userId"),
    }));
}

function listenForClicks() {
	document.querySelectorAll("#cards-container .card .overlay .img-container .button").forEach(el => {
		el.addEventListener("click", () => {
		    xhr.open("POST", "http://localhost:8000/game/create-match");

            xhr.onreadystatechange = () => {
                if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
                    window.location = "http://localhost:8080/pages/play-match.html";
                } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
                    alert("You're already in a match. Please finish it before starting another one");
                } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
                    alert("Invalid userID or gameID");
                }
            };

            xhr.send(JSON.stringify({
                userID: sessionStorage.getItem("userId"),
                gameID: el.parentElement.parentElement.parentElement.getAttribute("data-id")
            }));
		});
	});
}
