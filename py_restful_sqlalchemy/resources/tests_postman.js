var jsonData = JSON.parse(responseBody);
tests["Access token was not empty"] = jsonData.access_token !== "';

postman.setEnvironmentVariable("jwt_token", jsonData.access_token);