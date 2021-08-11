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

	document.querySelectorAll("#cards-container .card .overlay .img-container .button.publicity").forEach(el => {
		el.addEventListener("click", () => {
			togglePublicity(el);
		});
	});
}

function deleteGame(el) {
	xhr.open("POST", "http://localhost:8000/game/delete");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			el.parentElement.parentElement.parentElement.remove();
		}
	};

	xhr.send(JSON.stringify({
		gameId: el.parentElement.parentElement.parentElement.getAttribute("data-id"),
		userId: sessionStorage.getItem("userId")
	}));
}

function togglePublicity(el) {
	xhr.open("POST", "http://localhost:8000/game/toggle-publicity");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			el.setAttribute("src", el.getAttribute("src").replace("public", "xxx").replace("private", "public").replace("xxx", "private"));
		}
	};

	xhr.send(JSON.stringify({
		gameId: el.parentElement.parentElement.parentElement.getAttribute("data-id"),
		userId: sessionStorage.getItem("userId")
	}));
}
