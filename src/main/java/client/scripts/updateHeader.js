updateHeader();

function updateHeader() {
	switch (sessionStorage.getItem("userType")) {
		case "TRIAL":
			showTrialHeader();
		    break;
		case "ADMIN":
		    showAdminHeader();
		    break;
		case "MEMBER":
			showMemberHeader();
		    break;
		case "TEMP":
        	showMemberHeader();
            break;
	}
}

function showTrialHeader() {
    document.getElementsByClassName("trial")[0].hidden = false;
    document.getElementsByClassName("trial")[1].hidden = false;
    document.getElementsByClassName("member")[0].hidden = true;
    document.getElementsByClassName("admin")[0].hidden = true;
    document.getElementsByClassName("admin")[1].hidden = true;
    document.getElementsByClassName("admin")[2].hidden = true;
    document.getElementsByClassName("user")[0].hidden = false;
    document.getElementById("my-account").hidden = true;
}
function showAdminHeader() {
    document.getElementsByClassName("trial")[0].hidden = true;
    document.getElementsByClassName("trial")[1].hidden = true;
    document.getElementsByClassName("member")[0].hidden = true;
    document.getElementsByClassName("admin")[0].hidden = false;
    document.getElementsByClassName("admin")[1].hidden = false;
    document.getElementsByClassName("admin")[2].hidden = false;
    document.getElementsByClassName("user")[0].hidden = true;
    document.getElementById("my-account").hidden = false;
}
function showMemberHeader() {
    document.getElementsByClassName("trial")[0].hidden = false;
    document.getElementsByClassName("trial")[1].hidden = false;
    document.getElementsByClassName("member")[0].hidden = false;
    document.getElementsByClassName("admin")[0].hidden = true;
    document.getElementsByClassName("admin")[1].hidden = true;
    document.getElementsByClassName("admin")[2].hidden = true;
    document.getElementsByClassName("user")[0].hidden = true;
    document.getElementById("my-account").hidden = false;
}
