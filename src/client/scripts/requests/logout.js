if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

window.addEventListener("beforeunload", logout);

function logout() {
	xhr.open("POST", "http://localhost:8000/user/logout");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
			sessionStorage.setItem("userId", null);
			sessionStorage.setItem("userType", null);
			newTrial();
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
            alert("This user does not exist or has already been deleted");
		}
	};

	xhr.send(JSON.stringify({
		userID: sessionStorage.getItem("userId")
	}));
}

function deleteUser() {
    xhr.open("POST", "http://localhost:8000/user/delete");

    	xhr.onreadystatechange = () => {
    		if (xhr.readyState === XMLHttpRequest.Done && xhr.status === 200) {
    			sessionStorage.setItem("userId", null);
    			sessionStorage.setItem("userType", null);
    			newTrial();
    		}
    	};

    	xhr.send(JSON.stringify({
    		userID: sessionStorage.getItem("userId")
    	}));
}

function newTrial() {
	if (sessionStorage.getItem("userType") === "TRIAL") return;

	xhr.open("POST", "http://localhost:8000/user/trial");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			const data = JSON.parse(xhr.response);
			sessionStorage.setItem("userId", data.userID);
			sessionStorage.setItem("userType", "TRIAL");
			document.getElementById("header").contentWindow.updateHeader();
            window.location = "http://localhost:8080/pages/matches";
		}
	};

	xhr.send();
}
