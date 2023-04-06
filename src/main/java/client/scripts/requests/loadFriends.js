if (typeof xhr2 === "undefined") xhr2 = new XMLHttpRequest();

document.addEventListener("DOMContentLoaded", loadPendings, false);

function loadPendings() {
	xhr2.open("GET", "http://localhost:8000/user/pending-friends?userid=" + sessionStorage.getItem("userId"));

	xhr2.onreadystatechange = () => {
		if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 200) {
			JSON.parse(xhr2.response).forEach(pendingFriend => createRow("friends", pendingFriend, ["Decline", "Accept"]));
			listenForAcceptances();
			listenForDeclines();
			loadFriends();
		}
	}

	xhr2.send();
}

function loadFriends() {
	xhr2.open("GET", "http://localhost:8000/user/friends?userid=" + sessionStorage.getItem("userId"));

	xhr2.onreadystatechange = () => {
		if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 200) {
			JSON.parse(xhr2.response).forEach(friend => createRow("friends", friend, ["Unfriend"]));
			listenForRemoveFriends();
		}
	}

	xhr2.send();
}

function listenForRemoveFriends() {
	document.querySelectorAll("#friends .unfriend-button")?.forEach(el => {
		el.addEventListener("click", () => {
		    if (confirm("Are you sure you want to remove this friend?")) {
			    removeFriend(el.parentElement.parentElement.getAttribute("data-id"));
			}
		});
	});
}

function removeFriend(receiverID) {
	xhr2.open("POST", "http://localhost:8000/user/remove-friend");

	xhr2.onreadystatechange = () => {
		if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 204) {
			window.location.reload();
		} else if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 400) {
			alert("userID is invalid");
		}
	}

	xhr2.send(JSON.stringify({
		senderID: sessionStorage.getItem("userId"),
		receiverID
	}));
}

function listenForAcceptances() {
	document.querySelectorAll("#friends .accept-button")?.forEach(el => {
		el.addEventListener("click", () => {
			acceptFriendRequest(el.parentElement.parentElement.getAttribute("data-id"));
		});
	});
}

function acceptFriendRequest(receiverID) {
	xhr2.open("POST", "http://localhost:8000/user/accept-pending-friend");

	xhr2.onreadystatechange = () => {
		if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 204) {
			window.location.reload();
		} else if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 400) {
			alert("userID is invalid");
		}
	}

	xhr2.send(JSON.stringify({
		senderID: sessionStorage.getItem("userId"),
		receiverID
	}));
}

function listenForDeclines() {
	document.querySelectorAll("#friends .decline-button")?.forEach(el => {
		el.addEventListener("click", () => {
			declineFriendRequest(el.parentElement.parentElement.getAttribute("data-id"));
		});
	});
}

function declineFriendRequest(receiverID) {
	xhr2.open("POST", "http://localhost:8000/user/decline-pending-friend");

	xhr2.onreadystatechange = () => {
		if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 204) {
			window.location.reload();
		} else if (xhr2.readyState === XMLHttpRequest.DONE && xhr2.status === 400) {
			alert("userID is invalid");
		}
	}

	xhr2.send(JSON.stringify({
		senderID: sessionStorage.getItem("userId"),
		receiverID
	}));
}
