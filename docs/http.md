The default HTTP connection port is `20059`. This can be configured in the options.

## Usage
The API can be consumed by sending a `POST` request to the URL `/api/v1/call` with a JSON payload describing the request:
```
{
    "expression": "<expression>",
    "tag": "<tag>"
}
```

You can also supply a JSON array of such requests and the API will return a JSON array of responses.

### Authentication
It is easy to authenticate with the API by using HTTP Basic Authentication. So, if the username is `admin` and the
password is `changeme` (the defaults), the request would need the following header:
```
Authorization: Basic YWRtaW46Y2hhbmdlbWU=
```

This header is constructed as follows ([source](https://en.wikipedia.org/wiki/Basic_access_authentication#Client_side)):

1. The username and password are combined into a string separated by a colon, e.g.: username:password
2. The resulting string is encoded using the RFC2045-MIME variant of Base64, except not limited to 76 char/line.
3. The authorization method and a space i.e. "Basic " is then put before the encoded string.

Pseudocode for generation of this header:
```
username = "admin"
password = "changeme"

header = "Authorization: Basic " + base64(username + ":" + password)
```

If the credentials are not correct, you will get back the following response:
```
[
  {
    "code": 6,
    "message": "Invalid credentials"
  }
]
```

If you don't send an authorization header, by default the user called `default` will be used. If this user is not
present, you will get the following response:
```
[
  {
    "code": 6,
    "message": "No authentication found and no default user found"
  }
]
```

### Example request
```
POST /api/v1/call HTTP/1.1
Host: localhost:20059
Content-Type: application/json
Authorization: Basic YWRtaW46Y2hhbmdlbWU=

{
    "expression": "users.generateApiKey()",
    "tag": "newtag"
}
```

### Example response
```
HTTP/1.1 200 OK
Content-Type: application/json
Date: Wed, 20 Apr 2016 15:11:59 GMT
Access-Control-Allow-Credentials: true
Access-Control-Allow-Headers: Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin
Access-Control-Allow-Methods: GET,PUT,POST,DELETE,OPTIONS
Access-Control-Allow-Origin: *
Server: Jetty(9.3.z-SNAPSHOT)
Transfer-Encoding: chunked

[
  {
    "result": "61412a5c740e72dae9e43890bcfa90ca",
    "success": true,
    "tag": "newtag"
  }
]
```