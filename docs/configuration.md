UniversalMinecraftAPI has multiple configuration options. There are currently two configuration files `config` and 
`users`. The file extension and format depends on the platform. For Spigot this is `.yml` (YAML format) and for Sponge 
this is `.conf` (HOCON format).

## Main configuration
The main configuration currently contains only one section: `web_server`. This section specifies all details of the
web server that will be used for UniversalMinecraftAPI.

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

More information about SSL in the web server UniversalMinecraftAPI uses, can be found
[here](http://www.eclipse.org/jetty/documentation/current/configuring-ssl.html).

### thread_pool
This section is only for advanced use and will normally not be used, only for, for example, resource-constrained servers.
The following options are available:

* `max_threads`: Specify the max number of threads to use for the web server.
* `min_threads`: Specify the min number of threads to use for the web server
* `idle_timeout`: Specify the timeout of a thread

**Important**: This section will only work if `max_threads` is also configured to a value higher than 1.

## Users configuration
The second file called `users` specifies the users and groups.

### Permission model
The permission model consists of two parts: users and groups. They are related as follows:

1. A user can be a member of multiple groups.
2. A group has a default permission level, can inherit from multiple groups and has permissions for itself.

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
The `groups` section is a bit more difficult to understand than `users`. Again, how the name is specified depends on 
the format. The following options are present in all implementations:

* `default-permission`: The default permission for this group. See the description of the permission model below
for more information about this value.
* `inherits-from`: A list of the groups this group inherits from. Again, see the description of the permission model 
below for more information about this value.
* `permissions`: Contains a map of permission paths to their values.  It can also contain a nested map. In that case, 
the key `default` specifies the permission for the parent value. So, for example
the following group sections are valid:

Spigot (YAML):
```
groups:
    default:
        permissions:
            players:
                default: 1
                get: -1
            uma: 1
```

Sponge (HOCON):
```
groups = [
    {
        name = "default"
        permissions {
            players {
                default = 1
                get = -1
            }
            uma = 1
        }
    }
]
```

Both would resolve to the following values for the permission paths:

| Path | Value |
| ---- | ----- |
| `players` | `1`|
| `players.get` | `1+(-1) = 0` |
| `uma` | `1` |

For a more extensive example, see below.

## The permission model
This section will explain how permissions are checked.

The system works based on integer values which specify whether a permission path gets an allow or a deny. If this value
is larger than 0 (not 0 itself), the permission is allowed, otherwise it is denied. This value is calculated by
adding all values on all the levels that this permission is specified.

First of all, the value specified in the configuration file is used. So, if the permission `players.get` is checked,
it first checks for the value of an empty string. This value is determined by the `default-permission` value
of the group. For more information about this value, see below. Then, the value of `players` is added to the
previously determined value. Lastly, the value of `players.get` is added to this value. Then, if the value is larger
than 0, the action is allowed, if not it is denied.

The `default-permission` value of the group is not only determined by the `default-permission` of the group, but is also
dependent on the `default-permission` of all groups it inherits from. So, if `group1` has a `default-permission` of 1
and `group2` has a default-permission of `-2` and `group3` inherits from `group1` and `group2` and doesn't specify
a `default-permission`, the `default-permission` wil be `1+(-2) = -1`.

The value of a permission path is also dependent on the groups it inherits from. If `group1` specifies `players` as `-1`
and `group2` inherits from `group1` and specifies `players.get` as `1`, the final value will be `-1+1 = 0` (and thus the action
would not be allowed).

So, to give a user access to everything, specify the `default-permission` to a high value. If there is no inheritance
involved, the value could even be `1`.

## Example files

### Main configuration file

#### Spigot `config.yml`
```
web_server:
  ip_address: null # the IP address to bind to, null to bind to all interfaces
  port: 20059 # the port to bind to, -1 to choose automatically
  secure:
    enabled: false # make the web server SSL secured
    keystore:
      file: "keystore"
      password: "password"
    truststore:
      file: "truststore"
      password: "password"
  thread_pool:
    max_threads: -1 # specify the max number of threads to use for the web server
    min_threads: -1 # specify the min number of threads to use for the web server, only available if max_threads is also set to a number higher than 0
    idle_timeout: -1 # specify the timeout of a thread, only available if max_threads is also set to a number higher than 0
```

#### Sponge `config.conf`
```
web_server {
    # the IP address to bind to, null to bind to all interfaces
    ip_address=null
    # the port to bind to, -1 to choose automatically
    port=20059
    secure {
        # make the web server SSL secured
        enabled=false
        keystore {
            file="keystore"
            password="password"
        }
        truststore {
            file="truststore"
            password="password"
        }
    }
    thread_pool {
        # specify the max number of threads to use for the web server
        max_threads=-1
        # specify the min number of threads to use for the web server, only available if max_threads is also set to a number higher than 0
        min_threads=-1
        # specify the timeout of a thread, only available if max_threads is also set to a number higher than 0
        idle_timeout=-1
    }
}
```

### Users configuration

#### Spigot `users.yml`
```
users:
  admin:
    password: changeme
    password_type: plain
    groups:
      - admin
  default:
    password: default
    password_type: plain
    groups:
      - default

groups:
  default:
    inherits-from: [streams]
    permissions:
      players:
        default: 10
        get: 1
      uma: 1
  streams:
    permissions:
      streams:
        subscribe: 1
        unsubscribe: -1
        chat: 1
        console: -1
  admin:
    inherits-from: [streams]
    default-permission: 1
```

#### Sponge `users.conf`
```
users = [
    {
        username = "default"
        password = "default"
        password_type = "plain"
        groups = [
            "default"
        ]
    }
    {
        username = "admin"
        password = "changeme"
        password_type = "plain"
        groups = [
            "admin"
        ]
    }
]

groups = [
    {
        name = "default"
        inherits-from = [streams]
        permissions {
            players {
                default = 10
                get = 1
            }
            uma = 1
        }
    },
    {
        name = "streams"
        permissions {
            streams {
                subscribe = 1
                unsubscribe = -1
                chat = 1
                console = -1
            }
        }
    },
    {
        name = "admin"
        inherits-from = [streams]
        default-permission = 1
    }
]
```

#### Resolved values
Both of these files are equivalent and resolve to the same permission values:

| User      | Path                      | Value         | Result        |
| --------- | ------------------------- | ------------- | ------------- |
| `default` | `players.get`             | `11`          | Allow         |
| `default` | `players.chat`            | `10`          | Allow         |
| `default` | `uma.platform`            | `1`           | Allow         |
| `default` | `streams.subscribe`       | `1`           | Allow         |
| `default` | `streams.unsubscribe`     | `-1`          | Deny          |
| `default` | `streams.chat`            | `1`           | Allow         |
| `default` | `streams.console`         | `-1`          | Deny          |
| `default` | `server.broadcast`        | `0`           | Deny          |
| `admin`   | `players.get`             | `1`           | Allow         |
| `admin`   | `server.broadcast`        | `1`           | Allow         |
| `admin`   | `streams.subscribe`       | `2`           | Allow         |
| `admin`   | `streams.unsubscribe`     | `0`           | Deny          |
| `admin`   | `streams.chat`            | `2`           | Allow         |
| `admin`   | `streams.console`         | `0`           | Deny          |