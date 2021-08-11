document.addEventListener("DOMContentLoaded", fetchTemplates, false);

function fetchTemplates() {
	xhr.open("GET", "http://localhost:8000/template/all-templates");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			JSON.parse(xhr.response).templates.forEach(template => createCard(template, "USE THIS"));
			listenForClicks();
		}
	};

	xhr.send();
}

function listenForClicks() {
	document.querySelectorAll("#cards-container .card .overlay .img-container .button").forEach(el => {
		el.addEventListener("click", () => {
			window.location = "http://localho.st:8080/pages/create-game?template=" +
				el.parentElement.parentElement.parentElement.getAttribute("data-id");
		});
	});
}
