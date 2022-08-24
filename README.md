# Stuff'n'Things Source Code
The source code for my Discord™ bot Stuff'n'Things. Mostly an entertainment bot.

## Commands
`<>` - Required<br>
`[]` - Optional
* `/about`
* `/get-invite`
* `/kill random`
* `/kill suggest`
* `/lmgtfy web <search> [victim]`
* `/lmgtfy images <search> [victim]`
* `/meme reddit [subreddit]`
* `/rob-fail-chance <your-net-worth> <their-cash>`
* `/say <message> [channel]`
* `/spam send <target> [message]`
* `/spam upload <link>`
* `/suggest-command <suggestion> <importance>`
* `/user-info [user]`

## Things to Note
* There is also a `/test` command that would be located in the [commands folder]() is not included as it would contain hardcoded IDs that I do not wish to expose. If you would like the command for yourself, please create one implementing [ISlash]() or delete or comment out the `addCommand(new Test());` function in the [CommandManager]() class.
* The `/rob-fail-chance` was built for the Discord™ bot [UnbelievaBoat](https://unbelievaboat.com/)'s rob command.
* There is currently no database currently set up. There will be **Soon™**.
* The way I'm currently implementing `/kill random`'s source data is bad and I need a database.
* The command `/spam` may get removed at some point.
* [secretsAndLies();]() class referenced in the main class [[core]()] and the button class [[testButton]()] contains real names and has been removed to avoid doxxing.