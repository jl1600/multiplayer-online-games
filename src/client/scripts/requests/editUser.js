const xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", fillUsername, false);

function fillUsername() {
    xhr.open("GET", "http://localhost:8000/user/username?userid=" + sessionStorage.getItem("userId"));

    	xhr.onreadystatechange = () => {
    		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
    		    document.getElementById("username").value = JSON.parse(xhr.response).username;
    		    document.getElementById("email").value = JSON.parse(xhr.response).email;
    		}
    	}

    	xhr.send();
}

function allowEditUsername() {
    document.getElementById("username").readOnly = false;
    document.getElementById("username").focus();
    document.getElementById("edit-username").hidden = true;
    document.getElementById("save-username").hidden = false;
}
function allowEditEmail() {
    document.getElementById("email").readOnly = false;
    document.getElementById("email").focus();
    document.getElementById("edit-email").hidden = true;
    document.getElementById("save-email").hidden = false;
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
function updateEmail() {
	xhr.open("POST", "http://localhost:8000/user/edit-email");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
            document.getElementById("edit-email").hidden = false;
            document.getElementById("save-email").hidden = true;
            document.getElementById("email").readOnly = true;
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
		    alert("Invalid userID");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
		    alert("Duplicate email");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
            alert("Invalid email");
        }
	}

	xhr.send(JSON.stringify({
	    userID: sessionStorage.getItem("userId"),
	    newEmail: document.getElementById("email").value
	}));
}
function allowEditPassword() {
    document.getElementById("edit-password").hidden = true;
    document.getElementById("save-password").hidden = false;
    document.getElementById("old-password").value = "";
    document.getElementById("old-password").readOnly = false;
    document.getElementById("old-password").focus();
    document.getElementById("new-password").hidden = false;
    document.getElementById("confirm-password").hidden = false;
}
function updatePassword() {
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
            alert("Password edit success.");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("Invalid userID");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
            alert("Incorrect old password");
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 412) {
            alert("Password isn't strong enough");
        }
	}

	xhr.send(JSON.stringify({
	    userID: sessionStorage.getItem("userId"),
	    oldPassword: document.getElementById("old-password").value,
	    newPassword: document.getElementById("new-password").value
	}));
}

