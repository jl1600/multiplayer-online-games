function login(username, password) {
	if (!username || !password) return;

	xhr.open("POST", "http://localhost:8000/login");
	xhr.setRequestHeader("Content-Type", "application/json");

	xhr.onreadystatechange = () => {
		if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
			const data = JSON.parse(xhr.response);
			sessionStorage.setItem("userId", data.userId);
			sessionStorage.setItem("userType", data.userType);
			window.location ="http://localho.st:8080/pages/matches";
			document.getElementById("header").contentWindow.updateHeader();
		}
	};

	xhr.send(JSON.stringify({ username, password }));
}
