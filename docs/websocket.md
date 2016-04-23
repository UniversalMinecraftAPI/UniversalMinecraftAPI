## Usage
The WebSocket can be accessed on the HTTP port by using the URL `/api/v1/websocket`. The WebSocket can then be used 
just like the HTTP API with the addition of a few API methods that only work on WebSockets, 
such as [streams.subscribe](namespaces/streams.html#subscribe) and [streams.unsubscribe](namespaces/streams.html#unsubscribe).

The connection will be automatically closed if the timeout of 300 seconds (5 minutes) is reached. Therefore, make sure
to send a message at least once every 5 minutes. For that, you can use the method [uma.ping](namespaces/uma.html#ping)
which will always return `pong`.

## Authentication
You can authenticate the same way as over HTTP by using the HTTP `Authorization` header. However, if you are unable to
do so, such as in the browser, there is another possibility which requires you to first acquire an API key using HTTP. This can be done by 
calling [users.generateApiKey](namespaces/users.html#generateApiKey), such as the following:
```
POST /api/v1/call HTTP/1.1
Host: localhost:20059
Content-Type: application/json
Authorization: Basic YWRtaW46Y2hhbmdlbWU=

{
    "expression": "users.generateApiKey()"
}
```

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
    "tag": ""
  }
]
```

This API key can then be used as a query parameter to the web socket connection, which could turn the URL into
`/api/v1/websocket?key=61412a5c740e72dae9e43890bcfa90ca`. This key is only valid for 1 minute so it should be
generated before every (re)connection.

### Streams
The main advantage of the web socket connection method is the availability of streams which continuously push data
to the client instead of the client needing to poll the server. There are different streams available, such as 
[chat](streams/chat.html) and [console](streams/console.html). For a complete list, please see the [homepage](index.html).

To subscribe to a stream, call [streams.subscribe](namespaces/streams.html#subscribe):
```
{     
    "expression": "streams.subscribe('chat')" 
}
```

```
[
    {
        "result": true,
        "success": true,
        "tag": ""
    }
]
```

In this example, whenever a chat message is sent by a player, you will receive a [ChatMessage](classes/ChatMessage.html):
```
{
    "result": {
        "message": " Hi! Welcome to UniversalMinecraftAPI!",
        "player": "Koen"
    },
    "stream": "chat",
    "success": true,
    "tag": ""
}
```

It is also possible to unsubscribe from a stream by using [streams.unsubscribe](namespaces/streams.html#unsubscribe):
```
{     
    "expression": "streams.unsubscribe('chat')" 
}
```