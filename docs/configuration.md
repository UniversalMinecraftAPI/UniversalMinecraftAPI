JSONAPI has multiple configuration options. There are currently two configuration files `config` and `users`. The file
extension and format depends on the platform. For Spigot this is `.yml` (YAML format) and for Sponge this is `.conf`
(HOCON format).

## Main configuration
The main configuration currently contains only one section: `web_server`. This section specifies all details of the
web server that will be used for JSONAPI.

### ip_address
**Default value**: `null`

This option can be specified to bind the server to a specific host, but can also be left at its default value `null` to
bind to all addresses.

### port
**Default value**: `20059`

This option determines the port that will be used by the web server. If it is set to `-1`, the port will be chosen
automatically.

### secure
This section specifies the HTTPS/SSL options for the web server, which is recommended for secure use. The following
options are available:

#### enabled
**Default value**: `false`

Specifies whether HTTPS/SSL is enabled or not.

#### keystore/truststore
This section contains two options:

1. `file`: Specifies the file for the keystore/truststore
2. `password`: Specifies the password for the keystore/truststore

More information about SSL in the web server JSONAPI uses, can be found
[here](http://www.eclipse.org/jetty/documentation/current/configuring-ssl.html).

### thread_pool
This section is only for advanced use and will normally not be used, only for, for example, resource-constrained servers.
The following options are available:

* `max_threads`: Specify the max number of threads to use for the web server.
* `min_threads`: Specify the min number of threads to use for the web server
* `idle_timeout`: Specify the timeout of a thread

**Important**: This section will only work if `max_threads` is also configured to a value higher than 1.

## Users configuration
The second file called `users` specifies the users, groups and permissions used.

### Permission model
The permission model consists of three parts: users, groups and permissions. They are related as follows:

1. A user can be a member of multiple groups
2. A group can contain multiple permissions
3. A permission can contain multiple permission white/blacklists.

### users
The `users` section is easy to understand. It contains a list of users, of which the format can be seen by
use of the default users file. It depends on the format where the username is specified. The following options are
however present in all implementations:

* `password`: The password of the user. The exact representation depends on `password_type`.
* `password_type`: This is the password type of the user which specifies how the password is checked. Currently, the
only password type implemented is `plain`, which specifies that the passwords are checked against each other as is. If
another password type was specified such as `sha1` and this type was implemented, the password supplied by the API
consumer would be SHA1-encoded and checked against the value stored in `password`. Thus, the password stored in
`password` is the encoded password.
* `groups`: Contains a simple list of group names which must be specified in the `groups` section.

### groups
The `groups` section is even easier to understand than `users` as it contains only one or two values, depending
on the implementation. Again, how the name is specified depends on the format. There is one option which is present in
all implementations:

* `permissions`: Contains a simple list of permission names which must be specified in the `permissions` section. There
is one special permission which is `ALLOW_ALL`. This makes it possible for an admin to gain full control without
needing to specify every single namespace/class/stream. However, make sure to read the section below for information
about the permission system as this is **not** an overriding permission.

### permissions
The `permissions` section is the most difficult one to understand. Again, the name is specified in different ways
depending on the implementation, so we will focus on all the other values.

This section can best be explained by the use of an example, so we will use the following (YAML) configuration as an
example:
```yaml
permissions:
  default:
    namespaces:
      players:
        type: whitelist
        methods:
          - getPlayer
      streams:
        type: blacklist
        methods:
          - subscriptionCount
    classes:
      Player:
        type: whitelist
        methods:
          - getUUID
    streams:
      type: blacklist
      streams:
        - console
```

The permissions section first contains the name of this permission, which differs per implementation. Here it is
specified as the key of the section. Then, the section contains three keys. We will discuss each of the keys.

#### namespaces
The `namespaces` section contains all namespaced method permissions. Namespaced methods are the methods that can be
called by the use of `<namespace>.<method>`. This in contrast with class method which can only be called on intermediate
objects, which are normally obtained by the use of a namespaced method.

There are 2 namespaces in the example. The first one is the [`players`](namespaces/players.html) namespace which
contains all player related methods. The type of the permissions is `whitelist`, which means that **only** the method
listed here are allowed to be called. So, in this case only the method
[`players.getPlayer`](namespaces/players.html#getPlayer) can be called by a user which is in a group that has this
permission.

The second namespace listed is the [`streams`](namespaces/streams.html) namespace which has a type of `blacklist`. In
this case, all methods in this namespace can be called, **except** for the methods listed here. So, in this case only
the method [`streams.subscriptionCount`](namespaces/streams.html#subscriptionCount) cannot be called, all the other
methods in [`streams`](namespaces/streams.html) can be called.

#### classes
The `classes` section contains all class method permissions. For an explanation of class methods, please see the
namespaces section. The example above only contains the class [`Player`](classes/Player.html) which has a type of
`whitelist`. So, in this case only the method [`getUUID`](classes/Player.html#getUUID) can be called on the player.
For example, the following expression is valid:
```
players.getPlayer('Koen').getUUID()
```

However, the following is not:
```
players.getPlayer('Koen').getUsername()
```

#### streams
The last section is the streams section, which defines which streams the user is allowed to subscribe to. The white- and
blacklist work the same as before, so in this case the user can access all streams except for the
[`console`](streams/console.html) stream.

It needs to be noted that adding a stream to the whitelist or adding a blacklist without any methods is not enough for
the user to be able to subscribe to a stream. For that, the user will also need access to the
[`streams`](namespaces/streams.html) namespace and its [`subscribe`](namespaces/streams.html#subscribe) and
[`unsubscribe`](namespaces/streams.html#unsubscribe) methods.

## The permission model
This section will explain how permissions are checked and is for advanced users. However, if you are having problems
with permissions not working as intended, this section can help you troubleshoot the issues.

As explained before, there are three layers involved: users, groups and permissions. The system checks whether a method
or stream is allowed to be accessed on the user level. The user then asks the groups whether they have permission to
execute this method or subscribe to this stream who in their turn ask the permissions the same question. The permissions
respond by the means of a few possible values:

* `NEUTRAL`
* `ALLOWED`
* `DENIED`
* `OVERRIDE_ALLOWED`
* `OVERRIDE_DENIED`

These responses are accumulated in the group and the group will return a list of these responses to the user, which in
its turn will accumulate the responses of the groups. The user will then check the responses and determine whether the
user has this permission. It does does by an unanimous voting method: all responses must be ALLOWED for the user to be
granted this permission. There are a few rules while doing this:

1. All NEUTRAL responses are removed from the responses list and have no influence on the voting process.
2. If the list is empty now, the user doesn't have permission. In other words, no permissions defined for this
particular namespace/class/stream equals no permission.
3. If the list contains `OVERRIDE_ALLOWED` the user does have permission.
4. If the list contains `OVERRIDE_DENIED` the user doesn't have permission.
5. If all responses match `ALLOWED` the user does have permission. In other words, if there is only one permission
denying access (i.e. one `DENIED` response) while an infinite number of permissions is allowing access, the access will
still be denied.

Even the special permission `ALLOW_ALL` doesn't return the `OVERRIDE_ALLOWED` response, instead it functions as if the
permission was normally allowed. So, if you want to be sure a user has access to all methods, only specify the 
`ALLOW_ALL` permission on that user and don't add any additional permissions. They might have a whitelist permission 
type which means that if the method is not listed in that whitelist, you will be denied access, even while having
`ALLOW_ALl` permission. This does mean that you can give a user access to almost everything while still denying a few
things without needing to do the inverse of specifying everything.