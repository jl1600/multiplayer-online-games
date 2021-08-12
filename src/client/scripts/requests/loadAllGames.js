document.addEventListener("DOMContentLoaded", fetchAllGames, false);

function fetchAllGames() {
	xhr.open("GET", "http://localhost:8000/game/all-public-games");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			JSON.parse(xhr.response).games.forEach(match => createCard(match, "CREATE"));
			listenForClicks();
		}
	};

	xhr.send();
}

function listenForClicks() {
	document.querySelectorAll("#cards-container .card .overlay .img-container .button").forEach(el => {
		el.addEventListener("click", () => {
		    xhr.open("GET", "http://localhost:8000/game/create-match");

            xhr.onreadystatechange = () => {
                if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
                    window.location = "http://localho.st:8080/pages/play-match";
                }
            };

            xhr.send(JSON.stringify({
                userId: sessionStorage.getItem("user"),
                gameId: el.parentElement.parentElement.parentElement.getAttribute("data-id")
            }));
		});
	});
}
