function login(username, password) {
	if (!username || !password) return;

	xhr.open("POST", "http://localhost:8000/user/login");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			const data = JSON.parse(xhr.response);
			sessionStorage.setItem("userId", data.userid);
			sessionStorage.setItem("userType", data.role);

			window.location = data.userType === "admin" ? "http://localhost:8080/pages/templates" : "http://localhost:8080/pages/matches";
			document.getElementById("header").contentWindow.updateHeader();
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
		    alert("Wrong username or password");
		}
	};

	xhr.send(JSON.stringify({ username, password }));
}
