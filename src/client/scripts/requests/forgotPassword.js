if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

function forgotPass() {
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;

	xhr.open("POST", "http://localhost:8000/user/forgot-password");

	xhr.onreadystatechange = () => {        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
            alert("An \"email\" was sent to you, check your \"inbox\"");
    	} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 403) {
            alert("Invalid username or Email");
        }
    };

	xhr.send(JSON.stringify({username, email}));
}