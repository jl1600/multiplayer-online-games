function createRow(id, userData, buttonText = null) {
	const row = document.createElement("div");
	row.classList.add("row");
	row.setAttribute("data-id", userData.userID);
	row.innerHTML = userData.username;

	if (buttonText) {
		const button = createTextButton(buttonText);
		row.appendChild(button);
	} else {
		createPendingButtons();
	}

	document.getElementById(id).appendChild(row);
}

function createTextButton(text) {
	const button = createButton("button");
	button.innerHTML = text;

	return button;
}

function createPendingButtons() {
	const wrapper = document.createElement("div");
	wrapper.classList.add("button-wrapper");

	const accept = createButton("accept-button", "accept");
	const decline = createButton("decline-button", "decline");

	wrapper.appendChild(accept);
	wrapper.appendChild(decline);

	return wrapper;
}

function createButton(className, img = null) {
	const button = document.createElement("button");
	button.classList.add(className);
	button.type = "button";

	if (img) {
		const imgEl = createImg(img);
		button.appendChild(imgEl);
	}

	return button;
}

function createImg(src) {
	const img = document.createElement("img");
	img.src = `../static/${ src }.png`;

	return img;
}
