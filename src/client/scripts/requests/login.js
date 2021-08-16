if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

function login(username, password) {
	if (!username || !password) return;

	xhr.open("POST", "http://localhost:8000/user/login");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
			const data = JSON.parse(xhr.response);
			if (data.role === "TEMP") {
			    data.role = "MEMBER"
			}
			sessionStorage.setItem("userId", data.userID);
			sessionStorage.setItem("userType", data.role);

			window.location = data.role === "ADMIN" ? "http://localhost:8080/pages/templates.html" : "http://localhost:8080/pages/matches.html";
			document.getElementById("header").contentWindow.updateHeader();
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
		    alert(xhr.responseText);
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
            alert(xhr.responseText);
        }
	};

	xhr.send(JSON.stringify({ username, password }));
}


function redirect(){
    console.log("redirecting to reset.html")
    window.location = "http://localhost:8080/pages/forgot-password.html"
}