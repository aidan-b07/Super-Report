# config.yml

The plugin's main settings: the database, this server's name, and the Discord bot.

```yaml
MySQL:
  MySqlEnabled: false
  host: localhost
  port: 3306
  database: superreport
  username: root
  password: password

ServerName: "Minecraft Server"

DiscordBot:
  Enabled: false
  Token: "TOKENHERE"
  # ... see the Discord bot page
```

## Database

| Setting | Default | What it does |
|---|---|---|
| `MySQL.MySqlEnabled` | `false` | `false` stores everything in `plugins/SuperReport/database.db` (SQLite). `true` uses the MySQL database below |
| `MySQL.host` | `localhost` | MySQL server address |
| `MySQL.port` | `3306` | MySQL server port |
| `MySQL.database` | `superreport` | Database name. It must already exist; the tables inside it are created for you |
| `MySQL.username` | `root` | MySQL user |
| `MySQL.password` | `password` | MySQL password |

SQLite needs no setup and is right for a single server. Use MySQL to share reports and punishments between servers, see [Networks](../networks.md).

If the plugin can't connect to the database on startup, it logs the error and disables itself.

## Server name

| Setting | Default | What it does |
|---|---|---|
| `ServerName` | `Minecraft Server` | This server's name |

The server name is:
- saved with each report and punishment, to show where it happened
- used to track which server each player is on when servers share a database
- shown in Discord embeds as `{server}`

On a network, give every server a different name.

## Update checks

| Setting | Default | What it does |
|---|---|---|
| `CheckForUpdates` | `true` | Checks GitHub for a new release on startup and twice a day |

When a newer release is out, the console says so, and staff with `superreport.updates` are told when they join. Both list every place it can be downloaded. The check only asks GitHub for the latest release and sends no server details, though, like any web request, GitHub can see your server's IP address. Set it to `false` to turn it off.

## Discord bot

| Setting | Default | What it does |
|---|---|---|
| `DiscordBot.Enabled` | `false` | Turns the bot on |
| `DiscordBot.Token` | | The bot's token |

The rest of the `DiscordBot` section sets up the channels and how each message looks. See [Discord bot](../discord.md).
