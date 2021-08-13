if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", fetchTemplates, false);

function fetchTemplates() {
	xhr.open("GET", "http://localhost:8000/template/all-templates");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			JSON.parse(xhr.response).forEach(template => createCard(template,
			    sessionStorage.getItem("userType") === "ADMIN" ? "EDIT" : "USE THIS"));
			listenForClicks();
		}
	}

	xhr.send();
}

function listenForClicks() {
	document.querySelectorAll("#cards-container .card .overlay .img-container .button").forEach(el => {
		el.addEventListener("click", () => {
			window.location = sessionStorage.getItem("userType") === "ADMIN" ?
			    "http://localhost:8080/pages/edit-template.html?templateId=" +
				el.parentElement.parentElement.parentElement.getAttribute("data-id") :
				"http://localhost:8080/pages/create-game.html?templateId=" +
				el.parentElement.parentElement.parentElement.getAttribute("data-id");
		});
	});
}
