if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

getDefaultAttributes();

function getDefaultAttributes() {
    var genre = document.querySelector("input[name='templateGenre']:checked").value;
    xhr.open("GET", "http://localhost:8000/template/default-attr-map?genre=" + genre);

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            promptAttributes(JSON.parse(xhr.response).attrMap);
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("Invalid game genre.")
        }
    }

    xhr.send();
}

function onRadioButtonChange() {
    document.getElementById("checkboxes").innerHTML = '';
    getDefaultAttributes();
}

function createTemplate() {
	xhr.open("POST", "http://localhost:8000/template/create");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 201) {
		    alert("Successfully created template");
            window.location = "http://localhost:8080/pages/templates.html";
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("There's an invalid attribute or value");
		}
	}

    const attrMap = getAttrMap();

	xhr.send(JSON.stringify({
	    genre: document.querySelector("input[name='templateGenre']:checked").value,
		attrMap
	}));
}

function promptAttributes(map) {
    document.getElementsByTagName("input")[2].value = map.title;
    delete map.title;

    for (const [key, value] of Object.entries(map)) {
        addOption(key, value);
    }

    restrictOptions();
    document.getElementById("multipleChoice").onclick = restrictOptions;
}

function addOption(label, checked) {
    const labelEl = document.createElement("label");
    labelEl.innerHTML = label;
    const input = document.createElement("input");
    input.type = "checkbox";
    input.setAttribute("id", label);
    input.checked = checked === "true";

    labelEl.prepend(input);

    document.getElementById("checkboxes").appendChild(labelEl);
}

function restrictOptions() {
    if (!document.getElementById("multipleChoice").checked) {
        document.querySelectorAll("input[type='checkbox']:not(#multipleChoice)").forEach(el => {
            el.checked = false;
            el.disabled = true;
        });
    } else {
        document.querySelectorAll("input[type='checkbox']:not(#multipleChoice)").forEach(el => el.disabled = false);
    }
}

function getAttrMap() {
    var node = document.getElementById('checkboxes');
    const inputs = Array.from(node.getElementsByTagName("input"));

    let attrMap = {
       title: document.getElementsByTagName("input")[2].value
    };

    for (const el of inputs) {
        attrMap[el.getAttribute("id")] = el.checked.toString();
    }

    return attrMap;
}

function capitalize(text) {
    return text[0].toUpperCase() + text.substring(1).toLowerCase();
}