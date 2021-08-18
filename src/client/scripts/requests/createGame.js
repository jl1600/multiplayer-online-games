if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", createGameBuilder, false);

function createGameBuilder() {
	xhr.open("POST", "http://localhost:8000/game/create-builder");

	xhr.onreadystatechange = () => {
	    if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
	        resetQuestions();
	    } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 201) {
			promptQuestion(JSON.parse(xhr.response).designQuestion);
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
			alert("templateID or userID is invalid");
		}
	}

	xhr.send(JSON.stringify({
		templateID: window.location.href.split("?templateId=")[1],
		userID: sessionStorage.getItem("userId")
	}));
}

function makeDesignChoice() {
	const inputs = document.getElementsByTagName("input");
	const choice = inputs[inputs.length - 1].value;
	if (choice == "") return alert("The input is invalid. Please re-enter");

	xhr.open("POST", "http://localhost:8000/game/make-design-choice");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			inputs[inputs.length - 1].readOnly = true;
			promptQuestion(JSON.parse(xhr.response).designQuestion);
		} else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 201) {
			alert("Successfully created game");
			window.location = "http://localhost:8080/pages/my-games.html";
		} else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 400) {
			alert("The input is invalid. Please re-enter");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
		    alert("You haven't started creating a game");
		}
	}

	xhr.send(JSON.stringify({
		userID: sessionStorage.getItem("userId"),
		designChoice: choice
	}));
}

function cancelCreateGame(){
    xhr.open("POST", "http://localhost:8000/game/cancel-builder");

	xhr.onreadystatechange = () => {
		window.location = "http://localhost:8080/pages/my-games.html"
	}

	xhr.send(JSON.stringify({
		userID: sessionStorage.getItem("userId")
	}));
}


function resetQuestions() {
	xhr.open("POST", "http://localhost:8000/game/cancel-builder");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
			window.location.reload();
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
		    alert("You haven't started building anything");
		}
	}

	xhr.send(JSON.stringify({
		userID: sessionStorage.getItem("userId")
	}));
}


