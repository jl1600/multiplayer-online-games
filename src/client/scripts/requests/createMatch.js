function createMatch(gameId) {
	xhr.open("GET", "http://localhost:8000/game/create-match");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {

		}
	};

	xhr.send(JSON.stringify({
		gameId,
		userId: sessionStorage.getItem("userId")
	}));
}
