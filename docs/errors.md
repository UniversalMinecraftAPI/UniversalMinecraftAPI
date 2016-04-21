There are multiple different error codes. The error code can be found in an error response in the `code` JSON field:
```
[
  {
    "code": 5,
    "message": "Invalid Authorization header"
  }
]
```

| Code  | Name                              | Description | 
| ----- | -----------------------------     | ----------- |
| 1     | Authentication error              | Returned when an unknown authentication error occured, should usually not happen. |
| 2     | Method invocation exception       | Returned when a method produced an error, the message will contain more information |
| 3     | Parse error                       | Returned when the expression supplied cannot be parsed. |
| 4     | Invalid content type              | Returned when the content type isn't `application/json`. |
| 5     | Invalid authorization header      | Returned when the `Authorization` header isn't valid, such as it not containing 2 parts or it doesn't start with `Basic`. |
| 6     | Invalid credentials               | Returned when the credentials are invalid, usually when the username or password doesn't match an existing one. |
| 7     | Access denied                     | Returned when the authenticated user doesn't have access to this method or stream. |
| 8     | JSON invalid                      | Returned when the JSON supplied cannot be parsed. |
| 9     | Not found                         | Returned when the page requested cannot be found. |
| 12    | Invalid stream usage              | Returned when you try to (un)subscribe to/from a stream while not connected via a web socket. |
| 13    | Invalid stream                    | Returned when the specified stream cannot be found. |
| 14    | Duplicate stream subscription     | Returned when the web socket session has already subscribed to this stream. |