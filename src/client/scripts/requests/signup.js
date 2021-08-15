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
	    console.log(xhr.status);
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
			window.location = "http://localhost:8080/pages/login.html";
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
		    alert("Username is taken");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
		    alert("Password is not strong enough")
		}
	};

	xhr.send(JSON.stringify({username, password, role: userType}));
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
    var charactersBool = false;
    var numberBool = false;
    var specialCharacterBool = false;
    var lengthBool = false;

    if (password.match(/([a-z].*[A-Z])/) || password.match(/([A-Z].*[a-z])/)){
        addCheck("characters");
        charactersBool = true;
    } else {
        removeCheck("characters");
        charactersBool = false;
    }


    if (password.match(/([0-9])/)){
        addCheck("numbers");
        numberBool = true;
    } else {
        removeCheck("numbers");
        numberBool = false;
    }

    if (password.match(/[!@#$%^&*()_~?,.<>/;:]/)){
        addCheck("special");
        specialCharacterBool = true;
    } else{
        removeCheck("special");
        specialCharacterBool = false;
    }

    if (password.length >= 6){
        addCheck("length");
        lengthBool = true;
    } else{
        removeCheck("length");
        lengthBool = false;
    }

    return [charactersBool, numberBool, specialCharacterBool, lengthBool].every(Boolean);
}

function addCheck(value){
    document.querySelector("." + value + " i").classList.remove("fa-times");
    document.querySelector("." + value + " i").classList.add("fa-check");
}

function removeCheck(value){
    document.querySelector("." + value + " i").classList.add("fa-times");
    document.querySelector("." + value + " i").classList.remove("fa-check");
}