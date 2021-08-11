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

    console.log(JSON.stringify({ username, password, userType }));
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
