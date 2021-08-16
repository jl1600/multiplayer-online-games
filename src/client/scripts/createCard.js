function createCard(data, type) {
	const card = createElement("card");
	card.setAttribute("data-id", data.templateID || data.gameID || data.matchId || data.id);

	const content = createContent(data);
	const overlay = createOverlay(type, data.accessLevel);

	card.appendChild(content);
	card.appendChild(overlay);

	document.getElementById(data.accessLevel === "DELETED" ? "deleted-cards" : "cards-container").appendChild(card);
}

function createImg(type, extension = "jpg") {
	const img = document.createElement("img");
	img.setAttribute("src", `../../static/${ type.toLowerCase() }.${ extension }`);
	return img;
}
function createText(text, elementClass) {
	const el = createElement(elementClass);
	el.innerHTML = text;
	return el;
}
function createContent(data) {
	const content = createElement("content");

	const img = createImg(data.gameGenre || data.genre);
	const title = createText(data.title || data.gameTitle, "title");
	const description = createText(data.maxPlayers ? `Room size: ${ data.numPlayers }/${ data.maxPlayers }`: "", "description");

	content.appendChild(img);
	content.appendChild(title);
	content.appendChild(description);
	return content;
}
function createOverlay(type, publicity) {
	const el = createElement("overlay");
	const imgs = createOverlayButtons(type, publicity);

	el.appendChild(imgs);
	return el;
}
function createElement(className) {
	const el = document.createElement("div");
	el.classList.add(className);
	return el;
}
function createOverlayButtons(type, publicity) {
	const imgs = createElement("img-container");

	if (type === "EDIT" && !!publicity) {
		createEditButtons(publicity).forEach(el => imgs.appendChild(el));
	} else {
		const btn = createElement("button");
		btn.innerHTML = type.toUpperCase();

		imgs.appendChild(btn);
	}

	return imgs;
}
function createEditButtons(publicity) {

    const visibilityImg = createImg(publicity.toLowerCase(), "png");
	visibilityImg.classList.add("button");
	visibilityImg.classList.add("publicity");

    if (publicity === "DELETED") return [visibilityImg];
	const deleteImg = createImg("trash", "png");
	deleteImg.classList.add("button");
	deleteImg.classList.add("delete");

	return [visibilityImg, deleteImg];
}
