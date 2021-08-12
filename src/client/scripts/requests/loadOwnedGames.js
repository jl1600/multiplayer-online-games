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

	document.querySelectorAll("#deleted-cards .card .overlay .img-container .button.publicity").forEach(el => {
        el.addEventListener("click", () => {
            recoverGame(el);
        });
    });
}

function deleteGame(el) {
	xhr.open("POST", "http://localhost:8000/game/access-level");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
		    const card = el.parentElement.parentElement.parentElement;
		    card.remove();
		    card.children[1].children[0].children[1].remove();

		    const img = card.children[1].children[0].children[0];
		    img.setAttribute("src", img.getAttribute("src").replace(/\/[a-z]+\.png$/, "/deleted.png"));
		    img.addEventListener("click", () => {
                recoverGame(img);
            });
		    document.getElementById("deleted-cards").appendChild(card);
		}
	};

	xhr.send(JSON.stringify({
		gameId: el.parentElement.parentElement.parentElement.getAttribute("data-id"),
		userId: sessionStorage.getItem("userId"),
		accessLevel: "DELETED"
	}));
}

function recoverGame(el) {
	xhr.open("POST", "http://localhost:8000/game/access-level");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
		    window.location.reload();
		}
	};

	xhr.send(JSON.stringify({
		gameId: el.parentElement.parentElement.parentElement.getAttribute("data-id"),
		userId: sessionStorage.getItem("userId"),
		accessLevel: "PRIVATE"
	}));
}

function togglePublicity(el) {
	xhr.open("POST", "http://localhost:8000/game/access-level");

    let newAccessLevel;
    if (el.getAttribute("src").includes("public")) {
        newAccessLevel = "PRIVATE";
    } else if (el.getAttribute("src").includes("private")) {
        newAccessLevel = "FRIENDS";
    } else if (el.getAttribute("src").includes("friends")) {
        newAccessLevel = "PUBLIC";
    }

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			el.setAttribute("src", el.getAttribute("src").replace(/[a-z]+\.png$/, newAccessLevel.toLowerCase() + ".png"));
		}
	};

	xhr.send(JSON.stringify({
		gameId: el.parentElement.parentElement.parentElement.getAttribute("data-id"),
		userId: sessionStorage.getItem("userId"),
		accessLevel: newAccessLevel
	}));
}
