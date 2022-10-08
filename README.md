# Stuff'n'Things Source Code
The source code for my Discord™ bot Stuff'n'Things. Mostly an entertainment bot.

## Commands
`<>` - Required<br>
`[]` - Optional
* `/about`
* `/check-perms server`
* `/check-perms channel <channel>`
* `/color-info hex-triplet <triplet>`
* `/color-info rgb <red> <green> <blue>`
* `/color-info cmyk <cyan> <magenta> <yellow> <black>`
* `/config kill max-kills`
* `/config kill set-timeout`
* `/dad-joke`
* `/get-invite`
* `/kill random`
* `/kill target`
* `/kill suggest`
* `/lmgtfy web <search> [victim]`
* `/lmgtfy images <search> [victim]`
* `/meme reddit [subreddit]`
* `/ner <start-count> <iterations>`
* `/rob-fail-chance <your-net-worth> <their-cash>`
* `/say <message> [channel]`
* `/suggest-command <suggestion> <importance>`
* `/user-info [user]`

## Things to Note

* There is also a `/test` command that would be located in
  the [commands folder](https://github.com/dumbdemon/Stuff-n-Things/tree/master/src/main/java/com/terransky/StuffnThings/commandSystem/commands)
  is not included as it would contain hardcoded IDs that I do not wish to expose. If you would like the command for
  yourself, please create one
  implementing [ISlashCommand](https://github.com/dumbdemon/Stuff-n-Things/blob/master/src/main/java/com/terransky/StuffnThings/commandSystem/interfaces/ISlashCommand.java)
  or delete/comment out the `addCommand(new Test());` line in
  the [CommandManager](https://github.com/dumbdemon/Stuff-n-Things/blob/master/src/main/java/com/terransky/StuffnThings/commandSystem/CommandManager.java)
  class.
* The `/rob-fail-chance` was built for the Discord™ bot [UnbelievaBoat](https://unbelievaboat.com/)'s rob command.
* Database uses [SQLite](https://www.sqlite.org/index.html) [WIP].
* The way I'm currently implementing `/kill random`'s source data is bad and the database table for it has not been set
  up yet.
* `secretsAndLies()` class referenced in the main
  class [[core](https://github.com/dumbdemon/Stuff-n-Things/blob/master/src/main/java/com/terransky/StuffnThings/core.java#L49)]
  and listener
  class [[ListeningForEvents](https://github.com/dumbdemon/Stuff-n-Things/blob/87ec70d2f9174c042ce784cdd11c580e8866a322/src/main/java/com/terransky/StuffnThings/listeners/ListeningForEvents.java#L45)]
  contains real names and has been removed to avoid doxxing.
