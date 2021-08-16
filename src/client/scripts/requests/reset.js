if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

function reset(username, email) {
	if (!username || !email) return;
	if (!checkValidEmail) return;

	xhr.open("POST", "http://localhost:8080/user/forgot-password");
	xhr.send(JSON.stringify({username, email}));
}

function checkValidEmail(email){
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
}

function redirect(){
    console.log("redirecting to login.html")
    window.location = "http://localhost:8080/pages/login.html"
}