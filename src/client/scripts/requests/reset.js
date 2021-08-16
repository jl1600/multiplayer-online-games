if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

function forgotPass() {
    let username = document.getElementById("username").value;
    let email = document.getElementById("email").value;
    console.log(username, email)
	xhr.open("POST", "http://localhost:8080/user/forgot-password");
	console.log("here", JSON.parse(xhr.response), username, email);
	xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
    	    console.log("trying to reset");
    	} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
            alert("Invalid username or Email");
        }
    };

	xhr.send(JSON.stringify({username, email}));
}