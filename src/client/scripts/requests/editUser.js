const xhr = new XMLHttpRequest();
const xhr2 = new XMLHttpRequest();
document.addEventListener("DOMContentLoaded", fillUsername(), false);
document.addEventListener("DOMContentLoaded", fillEmail(), false);


function fillUsername() {
    xhr.open("GET", "http://localhost:8000/user/username?userid=" + sessionStorage.getItem("userId"));
    	xhr.onreadystatechange = () => {
    		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
    		    document.getElementById("username").value = JSON.parse(xhr.response).username;
    		}
    	}
    	xhr.send();
}

function fillEmail() {
    xhr2.open("GET", "http://localhost:8000/user/email?userid=" + sessionStorage.getItem("userId"));

    	xhr2.onreadystatechange = () => {
    		if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 200) {
    		    document.getElementById("email").value = JSON.parse(xhr2.response).email;
    		}
    	}

    	xhr2.send();
}

function allowEditUsername() {
    document.getElementById("username").readOnly = false;
    document.getElementById("username").focus();
    document.getElementById("edit-username").hidden = true;
    document.getElementById("save-username").hidden = false;
}

function updateUsername() {
	xhr.open("POST", "http://localhost:8000/user/edit-username");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
            document.getElementById("edit-username").hidden = false;
            document.getElementById("save-username").hidden = true;
            document.getElementById("username").readOnly = true;
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
		    alert("Invalid userID");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
		    alert("Duplicate username");
		}
	}

	xhr.send(JSON.stringify({
	    userID: sessionStorage.getItem("userId"),
	    newUsername: document.getElementById("username").value
	}));
}

function allowEditEmail(){
    document.getElementById("email").readOnly = false;
    document.getElementById("email").focus();
    document.getElementById("edit-email").hidden = true;
    document.getElementById("save-email").hidden = false;
}

function updateEmail(){
    xhr.open("POST", "http://localhost:8000/user/edit-email");
	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
            document.getElementById("edit-email").hidden = false;
            document.getElementById("save-email").hidden = true;
            document.getElementById("email").readOnly = true;
		}else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
         	alert("Invalid userID");
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 412) {
         	alert("Duplicate email");
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("Invalid email address.");
        }
	}

	xhr.send(JSON.stringify({
	    userId: sessionStorage.getItem("userId"),
	    newEmail: document.getElementById("email").value
	}));
}

function allowEditPassword() {
    document.getElementById("edit-password").hidden = true;
    document.getElementById("save-password").hidden = false;
    document.getElementById("old-password").value = "";
    document.getElementById("old-password").readOnly = false;
    document.getElementById("old-password").required = true;

    document.getElementById("old-password").focus();
    document.getElementById("new-password").hidden = false;
    document.getElementById("confirm-password").hidden = false;

    if (document.getElementById("password-strength").style.display === "none") {
        document.getElementById("password-strength").style.display = "block";
     }

     document.getElementById("new-password").addEventListener("input", () => {
         passwordStrengthChecker(document.getElementById("new-password").value)
     });

}
function updatePassword() {
    if (!checkPassword()) return false;
    xhr.open("POST", "http://localhost:8000/user/edit-password");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
            document.getElementById("edit-password").hidden = false;
            document.getElementById("save-password").hidden = true;
            document.getElementById("new-password").value = "";
            document.getElementById("new-password").hidden = true;
            document.getElementById("confirm-password").value = "";
            document.getElementById("confirm-password").hidden = true;

            document.getElementById("old-password").value = "xxxxxxxx";
            document.getElementById("old-password").readOnly = true;
            document.getElementById("password-strength").style.display = "none"
            alert("Password edit success.");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
            alert("Invalid userID");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
            alert("Incorrect old password");
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("Password isn't strong enough");
        }
	}

	xhr.send(JSON.stringify({
	    userID: sessionStorage.getItem("userId"),
	    oldPassword: document.getElementById("old-password").value,
	    newPassword: document.getElementById("new-password").value
	}));
}

function checkPassword() {
	if (document.getElementById("new-password").value !== document.getElementById("confirm-password").value) {
		document.getElementById("errorMessage").innerHTML = "Passwords don't match!";
		return false;
	} else {
		document.getElementById("errorMessage").innerHTML = "";
		return true;
	}
}

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