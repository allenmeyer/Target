myRetail RESTful Service API

Setup Requirements:

1. Install mongoDB https://docs.mongodb.com/manual/installation/

2. Ensure you have gradle installed on your machine https://gradle.org/install/ (At least 7.0 for jdk16)

In order to run and interact:

1. From the workspace type
```
gradle bootRun
```

To check unit tests type
```
gradle test
```

2. From another command prompt

GET example:
```
>curl localhost:8080/products/12345 -v

*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /products/12345 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.55.1
> Accept: */*
>
< HTTP/1.1 404
< Content-Length: 0
< Date: Thu, 22 Jul 2021 01:44:39 GMT
<
* Connection #0 to host localhost left intact
```

PUT example:
```
>curl -H "Content-Type: application/json" -X PUT -d "{\"id\": \"12345\", \"name\": \"towel\", \"current_price\": {\"value\": 20.0, \"currency_code\": \"USD\"}}" localhost:8080/products/12345

{"id":"12345","name":"towel","current_price":{"value":20.0,"currency_code":"USD"},"_id":{"timestamp":1626918456,"date":"2021-07-22T01:47:36.000+00:00"}}

>curl localhost:8080/products/12345
{"_id":{"timestamp":1626918456,"date":"2021-07-22T01:47:36.000+00:00"},"id":"12345","name":"towel","current_price":{"value":20.0,"currency_code":"USD"}}
```

3. In order to stop

Try one of:

Ctrl - C on the command prompt running the web service

running gradle -stop from another command prompt

