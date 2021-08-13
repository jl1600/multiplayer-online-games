if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

function createGame(templateId) {
	xhr.open("GET", "http://localhost:8000/game/create-builder");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {

		}
	};

	xhr.send(JSON.stringify({
		templateId,
		userId: sessionStorage.getItem("userId")
	}));
}
