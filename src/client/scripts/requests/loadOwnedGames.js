document.addEventListener("DOMContentLoaded", fetchOwnedGames, false);

function fetchOwnedGames() {
	xhr.open("GET", "http://localhost:8000/game/all-owned-games?userid=" + sessionStorage.getItem("userId"));

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
		    JSON.parse(xhr.response).games.forEach(match => createCard(match, "EDIT"));
		    listenForClicks();
		}
	};

	xhr.send();
}

function listenForClicks() {
    document.querySelectorAll("#cards-container .card .overlay .img-container .button.edit").forEach(el => {
        el.addEventListener("click", () => {
            window.location = "http://localho.st:8080/pages/edit-game?template=" +
                el.parentElement.parentElement.parentElement.getAttribute("data-id");
        });
    });

    document.querySelectorAll("#cards-container .card .overlay .img-container .button.delete").forEach(el => {
        el.addEventListener("click", () => {
            deleteGame(el);
        });
    });
}

function deleteGame(el) {
    const gameId = el.parentElement.parentElement.parentElement.getAttribute("data-id");
    xhr.open("POST", "http://localhost:8000/game/delete" + sessionStorage.getItem("userId"));

    xhr.onreadystatechange = () => {
        if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            el.parentElement.parentElement.parentElement.remove();
        }
    };

    xhr.send(JSON.stringify({
        gameId,
        userId: sessionStorage.getItem("userId")
    }));
}