if (typeof xhr1 === "undefined") xhr1 = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", loadUsers, false);

function loadUsers() {
	const userType = sessionStorage.getItem("userType");
	xhr1.open("GET", "http://localhost:8000/user/all-members" + (userType === "ADMIN" ? "" : `?userid=${ sessionStorage.getItem("userId") }`));

	xhr1.onreadystatechange = () => {
		if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 200) {
			JSON.parse(xhr1.response).forEach(user => createRow("users", user, userType === "ADMIN" ? ["SUSPEND", "EDIT"] : ["Add friend"]));
			listenForEdits();
			listenForSuspensions();
			listenForAddFriends();
		} else if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 400) {
			alert("Invalid id");
		}
	}

	xhr1.send();
}

function listenForEdits() {
	document.querySelectorAll("#users .edit-button")?.forEach(el => {
		const user = el.parentElement.parentElement;
		el.addEventListener("click", () => {
		    window.location = `http://localhost:8080/pages/my-games.html?userId=${ user.getAttribute("data-id") }&username=${ user.childNodes[0].nodeValue }`;
		});
	});
}

function listenForSuspensions() {
	document.querySelectorAll("#users .suspend-button")?.forEach(el => {
		el.addEventListener("click", () => {
		    suspendUser(el.parentElement.parentElement.getAttribute("data-id"));
		});
	});
}

function listenForAddFriends() {
	document.querySelectorAll("#users .add-friend-button")?.forEach(el => {
		el.addEventListener("click", () => {
		    addFriend(el.parentElement.parentElement.getAttribute("data-id"));
		});
	});
}

function suspendUser(userID) {
    const length = parseInt(prompt("How many days?"));
    if (isNaN(length)) return alert("Invalid input. Please enter a whole number");

	xhr1.open("POST", "http://localhost:8000/user/suspend");

	xhr1.onreadystatechange = () => {
		if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 204) {
			alert("User suspended");
		} else if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 400) {
			alert("Invalid userID or adminID");
		} else if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 403) {
         	alert("Error: You don't have the permission to perform this command.");
        }
	}

	xhr1.send(JSON.stringify({
		adminID: sessionStorage.getItem("userId"),
		userID,
		banLength: length
	}));
}

function addFriend(receiverID) {
	xhr1.open("POST", "http://localhost:8000/user/send-friend-request");

	xhr1.onreadystatechange = () => {
		if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 204) {
			alert("Friend request sent!");
		} else if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 400) {
			alert("userID is invalid");
		}
	}

	xhr1.send(JSON.stringify({
		senderID: sessionStorage.getItem("userId"),
		receiverID
	}));
}
