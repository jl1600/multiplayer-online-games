const xhr = new XMLHttpRequest();
newTrial();

//document.addEventListener("unload", () => {
//    navigator.sendBeacon("http://localhost:8000/user/logout", JSON.stringify({
//        userID: sessionStorage.getItem("userId")
//    }));
//});

function newTrial() {
	if (!!sessionStorage.getItem("userType")) return;

	xhr.open("POST", "http://localhost:8000/user/trial");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			const data = JSON.parse(xhr.response);
			sessionStorage.setItem("userId", data.userID);
			sessionStorage.setItem("userType", "TRIAL");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 500) {
		    alert("Server error");
		}
	}

	xhr.send();
}
