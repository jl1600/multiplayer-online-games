if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

function reset(username, email) {
	if (!username || !email) return;
	xhr.open("POST", "http://localhost:8080/user/forgot-password");
	xhr.onreadystatechange = () => {
    		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
    			window.location = "http://localhost:8080/pages/login.html";
    		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
    		    alert("Expected an email")
    		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
                alert("Invalid username or Email")
             }
    	};

	xhr.send(JSON.stringify({username, email}));
}

function redirect(){
    window.location = "http://localhost:8080/pages/login.html"
}