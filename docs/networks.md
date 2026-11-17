# Networks

Several servers can share one MySQL database. They then share every report, punishment and player.

## Setting it up
On every server, in `config.yml`:

```yaml
MySQL:
  MySqlEnabled: true
  host: your.database.host
  port: 3306
  database: superreport
  username: superreport
  password: "a strong password"

# Different on every server
ServerName: "Survival"
```

Create the database (`superreport` here) before starting the servers. The tables are created automatically by whichever server starts first.

## What's shared

| Feature | Across servers |
|---|---|
| Bans | Apply on every server. A player online elsewhere when they're banned is kicked within about 10 seconds |
| Mutes | Apply on every server straight away |
| Reporting | Players online on any server can be reported, by name |
| `/reports` | Shows open reports from every server |
| Join notification | Counts open reports from every server |
| `/unban`, `/unmute` | Work for any player seen on any server |
| Player names | Show correctly in menus and Discord, even for players who've never joined this server |
| New report alerts | Only reach staff on the server where the report was made |

## Server names
Every server needs its own `ServerName`. It's saved with each report and punishment, shown in Discord as `{server}`, and used to track which server each player is on. The review panel shows the server a player is online on.

On startup each server marks its own players as offline, in case it didn't shut down cleanly. Using the same name on two servers would mark players on the other one as offline too.

## Discord on a network
Each server posts only its own reports, bans and mutes. Turn the bot on for every server. They can share the same token and channels.
