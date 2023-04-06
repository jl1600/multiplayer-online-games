if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

function signup(username, password, confirmPassword, email, userType) {
	if (!username || !password || !checkPassword()) return false;
	if (!checkValidEmail(email)) {
	    alert("Email is invalid");
	    return false;
	}

	xhr.open("POST", "http://localhost:8000/user/register");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
			window.location = "http://localhost:8080/pages/login.html";
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
		    alert("Username is already taken.");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 412) {
		    alert("Password is not strong enough.")
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("Email is invalid.")
         }
	};

	xhr.send(JSON.stringify({
	    userID: sessionStorage.getItem("userId"),
	    username,
	    password,
	    email,
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

document.getElementById("password").addEventListener("input", () => {
    passwordStrengthChecker(document.getElementById("password").value)
});


function passwordStrengthChecker(password){
    const charactersBool = hasCharacters(password);
    const numberBool = hasNumber(password);
    const specialCharacterBool = hasSpecialCharacter(password);
    const lengthBool = hasMinLength(password);

    return [charactersBool, numberBool, specialCharacterBool, lengthBool].every(Boolean);
}

function hasCharacters(password) {
    if (password.match(/[a-z]/) && password.match(/[A-Z]/)){
        addCheck("characters");
        return true;
    } else {
        removeCheck("characters");
        return false;
    }
}

function hasNumber(password) {
    if (password.match(/([0-9])/)) {
        addCheck("numbers");
        return true;
    } else {
        removeCheck("numbers");
        return false;
    }
}

function hasSpecialCharacter(password) {
    if (password.match(/[!@#$%^&*()_~?,.<>/;:]/)){
        addCheck("special");
        return true;
    } else{
        removeCheck("special");
        return false;
    }
}

function hasMinLength(password) {
    if (password.length >= 6){
        addCheck("length");
        return true;
    } else {
        removeCheck("length");
        return false;
    }
}

function addCheck(value){
    document.querySelector("." + value + " i").classList.remove("fa-times");
    document.querySelector("." + value + " i").classList.add("fa-check");
}

function removeCheck(value){
    document.querySelector("." + value + " i").classList.add("fa-times");
    document.querySelector("." + value + " i").classList.remove("fa-check");
}

function checkValidEmail(email){
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}
