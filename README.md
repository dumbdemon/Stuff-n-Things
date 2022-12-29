[![CodeFactor](https://www.codefactor.io/repository/github/dumbdemon/stuff-n-things/badge)](https://www.codefactor.io/repository/github/dumbdemon/stuff-n-things)

# Stuff'n'Things Source Code

The source code for my Discord™ bot Stuff'n'Things. Mostly an entertainment bot.

## Commands

Command info moved to [GitHub Wiki](https://github.com/dumbdemon/Stuff-n-Things/wiki).

## Things to Note

* There is also a `/test` command that would be located in
  the [dev commands folder](https://github.com/dumbdemon/Stuff-n-Things/tree/master/src/main/java/com/terransky/stuffnthings/interactions/commands/slashCommands/devs)
  is not included as it would contain hardcoded IDs that I do not wish to expose. If you would like the command for
  yourself, please create one
  implementing [ICommandSlash](https://github.com/dumbdemon/Stuff-n-Things/blob/master/src/main/java/com/terransky/stuffnthings/interfaces/interactions/ICommandSlash.java)
  or delete/comment out the `new test(),` line in
  the [ManagersManager](https://github.com/dumbdemon/Stuff-n-Things/blob/master/src/main/java/com/terransky/stuffnthings/ManagersManager.java)
  class.
* The `/rob-fail-chance` was built for the Discord™ bot [UnbelievaBoat](https://unbelievaboat.com/)'s rob command.
* Database uses [SQLite](https://www.sqlite.org/index.html) [WIP].
* The way I'm currently implementing `/kill random`'s source data is bad and the database table for it has not been set
  up yet.
* `secretsAndLies()` class referenced in the main
  class [[StuffNThings](https://github.com/dumbdemon/Stuff-n-Things/blob/e3659163b3e1cb0dbf95680e325d2a2fecc2e886/src/main/java/com/terransky/stuffnthings/StuffNThings.java#L33)]
  and listener
  class [[ListeningForEvents](https://github.com/dumbdemon/Stuff-n-Things/blob/e3659163b3e1cb0dbf95680e325d2a2fecc2e886/src/main/java/com/terransky/stuffnthings/listeners/ListeningForEvents.java#L65)]
  contains real names and has been removed to avoid doxxing.
