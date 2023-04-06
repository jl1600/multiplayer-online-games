function promptQuestion(questionText, defaultValue = "") {
	const question = document.createElement("label");
	const input = document.createElement("input");
	input.value = defaultValue;
	input.required = true;
	input.onkeypress = event => {
		if (event.keyCode === 13) makeDesignChoice();
	}
	input.setAttribute("data-id", document.getElementsByTagName("tag").length);
	question.innerHTML = questionText;
	question.appendChild(input);

	document.getElementById("form").insertBefore(question, document.getElementsByClassName("buttons")[0]);
	question.focus();
}
