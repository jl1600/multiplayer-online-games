document.addEventListener("DOMContentLoaded", createGameBuilder, false);

function createGameBuilder(templateId) {
	xhr.open("GET", "http://localhost:8000/game/create-builder");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            promptQuestion("title?", "Untitled game");
		}
	}

	xhr.send(JSON.stringify({
		templateId,
		userId: sessionStorage.getItem("userId")
	}));
}

function promptQuestion(questionText, defaultValue = "") {
    const question = document.createElement("label");
    const input = document.createElement("input");
    input.setAttribute("value", defaultValue);
    input.required = true;
    input.setAttribute("data-id", document.getElementsByTagName("tag").length);
    question.innerHTML = questionText;
    question.appendChild(input);

    document.getElementsByTagName("form")[0].insertBefore(question, document.getElementsByClassName("buttons")[0]);
}

function makeDesignChoice() {
    const inputs = document.getElementsByTagName("input");
    const choice = inputs[inputs.length - 1].value;
    if (choice == "") return alert("The input is invalid. Please re-enter");

    xhr.open("GET", "http://localhost:8000/game/make-design-choice");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            promptQuestion("title?", "Untitled game");
		} else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 201) {
		    alert("Successfully created game");
		    window.location = "http://localho.st:8080/pages/my-games";
		} else if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 400) {
		    alert("The input is invalid. Please re-enter");
		}
	}

	xhr.send(JSON.stringify({
		userId: sessionStorage.getItem("userId"),
		designChoice: choice
	}));
}

function resetQuestions() {
    xhr.open("GET", "http://localhost:8000/game/cancel-builder");

    xhr.onreadystatechange = () => {
        if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            window.location.reload();
        }
    }

    xhr.send(JSON.stringify({
        userId: sessionStorage.getItem("userId")
    }));
}
