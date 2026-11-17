# Commands and permissions

## Commands

| Command | What it does | Permission |
|---|---|---|
| `/report <player>` | Opens the reason menu for that player | `superreport.report` |
| `/report <player> <reason>` | Sends a report straight away with what you typed as the reason | `superreport.report` |
| `/reports` | Lists open reports, newest first | `superreport.reports` |
| `/punish <player>` | Opens ban or mute, then reason, then length | `superreport.punish` |
| `/ban <player>`, `/tempban <player>` | Same as `/punish`, starting at the ban reasons | `superreport.punish` |
| `/mute <player>`, `/tempmute <player>` | Same as `/punish`, starting at the mute reasons | `superreport.punish` |
| `/unban <player>` | Lifts a ban | `superreport.unban` |
| `/unmute <player>` | Lifts a mute | `superreport.unmute` |
| `/superreport` | Lists the Super-Report commands you can use | `superreport.help` |
| `/superreport reload` | Reads every config file again | `superreport.reload` |

### Reporting
- You can only report players who are online, either on this server or on another one sharing the database (see [Networks](networks.md)).
- You can't report yourself, or report the same player again while your first report is still open.
- Staff with `superreport.notifications` are told about each new report.

### Reviewing reports
Click a report in `/reports` to see both players' history (times reported, reports sent, bans, mutes, online status), then:
- **Punish** to ban or mute the player. The report is closed once the punishment is applied.
- **Dismiss** to close the report without punishing anyone.

If someone else closes the report while you have it open, you're told and nothing is applied twice.

### Punishing
Every punishment goes through the same menus: ban or mute, a reason from [the reason menu](configuration/menus.md), then a length.
- The length menu has +1 and -1 buttons for minutes, hours, days, weeks and months, presets from 1 day to 1 year, and Permanent.
- **Back** and **Next** buttons are along the bottom of every menu.
- A ban kicks the player straight away. Both bans and mutes apply on every server sharing the database.
- `/punish` won't add a second ban or mute to a player who already has one.

### Lifting
`/unban` and `/unmute` lift the punishment early. It stays in the player's history, marked as lifted. A punishment that has already run out counts as not banned or not muted.

### Reloading
`/superreport reload` reads `config.yml`, `Messages.yml` and the three menu files again, and reconnects the Discord bot with its new settings. Menus already open keep showing what they opened with until they're reopened.

The database connection isn't changed by a reload. If the `MySQL` settings changed, you're told to restart the server for them to apply. `CheckForUpdates` also needs a restart.

## Permissions

| Permission | Default | What it does |
|---|---|---|
| `superreport.report` | Everyone | Use `/report` |
| `superreport.help` | Everyone | Use `/superreport` |
| `superreport.reload` | Op | Use `/superreport reload` |
| `superreport.reports` | Op | Use `/reports` |
| `superreport.punish` | Op | Use `/punish`, `/ban`, `/mute`, `/tempban` and `/tempmute` |
| `superreport.unban` | Op | Use `/unban` |
| `superreport.unmute` | Op | Use `/unmute` |
| `superreport.notifications` | Op | Be told about each new report, and how many are open when joining |
| `superreport.updates` | Op | Be told when a new version of Super-Report is out |
| `superreport.banbypass` | Nobody | Can't be banned |
| `superreport.mutebypass` | Nobody | Can't be muted |

### Bypass permissions
- Staff trying to ban or mute someone with a bypass permission are told it can't be done. If it came from a report, the report stays open.
- Bukkit can only check permissions for online players, so a ban or mute given while the player is offline still goes through. When they join, a mute still won't apply to someone with `superreport.mutebypass`.
