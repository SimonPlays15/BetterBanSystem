# BetterBanSystem Plugin

Welcome to the BetterBanSystem plugin!<br>
It was never easier to ban, mute or warn a user

## Installation

1. Download the Plug-In latest version from SpigotMC or of the release tab from Github.
2. After downloading, move the Plug-In into your `plugins` folder of your Minecraft Server.<br>
3. Restart your server.
4. After restarting the server, you can edit the config.yml file inside the generated
   Directory: `plugins/BetterBanSystem/`

## How to use

After the installation the Plug-In will create the basic config.yml File and the default language file
inside `plugins/BetterBanSystem/languages/`
<br>
The default config will look like this:
<br>

````yml
chat:
  prefix: "&6[&cBetterBanSystem&6]"
  language: "en_US"

database:
  type: none

automod:
  use: true
  modules:
    chat:
      spamming:
        use: true
        maxMessages: 5
        action: "DELETE;WARN!3:reason=Spamming"
      duplicatedText:
        use: false
      capslock:
        use: true
      links:
        use: true
      badwords:
        use: true
        replaceWords: true
        badWordList: [ ]

warns:
  autodelete:
    use: true
    time: 60
    unit: MINUTES
  actions:
    3: "mute %p 30min"
    5: "timeban %p 1h"

permissions:
  system: SPIGOT

logging:
  logfile: true
  debug: false

exempted-players: [ ]
exempted-mute-players: [ ]
exempted-warn-players: [ ]
exempted-ips: [ ]

mute:
  blocked-commands:
    - me
    - tell
    - msg
````

## Config Explanation

### Chat settings

You can set your own Prefix for every Message. Simply edit the line `chat.prefix` to what you want.<br>
If you don't want to print the prefix, simply edit the `chat.prefix` line to "none" or just leave it blank like `""`

### How to use the database tag?

If you want to use a Database for the bans,mutes and warns you can simply change the `database.type`
to `MYSQL`, `SQLITE` or `MONGODB`<br>
If you want to see some examples for the specific database configuration, open the summarys

<details>
<summary>SQLITE</summary>

````yml
database:
  type: SQLITE
  dbFile: "path/to/your/database_file.db"
````

You don't need to create this File on your own. After restarting the server (not reloading) the Plug-In will create the
file on its own and created the needed tables.
</details>

<details>
<summary>MYSQL / MONGODB</summary>

````yml
database:
  type: MYSQL # or MONGODB
  hostname: localhost # or 127.0.0.1
  port: 3363
  user: username
  password: yoursupersecretpassword
`````

You don't need to create the Database or the tables. If a connection to the MySQL or MongoDB was successful the Plug-In
will create the database and the needed tables on its own.

</details>

### AutoMod

<details>
<summary>Config Lines</summary>

````yaml
automod:
  use: true
  modules:
    chat:
      spamming:
        use: true
        maxMessages: 5
        action: "DELETE;WARN!3:reason=Spamming"
      duplicatedText:
        use: false
      capslock:
        use: true
      links:
        use: true
      badwords:
        use: true
        replaceWords: true
        badWordList: [ ]
````

</details>

Here you can find some AutoMod "modules" which you can enable or disable like you want. Even if you don't want to use
the AutoMod system at all, simply change `automod.use` from `true` to `false`.<br>
Inside the modules path you can find all modules this plug-in provides. (Currently for version 1.0 only the Chat
module).<br>
You can enable or disable the modules on its own. Just change the `true` value to `false`.<br>

#### The "action" Tag

The `action` tag defines what action the AutoMod should take for the specific module.<br>
<details>
<summary>Valid ActionTypes</summary>

- `DELETE`
    - Simply blocks the message from the player, so it will not be printed to the chat.
- `WARN`
    - Simply calls the "warn" Command from the console with the given parameters
- `INSTANTBAN`
    - Simply calls the "ban" Command from the console with the given parameters
- `MUTE`
    - Simply calls the "mute" Command from the console with the given parameters
- `TIMEBAN`
    - Simply calls the "timeban" Command from the console with the given parameters.

</details>

Every action is seperated with an `;` so it's quite simple to add more actions.<br>
You can add an `!(number)` to every action like `DELETE!4`. This tells the AutoMod system that the Player has to
execute
the specific module four times before the execution.<br>
The `WARN`, `INSTANTBAN`, `MUTE` and `TIMEBAN` actions having some more parameters they need.<br>
So the `MUTE` action needs the `reason` and the `duration` parameters. For
example: `"MUTE:reason=Stop spamming:duration=5min"`<br>
So the Plug-In will execute the `MUTE` action which simply mutes the user for "5 minutes" and for the reason "Stop
Spamming"<br>
<details>
<summary>Available Parameters</summary>

- `REASON`
- `DURATION`

</details>

##### The Chat Modules

**!NOTE!** For the Chat Modules the standard action is `DELETE`. So the message will not be printed inside the chat if
no `action` Tags are defined.<br>

##### Examples

<details>
<summary>Examples</summary>

```yaml
automod:
  use: true
  modules:
    chat:
      spamming:
        use: true
        maxMessages: 5
