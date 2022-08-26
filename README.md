# Stuff'n'Things Source Code
The source code for my Discord™ bot Stuff'n'Things. Mostly an entertainment bot.

## Commands
`<>` - Required<br>
`[]` - Optional
* `/about`
* `/dad-joke`
* `/get-invite`
* `/kill random`
* `/kill target`
* `/kill suggest`
* `/lmgtfy web <search> [victim]`
* `/lmgtfy images <search> [victim]`
* `/meme reddit [subreddit]`
* `/rob-fail-chance <your-net-worth> <their-cash>`
* `/say <message> [channel]`
* `/suggest-command <suggestion> <importance>`
* `/user-info [user]`

## Things to Note
* There is also a `/test` command that would be located in the [commands folder](https://github.com/dumbdemon/Stuff-n-Things/tree/master/src/main/java/com/terransky/TestingBot/slashSystem/commands) is not included as it would contain hardcoded IDs that I do not wish to expose. If you would like the command for yourself, please create one implementing [ISlash](https://github.com/dumbdemon/Stuff-n-Things/blob/master/src/main/java/com/terransky/TestingBot/slashSystem/ISlash.java) or delete or comment out the `addCommand(new Test());` function in the [CommandManager](https://github.com/dumbdemon/Stuff-n-Things/blob/master/src/main/java/com/terransky/TestingBot/slashSystem/CommandManager.java) class.
* The `/rob-fail-chance` was built for the Discord™ bot [UnbelievaBoat](https://unbelievaboat.com/)'s rob command.
* There is currently no database currently set up. There will be **Soon™**.
* The way I'm currently implementing `/kill random`'s source data is bad and I need a database.
* `secretsAndLies();` class referenced in the main class [[core](https://github.com/dumbdemon/Stuff-n-Things/blob/9fe8fd3353d27519a99fcbe3450f27ef0ce548ba/src/main/java/com/terransky/TestingBot/core.java#L36)] and the button class [[testButton](https://github.com/dumbdemon/Stuff-n-Things/blob/9fe8fd3353d27519a99fcbe3450f27ef0ce548ba/src/main/java/com/terransky/TestingBot/buttonSystem/buttons/testButton.java#L26)] contains real names and has been removed to avoid doxxing.
