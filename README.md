[![CodeFactor](https://www.codefactor.io/repository/github/dumbdemon/stuff-n-things/badge)](https://www.codefactor.io/repository/github/dumbdemon/stuff-n-things)
[![DeepSource](https://deepsource.io/gh/dumbdemon/Stuff-n-Things.svg/?label=active+issues&show_trend=true&token=p8geN842seYBf9QPzbISgyan)](https://deepsource.io/gh/dumbdemon/Stuff-n-Things/?ref=repository-badge)

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
* Database uses [MongoDB](https://www.mongodb.com/).
