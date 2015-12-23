# Chest Plugin Minecraft
### by Nattack

A Minecraft plugin in order to generate chest at regular time interval

##Commands

###/chest start
This is a command in order to start the generation of the random chest.

```yml
chest start:
  description: This is a command in order to start the generation of the random chest.
  usage: /chest start
  default: op
```

###/chest stop
This is a command in order to stop the generation of the random chest.

```yml
chest stop:
  description: This is a command in order to stop the generation of the random chest.
  usage: /chest stop
  default: op
```

###/chest create
To create instantly a chest.

```yml
chest create:
  description: To create instantly a chest.
  usage: /chest create
  default: op
```

###/chest time <time>
Set the time between each chest generation (in second)

```yml
chest time:
  description: Set the time between each chest generation (in second)
  usage: /chest time <time>
  default: op
```

###/chest size <min> <max>
Set the area size (min and max for square)

```yml
chest size:
  description: Set the area size (min and max for square)
  usage: /chest size <min> <max>
  default: op
```
