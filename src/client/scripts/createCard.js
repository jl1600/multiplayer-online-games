function createCard(data, type) {
	const card = createElement("card");
	card.setAttribute("data-id", data.id);

	const content = createContent(data);
	const overlay = createOverlay(type);

	card.appendChild(content);
	card.appendChild(overlay);
	document.getElementById("cards-container").appendChild(card);
}

function createImg(type, extension = "jpg") {
	const img = document.createElement("img");
	img.setAttribute("src", `../../static/${ type }.${ extension }`);
	return img;
}
function createText(text, elementClass) {
	const el = createElement(elementClass);
	el.innerHTML = text;
	return el;
}
function createContent(data) {
    const content = createElement("content");

	const img = createImg(data.type);
	const title = createText(data.title, "title");
	const description = createText(data.description, "description");

	content.appendChild(img);
	content.appendChild(title);
	content.appendChild(description);
	return content;
}
function createOverlay(type) {
	const el = createElement("overlay");
	const imgs = createOverlayButtons(type);

	el.appendChild(imgs);
	return el;
}
function createElement(className) {
    const el = document.createElement("div");
    el.classList.add(className);
    return el;
}
function createOverlayButtons(type) {
    const imgs = createElement("img-container");

    if (type === "EDIT") {
	    const [editImg, deleteImg] = createEditButtons();

	    imgs.appendChild(editImg);
        imgs.appendChild(deleteImg);
	} else {
	    const btn = createElement("button");
	    btn.innerHTML = type.toUpperCase();

	    imgs.appendChild(btn);
	}

	return imgs;
}
function createEditButtons() {
    const editImg = createImg("edit", "png");
    editImg.classList.add("button");
    editImg.classList.add("edit");

    const deleteImg = createImg("trash", "png");
    deleteImg.classList.add("button");
    deleteImg.classList.add("delete");

    return [editImg, deleteImg];
}