```

If the player sends more than 5 messages in 4 seconds, he gets warned after 4 messages and then the messages gets
blocked. Because the default action is `DELETE`.<br>

```yaml
automod:
  use: true
  modules:
    chat:
      spamming:
        use: true
        maxMessages: 5
        action: "DELETE!2;WARN!3:reason=Spamming"
```

Now we set 2 actions. If the player sends more than 5 messages in 4 seconds, he gets warned after 4 messages.<br>
If the player now sends more messages, the messages will only be deleted if the player exceeded the message limit 2
times. Because of `DELETE!2`.

The second action is that he gets warned, after 3 executions for the given reason "Spamming".

Its quite simple or what do you think?

</details>

#### Chat modules

<details>
<summary>Spamming Module</summary>

This module checks if the player sends a big amount of messages in a certain time (4 seconds).<br>
With the `maxMessages` value you set the amount of messages the player needs to send in 4 seconds, then he gets firstly
warned before the given actions will be taken.

</details>

<details>
<summary>duplicatedText Module</summary>

This module checks if the player sends 2 messages with the exact same content

</details>

<details>
<summary>capslock Module</summary>

This module checks if the sended player message is written in only CapsLock or the complete content of the message
contains
more than 70% uppercase letters.

</details>

<details>
<summary>links Module</summary>

This module checks if the sended player message contains a link.

</details>

<details>
<summary>badwords Module</summary>

This module checks if the sended player message contains bad words from the `badWordList`.

If `replaceWords` is set to `true` the message will not be blocked, its just refactoring the given words into `*`.<br>
If it's set to false, the message will be blocked by default. (if no `action` tag is set.)

</details>

### Warn Settings

```yaml
warns:
  autodelete:
    use: true
    time: 60
    unit: MINUTES
  actions:
    3: "mute %p 30min"
    5: "timeban %p 1h"
