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
	if (!username || !password) return false;
	if (!checkPassword(password, confirmPassword)) return false;

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
