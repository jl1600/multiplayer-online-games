const http = require("http");

http.createServer((request, response) => {
	response.setHeader("Access-Control-Allow-Origin", "http://localhost:8080");
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
		response.end(`[
		    {
		        "username": "charlie",
		        "id": 1
		    },
		    {
                "username": "sm",
                "id": 2
            },
            {
                "username": "ad",
                "id": 3
            },
            {
                "username": "ching",
                "id": 4
            }
		]`);
	}
}).listen(8000);
