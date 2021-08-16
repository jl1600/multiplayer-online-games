const xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", fillUsername, false);

function fillUsername() {
    xhr.open("GET", "http://localhost:8000/user/username?userid=" + sessionStorage.getItem("userId"));

    	xhr.onreadystatechange = () => {
    		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
    		    document.getElementById("username").value = JSON.parse(xhr.response).username;
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
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            document.getElementById("edit-password").hidden = false;
            document.getElementById("save-password").hidden = true;
            document.getElementById("new-password").hidden = true;
            document.getElementById("confirm-password").hidden = true;

            document.getElementById("old-password").value = "xxxxxxxx";
            document.getElementById("old-password").readOnly = true;
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("Invalid userID");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
            alert("Incorrect old password");
        }
	}

	xhr.send(JSON.stringify({
	    userID: sessionStorage.getItem("userId"),
	    oldPassword: document.getElementById("old-password").value,
	    newPassword: document.getElementById("new-password").value
	}));
}
