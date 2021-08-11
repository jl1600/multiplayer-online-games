const xhr = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", fillUsername, false);

function fillUsername() {
    xhr.open("GET", "http://localhost:8000/user/username?userId=" + sessionStorage.getItem("userId"));

    	xhr.onreadystatechange = () => {
    		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
    		    const data = JSON.parse(xhr.response);
    		    document.getElementById("username").value = data.username;
    		}
    	};

    	xhr.send(JSON.stringify({
    	    userId: sessionStorage.getItem("userId"),
    	    username
    	}));
}

function allowEditUsername() {
    document.getElementById("username").readOnly = false;
    document.getElementById("username").focus();
    document.getElementById("edit-username").hidden = true;
    document.getElementById("save-username").hidden = false;
}
function updateUsername(username) {
	xhr.open("POST", "http://localhost:8000/edit");
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            document.getElementById("edit-username").hidden = false;
            document.getElementById("save-username").hidden = true;
            document.getElementById("username").readOnly = true;
		}
	};

	xhr.send(JSON.stringify({
	    userId: sessionStorage.getItem("userId"),
	    username
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
function updatePassword(oldPassword, newPassword) {
    xhr.open("POST", "http://localhost:8000/edit");
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            document.getElementById("edit-password").hidden = false;
            document.getElementById("save-password").hidden = true;
            document.getElementById("new-password").hidden = true;
            document.getElementById("confirm-password").hidden = true;

            document.getElementById("old-password").value = "xxxxxxxx";
            document.getElementById("old-password").readOnly = true;
		}
	};

	xhr.send(JSON.stringify({
	    userId: sessionStorage.getItem("userId"),
	    oldPassword,
	    newPassword
	}));
}