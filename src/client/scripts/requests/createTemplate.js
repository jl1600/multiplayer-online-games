if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

window.addEventListener("beforeunload", resetQuestions);

function createTemplateBuilder() {
	xhr.open("POST", "http://localhost:8000/template/create-builder");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            resetQuestions();
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 201) {
            promptQuestion(JSON.parse(xhr.response).designQuestion);
            document.getElementsByTagName("button")[1].onclick = makeDesignChoice;
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
            alert("The userID or the genre is invalid");
        }
	};

	xhr.send(JSON.stringify({
		genre: document.querySelector("input[name='templateGenre']:checked").value,
		userID: sessionStorage.getItem("userId")
	}));
}

function makeDesignChoice() {
	const inputs = document.getElementsByTagName("input");
	const choice = inputs[inputs.length - 1].value;
	if (choice == "") return alert("The input is invalid. Please re-enter");

	xhr.open("POST", "http://localhost:8000/template/make-design-choice");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			inputs[inputs.length - 1].readOnly = true;
			promptQuestion(JSON.parse(xhr.response).designQuestion);
		} else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 201) {
			alert("Successfully created template");
			window.location = "http://localhost:8080/pages/templates.html";
		} else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 400) {
			alert("The input is invalid. Please re-enter");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
		    alert("You haven't started creating a template");
		}
	}

	xhr.send(JSON.stringify({
		userID: sessionStorage.getItem("userId"),
		designChoice: choice
	}));
}

function resetQuestions() {
	xhr.open("POST", "http://localhost:8000/template/cancel-builder");

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
