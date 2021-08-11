window.addEventListener("beforeunload", logout);

function logout() {
	xhr.open("POST", "http://localhost:8000/user/logout");
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			sessionStorage.setItem("userId", null);
			sessionStorage.setItem("userType", null);
			newTrial();
		}
	};

	xhr.send(JSON.stringify`{
		userId: sessionStorage.getItem("userId")
	}`);
}

function delete() {
    xhr.open("POST", "http://localhost:8000/user/delete");
    	xhr.setRequestHeader("Content-Type", "application/json");

    	xhr.onreadystatechange = () => {
    		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
    			sessionStorage.setItem("userId", null);
    			sessionStorage.setItem("userType", null);
    			newTrial();
    		}
    	};

    	xhr.send(JSON.stringify`{
    		userId: sessionStorage.getItem("userId")
    	}`);
}

function newTrial() {
	if (sessionStorage.getItem("userType") === "trial") return;

	xhr.open("POST", "http://localhost:8000/trial");
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			const data = JSON.parse(xhr.response);
			sessionStorage.setItem("userId", data.userId);
			sessionStorage.setItem("userType", "trial");
			document.getElementById("header").contentWindow.updateHeader();
            window.location = "http://localho.st:8080/pages/matches";
		}
	};

	xhr.send();
}