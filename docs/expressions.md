UniversalMinecraftAPI has its own expression language so it can be easily used by everyone. This document will document
the syntax.

## Expressions
The language is based on expressions, of which there are multiple types. All will be explained here. But first, there
are a few general rules:
1. Whitespace is usually not taken into account
2. Expressions can't be too long as that will result in a parse error as a protection for a DOS

## Chained method call
The root expression of an expression supplied to UniversalMinecraftAPI is assumed to be a chained method call. The
chained method call can start with one of the following:

* Namespace expression
* Method expression

However, as of the time of writing, UMA doesn't have any methods without namespace, so all valid expressions start with
a namespace expression. This is then followed by a full stop (`.`), which is consequently followed by 1 or more method
expressions, all separated by a full stop (`.`). All of the following are valid expressions:
```
getIt()
test.getIt()
test.getIt().getThat()
test.getIt().getThat("test")
test.getIt("that").getValue("test")
```

These are not:
```
getIt().test
getIt().test.getThat()
test.getIt().test.getThat()
test.getIt().getThat().test
```

## Method call
A method call is a call to one of the methods. The method call does not include the namespace, which is only included
in the chained method call.

A method call consists of the following:
* A method name consisting of the characters `a-zA-Z0-9_`
* An opening parenthesis (`(`)
* 0 or more arguments separated by commas (`,`)
* A closing parenthesis (`)`)

Every argument must be a valid expression, but there are no bounds on what types of expressions are allowed.

Valid method calls:
```
getName("test")
getIt()
getTest(12, {"key"="value"})
_getMe()
```

Invalid method calls:
```
$test()
test("name"
```

## Namespace
A namespace is defined as follows: A character in the range `a-zA-Z` followed by 0 or more characters in the range 
`a-zA-Z0-9`. Valid namespaces:
```
test
test1234
t23a23
```

Invalid namespaces (but may be valid other expressions):
```
12test
test.test
test.2234
```

## Integer
An integer is defined as follows: An optional minus sign to denote a negative number followed by 1 or more characters
in the range `0-9`. Valid integers:
```
12
-12
1234
```

Invalid integers (but may be other valid expressions):
```
12.67
-a.23
b.23
```

## Double
A double consists of the following: 
* An optional minus sign to denote a negative number 
* 1 or more characters in the range `0-9`
* A full stop (`.`)
* 1 or more characters in the range `0-9`

Valid doubles:
```
12.67
2.34
3.14159265359
```

Invalid doubles (but may be other valid expressions):
```
12
12.a23
a23.123
```

## String
A string consists of the following:
* An opening quote (`"` or `'`)
* 0 or more characters which are not one of the following, unless preceded by a backslash (`\`): 
    * The quote used as opening quote
    * A backslash (`\`)
* A closing quote matching the opening quote

Valid strings:
```
"my name"
"my name\}"
"my name\""
'my name\''
```

Invalid strings:
```
'my name\'
'my\ name'
'my name"
'my name
'my name''
my name'
```

## Boolean
A boolean is either `true` or `false` (case sensitive). Valid booleans:
```
true
false
```

Invalid booleans:
```
TRUE
False
no
YES
```

## Map
A map is a mapping of keys to value expressions. It is defined as follows:
* An opening brace (`{`)
* 0 or more times a pair which consists of a key, which must be valid expression, followed by an equals sign (`=`), 
followed by the value, which must also be a valid expression. Pairs must be separated by a comma (`,`).
* A closing brace (`}`)

Valid maps:
```
{'key'='value'}
{'key'=12}
{getIt()=getThat()}
```

Invalid maps:
```
{'key'}
{'key'='value'
'key'='value'
```