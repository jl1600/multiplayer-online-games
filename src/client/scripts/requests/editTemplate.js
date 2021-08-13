if (typeof xhr === "undefined") xhr = new XMLHttpRequest();
const templateID = window.location.href.split("?templateId=")[1];

getTemplateAttributes();

function getTemplateAttributes() {
    xhr.open("GET", "http://localhost:8000/template/all-attributes?templateid=" + templateID);

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            promptAttributes(JSON.parse(xhr.response).attrMap);
        }
    }

    xhr.send();
}

function editTemplate() {
	xhr.open("POST", "http://localhost:8000/template/edit");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 204) {
		    alert("Successfully edited template");
            window.location = "http://localhost:8080/pages/templates.html";
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("There's an invalid attribute or value");
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 404) {
            alert("The templateID doesn't exist");
		}
	}

    const attrMap = getAttrMap();

	xhr.send(JSON.stringify({
		templateID,
		attrMap
	}));
}

function promptAttributes(map) {
    document.getElementsByTagName("input")[0].value = map.Title;
    delete map.Title;

    for (const [key, value] of Object.entries(map)) {
        addOption(key, value);
    }

    restrictOptions();
    document.getElementById("is-multiple-choice").onclick = restrictOptions;
}

function addOption(label, checked) {
    const labelEl = document.createElement("label");
    labelEl.innerHTML = label;
    const input = document.createElement("input");
    input.type = "checkbox";
    input.setAttribute("id", label.toLowerCase().replace(/\s/g, "-"));
    input.checked = checked === "true";

    labelEl.prepend(input);

    document.getElementById("form").insertBefore(labelEl, document.getElementsByTagName("button")[0]);
}

function restrictOptions() {
    if (!document.getElementById("is-multiple-choice").checked) {
        document.querySelectorAll("input[type='checkbox']:not(#is-multiple-choice)").forEach(el => {
            el.checked = false;
            el.disabled = true;
        });
    } else {
        document.querySelectorAll("input[type='checkbox']:not(#is-multiple-choice)").forEach(el => el.disabled = false);
    }
}

function getAttrMap() {
    const inputs = Array.from(document.getElementsByTagName("input"));

    let attrMap = {
        Title: inputs.shift().value
    };

    for (const el of inputs) {
        attrMap[capitalize(el.getAttribute("id").replace(/-/g, " "))] = el.checked.toString();
    }

    return attrMap;
}

function capitalize(text) {
    return text[0].toUpperCase() + text.substring(1).toLowerCase();
}
