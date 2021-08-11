const xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", newTrial, false);

function newTrial() {
    if (sessionStorage.getItem("userType") === "trial") return;

	xhr.open("POST", "http://localhost:8000/trial");
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
		    const data = JSON.parse(xhr.response);
		    sessionStorage.setItem("userId", data.userId);
		    sessionStorage.setItem("userType", "trial");
		}
	};

	xhr.send();
}
