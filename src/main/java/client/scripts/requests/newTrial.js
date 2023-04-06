const xhr = new XMLHttpRequest();
let isCmdHeld;
newTrial();

window.addEventListener("mouseover", () => window.onunload = null);
window.addEventListener("mouseout", () => window.onunload = logoutOnLeave);
document.addEventListener("keydown", event => {
    if (event.key === "Meta" || event.key === "Control") {
        window.onunload = logoutOnLeave;
    }
});
document.addEventListener("keyup", event => {
    if (event.key === "Meta" || event.key === "Control") {
        window.onunload = null;
    }
});

function newTrial() {
	if (!!sessionStorage.getItem("userType")) return;

	xhr.open("POST", "http://localhost:8000/user/trial");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			const data = JSON.parse(xhr.response);
			sessionStorage.setItem("userId", data.userID);
			sessionStorage.setItem("userType", "TRIAL");

			const updateHeader = document.getElementById("header").contentWindow.updateHeader;
			if (updateHeader) updateHeader();
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 500) {
		    alert("Server error");
		}
	}

	xhr.send();
}

function logoutOnLeave() {
    const userID = sessionStorage.getItem("userId");
    sessionStorage.clear();
    navigator.sendBeacon("http://localhost:8000/user/logout", JSON.stringify({ userID }));
}
