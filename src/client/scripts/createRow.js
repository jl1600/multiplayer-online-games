function createRow(id, userData, buttonTexts) {
	const row = document.createElement("div");
	row.classList.add("row");
	row.setAttribute("data-id", userData.userID);
	row.innerHTML = userData.username;

    const buttons = createButtons(buttonTexts);
    row.appendChild(buttons);

	document.getElementById(id).appendChild(row);
}

function createButtons(texts) {
    const wrapper = document.createElement("div");
    wrapper.classList.add("button-wrapper");

    texts.forEach(text => {
        const button = createButton(text);
        wrapper.appendChild(button);
    });

	return wrapper;
}

function createButton(text) {
	const button = document.createElement("button");
	button.classList.add(`${ text.toLowerCase().replace(/\s/g, "-") }-button`);
	button.classList.add("button");
	button.type = "button";
	button.innerHTML = text;

	return button;
}
