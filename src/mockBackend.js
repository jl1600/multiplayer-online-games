const http = require("http");

http.createServer((request, response) => {
	response.setHeader("Access-Control-Allow-Origin", "http://localho.st:8080");
	response.setHeader("Access-Control-Allow-Headers", "Content-Type");
	if (request.method == "POST") {
		let body = "";
		request.on("data", data => body += data);
		request.on("end", () => {
			console.log("POST: " + body);
			response.writeHead(200, {"Content-Type": "application/json"});
			response.end(`{"userId": 1,
			"userType": "admin",
				"games": [
					{
						"type": "hangman",
						"title": "My first game",
						"description": "My very first game!",
						"id": 2,
						"public": false
					}
				]
			}`);
		});
	} else {
		console.log("GET");
		response.writeHead(200, {"Content-Type": "application/json"});
		response.end(`{
			"username": "MyUserName",
			"matches": [
				{
					"type": "hangman",
					"title": "A cool match",
					"description": "Try to guess the word before the man is hung",
					"id": 1
				},
				{
					"type": "hangman",
					"title": "A really cool match",
					"description": "Try to guess the word before the man is hung",
					"id": 2
				}
			],
			"games": [
				{
					"type": "hangman",
					"title": "A public game",
					"description": "My very first game!",
					"id": 2,
					"accessLevel": "PRIVATE"
				},
				{
					"type": "quiz",
					"title": "Another public game",
					"description": "A fun quiz",
					"id": 3,
					"accessLevel": "PUBLIC"
				},
				{
                    "type": "quiz",
                    "title": "Another public game",
                    "description": "A fun quiz",
                    "id": 4,
                    "accessLevel": "FRIENDS"
                },
                {
                    "type": "quiz",
                    "title": "An uncool game",
                    "description": "made by username",
                    "id": 4,
                    "accessLevel": "FRIENDS"
                }
			],
			"templates": [
				{
					"type": "multiple-choice",
					"title": "Multiple choice",
					"description": "description of a multiple choice",
					"id": 1,
					"genre": "quiz"
				}
			]
		}`);
	}
}).listen(8000);
