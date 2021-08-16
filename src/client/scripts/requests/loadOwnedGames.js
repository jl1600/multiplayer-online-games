if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", fetchOwnedGames, false);

function fetchOwnedGames() {
    if (sessionStorage.getItem("userType") === "ADMIN") {
        document.getElementsByTagName("h1")[0].innerHTML = `${ window.location.href.match(/username=(\w+)/)[1] }'s games`;
        xhr.open("GET", `http://localhost:8000/game/all-owned-games?userid=${ window.location.href.match(/userId=(\d+)/)[1] }`);
        document.getElementById("create-card").hidden = true;
    } else {
        xhr.open("GET", `http://localhost:8000/game/all-owned-games?userid=${ sessionStorage.getItem("userId") }`);
    }

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			JSON.parse(xhr.response).forEach(game => createCard(game, "EDIT"));
			listenForClicks();
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("invalid id");
        }
	}

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
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
		    const card = el.parentElement.parentElement.parentElement;
		    card.remove();
		    card.children[1].children[0].children[1].remove();

		    const img = card.children[1].children[0].children[0];
		    img.setAttribute("src", img.getAttribute("src").replace(/\/[a-z]+\.png$/, "/deleted.png"));
		    img.addEventListener("click", () => {
                recoverGame(img);
            });
		    document.getElementById("deleted-cards").appendChild(card);
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
		    alert("You don't have permission to do this");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
		    alert("The userID or gameID is invalid");
		}
	};

	xhr.send(JSON.stringify({
		gameID: el.parentElement.parentElement.parentElement.getAttribute("data-id"),
		userID: sessionStorage.getItem("userId"),
		accessLevel: "DELETED"
	}));
}

function recoverGame(el) {
	xhr.open("POST", "http://localhost:8000/game/undo-access-level");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
		    window.location.reload();
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
            alert("You don't have permission to do this");
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
            alert("The userID or gameID is invalid");
        }
	};

	xhr.send(JSON.stringify({
		gameID: el.parentElement.parentElement.parentElement.getAttribute("data-id"),
		userID: sessionStorage.getItem("userId")
	}));
}

function togglePublicity(el) {
	xhr.open("POST", "http://localhost:8000/game/access-level");

    let newAccessLevel;
    if (el.getAttribute("src").includes("public")) {
        newAccessLevel = "PRIVATE";
    } else if (el.getAttribute("src").includes("private")) {
        newAccessLevel = "FRIEND";
    } else if (el.getAttribute("src").includes("friend")) {
        newAccessLevel = "PUBLIC";
    }

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
			el.setAttribute("src", el.getAttribute("src").replace(/[a-z]+\.png$/, newAccessLevel.toLowerCase() + ".png"));
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
		    alert("You don't have permission to do this");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
		    alert("Invalid gameID or userID");
		}
	};

	xhr.send(JSON.stringify({
		gameID: el.parentElement.parentElement.parentElement.getAttribute("data-id"),
		userID: sessionStorage.getItem("userId"),
		accessLevel: newAccessLevel
	}));
}
