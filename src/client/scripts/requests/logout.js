window.addEventListener("beforeunload", logout);

function logout() {
    xhr.open("POST", "http://localhost:8000/logout");
    xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onreadystatechange = () => {
        if (xhr.readyState == XMLHttpRequest.DONE && xhr.status == 200) {
            sessionStorage.setItem("userId", null);
            sessionStorage.setItem("userType", null);
            window.location ="http://localho.st:8080/pages/matches";
            document.getElementByTagName("header").contentWindow.updateHeader();
        }
    };

    xhr.send(JSON.stringify`{
        userId: sessionStorage.getItem("userId")
    }`);
}