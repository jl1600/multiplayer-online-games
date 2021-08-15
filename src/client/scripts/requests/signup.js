if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

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

	xhr.open("POST", "http://localhost:8000/user/register");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
			window.location = "http://localhost:8080/pages/login.html";
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
		    alert("Username is taken");
		}
	};

	xhr.send(JSON.stringify({
	    userID: sessionStorage.getItem("userId"),
	    username,
	    password,
	    role: userType
	}));
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
