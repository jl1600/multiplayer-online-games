if (typeof xhr === "undefined") xhr = new XMLHttpRequest();
getTemplateAttributes();

function getTemplateAttributes() {
    const templateId = window.location.href.split("?templateId=")[1];
    xhr.open("GET", "http://localhost:8000/template/all-attributes?templateid=" + templateId);

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            console.log(JSON.parse(xhr.response).attrMap);
        }
    }

    xhr.send();
}

function createGame(templateId) {
	xhr.open("POST", "http://localhost:8000/template/edit");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {

		}
	};

	xhr.send(JSON.stringify({
		templateID,
		userID: sessionStorage.getItem("userId")
	}));
}
