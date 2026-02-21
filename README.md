[![CodeFactor](https://www.codefactor.io/repository/github/dumbdemon/stuff-n-things/badge)](https://www.codefactor.io/repository/github/dumbdemon/stuff-n-things)
[![DeepSource](https://deepsource.io/gh/dumbdemon/Stuff-n-Things.svg/?label=active+issues&show_trend=true&token=p8geN842seYBf9QPzbISgyan)](https://deepsource.io/gh/dumbdemon/Stuff-n-Things/?ref=repository-badge)

# Stuff'n'Things Source Code

The source code for my Discord™ bot Stuff'n'Things. Mostly an entertainment bot.

## Commands

Command info moved to [GitHub Wiki](https://github.com/dumbdemon/Stuff-n-Things/wiki).

## Things to Note

* There is also a `/test` command that would be located in the
  [dev commands folder](https://github.com/dumbdemon/Stuff-n-Things/blob/9664c7745de8b6201305779758053cfdd9f6a791/src/main/java/com/terransky/stuffnthings/interactions/commands/slashCommands/devs)
  is not included as it would contain hardcoded IDs that I do not wish to expose. If you would like the command for
  yourself, please create one extending
  [SlashCommandInteraction](https://github.com/dumbdemon/Stuff-n-Things/blob/7095173d1bec13e0368cd4821c17e40af280ab16/src/main/java/com/terransky/stuffnthings/interfaces/interactions/SlashCommandInteraction.java)
  or delete/comment out the `new test(),` line in the
  [Managers](https://github.com/dumbdemon/Stuff-n-Things/blob/88eb9881baadd16207957cccde9f13f725957976/src/main/java/com/terransky/stuffnthings/Managers.java)
  class.
* The `/rob-fail-chance` was built for the Discord™ bot [UnbelievaBoat](https://unbelievaboat.com/)'s rob command.
* Database uses [MongoDB](https://www.mongodb.com/). An alternate database can be created implementing
  [DatabaseManager](https://github.com/dumbdemon/Stuff-n-Things/blob/master/src/main/java/com/terransky/stuffnthings/interfaces/DatabaseManager.java)
  and changing
  the `INSTANCE` variable within the interface.