```

Here you can set if player warns should be deleted after a certain time. (Like now after 60 Minutes 1 warn gets removed
after the creation)<br>
The "actions" are quite simple. After `3` warns the "mute" commands is getting executed or which command you like. Note
that "%p" stands for the player name.<br>
Currently for 3 warns an action is executing and for 5 warns. You can add more actions by simply adding more lines with
the same schema.

### Permissions

#### Permission Handling

The Plug-In itself checks the player permissions over the given PermissionManager.<br>
By default its "SPIGOT" for the default Spigot Permissions Handling.<br>
If you have a PermissionSystem on your server, you can change the config.yml tag `permissions.system` to the
following:<br>

<details>
<summary>Spigot Servers</summary>

- `SPIGOT`
    - for the default permission handling given from the Spigot API.
        - **!NOTE!** From the Spigot API its only available to check the player permissions if the player is
          **online!**
- `LUCKPERMS`
    - for the LuckPerms permission handling.
        - **!NOTE!** You have to run LuckPerms on your server!
- `VAULT`
    - for the Vault permission handling
        - **!NOTE!** You have to run Vault on your server!

</details>
<details>
<summary>BungeeCord Servers</summary>

- `BUNGEECORD`
    - for the default permission handling given from the BungeeCord API.
        - **!NOTE!** From the BungeeCord API its only available to check the player permissions if the player is
          **online!**
- `CLOUDNET`
    - for the CloudNet permissions handling
        - **!NOTE!** You have to run CloudNet with the CloudNet-Permissions Module!

</details>

If the Plug-In cannot find one of the given permission systems its using the default permission system given from the
runtime of the server (spigot or bungeecord)<br>
Should the problem appear that the Plug-In cannot find ANY System not even the default from Spigot / BungeeCord it's
returning for all players that he has no permissions for safety reasons.

#### Plug-In Permissions

Here is a list of all Permissions this Plug-In provides.<br>
Every permission starts with `betterbansystem.`

- `betterbansystem.*`
    - Grants **complete** access to the plugin commands and functionalities.
- `betterbansystem.commands.*`
    - Grants the player the complete access to all commands
- `betterbansystem.exempt.*`
    - Grants the player the accessibility to exempt bans, kicks, warns, mutes (only from
      players) and all AutoMod actions.

<details>
<summary> Command Permissions </summary>

- `betterbansystem.commands.ban`
    - Grants access to the `/ban` command
- `betterbansystem.commands.banip`
    - Grants access to the `/banip` command
- `betterbansystem.commands.kick`
    - Grants access to the `/kick` command
- `betterbansystem.commands.lookup`
    - Grants access to the `/lookup` command
- `betterbansystem.commands.lookup.other`
    - Grants access to the `/lookup (player)` command to lookup other players
- `betterbansyste.commands.timeban`
    - Grants access to the `/timeban` command
- `betterbansystem.commands.unban`
    - Grants access to the `/unban` command
- `betterbansystem.commands.unbanip`
    - Grants access to the `/unbanip` command
- `betterbansystem.commands.warn`
    - Grants access to the `/warn` command
- `betterbansystem.commands.delwarn`
    - Grants access to the `/delwarn` command
- `betterbansystem.commands.mute`
    - Grants access to the `/mute` command
- `betterbansystem.commands.unmute`
    - Grants access to the `/unmute` command

</details>

<details>
<summary>Exempt permissions</summary>

- `betterbansystem.exempt.ban`
    - If a player has this permission he cannot get banned by a player. Only per Console.
- `betterbansystem.exempt.mute`
    - If a player has this permission he cannot get muted by a player. Only per Console.
- `betterbansystem.exempt.warn`
    - If a player has this permission he cannot get warned by a player. Only per Console
- `betterbansystem.exempt.kick`
    - If a player has this permission he cannot get kicked by a player. Only per console
- `betterbansystem.exempt.automod`
    - If a player has this permission he is exempted from all AutoMod actions

</details>

### How to exempt a Player from Bans, Warns or Mutes

#### Add a Player or IP-Address to the config

To exempt a player from bans, warns or mutes is quite simple.<br>
You can simply add the players name inside the config.yml to the `exempted-players` list to exempt the Player from
bans<br>
or you add the players name to the `exempted-mute-players` or `exempted-warns-players` list to exempt the player from
mutes / warns.<br>
You can also add an IP-Address to the `exempted-ips` list to exempt a specific IP-Address from bans.<br>
**NOTE!**<br>
You can ban, warn, mute and ban an IP address over the console, even if the name or the ip address is inside one of the
lists.<br>

#### Add the Permission to a Player or an IP-Address.

You can also use one of the [Exempt Permissions](#permissions) to exempt a Player from warns, bans mutes.<br>
**NOTE!**<br>
If a player has the `betterbansystem.exempt.ban` permission he can get IP-Banned.<br>
To add an IP-Address to the exempted list `exempted-ips`.

### You have some suggestions, tips or additional functions?

Simply Join our discord [MCDevStudios](https://discord.gg/QWpsnKPC8W) and create a ticket and suggest your function

## Supported Minecraft Spigot Versions

| Version   | Supported    |
|-----------|--------------|
| \> 1.20.2 | ❌ NOT TESTED |
| 1.20.x    | ✅            |
| 1.19.x    | ✅            |
| < 1.12.2  | ❌ NOT TESTED |

## Supported Minecraft BungeeCord Versions

| Version      | Supported        |
|--------------|------------------|
| ALL VERSIONS | ❌ NOT TESTED YET |

## Report Issues ⚠️

Have you found a bug, a vulnerability, or have some other issues? Join our
discord [MCDevStudios](https://discord.gg/QWpsnKPC8W) and create a ticket.