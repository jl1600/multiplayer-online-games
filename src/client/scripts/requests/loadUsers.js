if (typeof xhr1 === "undefined") xhr1 = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", loadUsers, false);

function loadUsers() {
	const userType = sessionStorage.getItem("userType");
	xhr1.open("GET", "http://localhost:8000/user/all-members" + (userType === "ADMIN" ? "" : `?userid=${ sessionStorage.getItem("userId") }`));

	xhr1.onreadystatechange = () => {
		if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 200) {
			JSON.parse(xhr1.response).forEach(user => createRow("users", user, userType === "ADMIN" ? "EDIT" : "Add friend"));
			listenForClicks(userType);
		} else if (xhr1.readyState === XMLHttpRequest.DONE && xhr1.status === 400) {
			alert("Invalid id");
		}
	}

	xhr1.send();
}

function listenForClicks(userType) {
	document.querySelectorAll("#users .button")?.forEach(el => {
		const userId = el.parentElement.parentElement.getAttribute("data-id");
		el.addEventListener("click", () => {
			switch (userType) {
				case "ADMIN":
					window.location = `http://localhost:8080/pages/my-games?userId=${ userId }`;
				break;
				case "MEMBER":
					addFriend(userId);
				break;
				case "TRIAL":
					window.location = "http://localhost:8080";
				break;
			}
		});
	});
}

function addFriend(receiverID) {
	xhr1.open("POST", "http://localhost:8000/user/send-friend-request");

	xhr1.onreadystatechange = () => {
		if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 200) {
			alert("Friend request sent!");
		} else if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 400) {
			alert("userID is invalid");
		}
	}

	xhr1.send(JSON.stringify({
		senderID: sessionStorage.getItem("userId"),
		receiverID
	}));
}
