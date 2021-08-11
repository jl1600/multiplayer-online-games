const xhr = new XMLHttpRequest();
const url = "http://localhost:8000/register";

function signup(username, password, confirmPassword) {
	if (!username || !password) return;
	if (!checkPassword(password, confirmPassword)) return;

	xhr.open("POST", url);
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			console.log(xhr.response);
		}
	};

	xhr.send(JSON.stringify({ username, password }));
}

function checkPassword() {
	if (document.getElementById("password").value !== document.getElementById("confirmPassword").value) {
		document.getElementById("errorMessage").innerHTML = "Passwords don't match!";
		return false;
	} else {

		document.getElementById("errorMessage").innerHTML = "";
		return true;
	}
}
function signup(username, password, confirmPassword, userType) {
	if (!username || !password) return;
	if (!checkPassword(password, confirmPassword)) return;

	xhr.open("POST", "http://localhost:8000/register");
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			window.location = "http://localho.st:8080/pages/login";
		}
	};

	xhr.send(JSON.stringify({ username, password, userType }));
}

function checkPassword() {
	if (document.getElementById("password").value !== document.getElementById("confirmPassword").value) {
		document.getElementById("errorMessage").innerHTML = "Passwords don't match!";
		return false;
	} else {
		document.getElementById("errorMessage").innerHTML = "";
		return true;
	}
}
