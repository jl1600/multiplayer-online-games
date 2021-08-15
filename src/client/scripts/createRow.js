function createRow(id, userData, buttonText = null) {
	const row = document.createElement("div");
	row.classList.add("row");
	row.setAttribute("data-id", userData.userID);
	row.innerHTML = userData.username;

	if (buttonText) {
		const button = createTextButton(buttonText);
		row.appendChild(button);
	} else {
		const buttons = createPendingButtons();
	    row.appendChild(buttons);
	}

	document.getElementById(id).appendChild(row);
}

function createTextButton(text) {
	const button = createButton("remove-button", text);

	return buttonWrapper(button);
}

function createPendingButtons() {
	const accept = createButton("accept-button", "Accept");
	const decline = createButton("decline-button", "Decline");

    const wrapper = buttonWrapper(accept);
	wrapper.appendChild(decline);

	return wrapper;
}

function createButton(className, buttonText) {
	const button = document.createElement("button");
	button.classList.add(className);
	button.classList.add("button");
	button.type = "button";
	button.innerHTML = buttonText;

	return button;
}

function buttonWrapper(buttons) {
    const wrapper = document.createElement("div");
    wrapper.classList.add("button-wrapper");

    wrapper.appendChild(buttons);

    return wrapper;
}
