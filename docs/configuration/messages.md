# Messages.yml

Every message players and staff see. Colour codes use `&`, e.g. `&a` for green and `&l` for bold.

If a message is missing from the file, it's added back with its default on the next start. Messages you've changed are never touched.

## Prefix

| Key | Default |
|---|---|
| `Prefix` | `&1&l[&9&lSuper-Report&1&l] ` |

Put `{prefix}` anywhere in a chat message to insert this. Changing `Prefix` updates every message that uses it.

## Placeholders
Each message can use the placeholders listed next to it. In the messages below:
- `{action}` is `banned` or `muted`
- `{duration}` is e.g. `for 3 days` or `permanently`
- `{time}` is the time left, e.g. `2 days, 4 hours, 30 minutes`

## General

| Key | Placeholders | When it's sent |
|---|---|---|
| `Commands.PlayersOnly` | | The console runs a command that needs a player |
| `Commands.NoPermission` | | Someone without permission runs `/superreport reload` |
| `SuperReport.HelpHeader` | `{version}` | The first line of `/superreport` |
| `SuperReport.HelpLine` | `{command}`, `{description}` | Once for each command in `/superreport` that the player can use |
| `SuperReport.Reloaded` | | `/superreport reload` worked |
| `SuperReport.RestartForDatabase` | | After a reload where the `MySQL` settings changed |
| `SuperReport.ReloadFailed` | | The reload went wrong. The console has the details |
| `Updates.Available` | `{latest}`, `{current}` | To staff with `superreport.updates` when they join, if a newer version is out |
| `Updates.Download` | `{platform}`, `{link}` | Sent after `Updates.Available`, once for each place the new version can be downloaded |

## Reporting

| Key | Placeholders | When it's sent |
|---|---|---|
| `ReportCommand.Usage` | | `/report` without a player |
| `ReportCommand.PlayerNotOnline` | `{player}` | The player isn't online on any server |
| `ReportCommand.SelfReport` | | Someone tries to report themselves |
| `ReportCommand.ReportExists` | | The reporter already has an open report on that player |
| `ReportCommand.ReportSuccessful` | | To the reporter, once their report is sent |
| `ReportCommand.ReportError` | | The report couldn't be saved |
| `ReportCommand.StaffNotification` | `{reporter}`, `{reported}`, `{reason}` | To staff with `superreport.notifications` when a report comes in. Not sent to the reporter |
| `ReportCommand.JoinNotification` | `{count}` | To staff with `superreport.notifications` when they join, if reports are open |

## Reviewing reports
Sent to the staff member who closed the report.

| Key | Placeholders | When it's sent |
|---|---|---|
| `Punishment.Applied` | `{player}`, `{action}`, `{duration}`, `{reason}` | The player was punished and the report closed |
| `Report.Dismissed` | `{player}` | The report was dismissed |
| `Report.AlreadyClosed` | `{player}` | Someone else closed the report first |
| `Report.ArchiveError` | `{player}` | The report couldn't be closed. A punishment may still have been applied |
| `Punishment.Bypassed` | `{player}`, `{action}` | The player has the bypass permission. The report stays open |
| `Punishment.Error` | | The punishment couldn't be saved. The report stays open |

## /punish, /ban and /mute

| Key | Placeholders | When it's sent |
|---|---|---|
| `PunishCommand.Usage` | | `/punish` without a player |
| `PunishCommand.Self` | | Someone tries to punish themselves |
| `PunishCommand.Applied` | `{player}`, `{action}`, `{duration}`, `{reason}` | The punishment was applied |
| `PunishCommand.AlreadyPunished` | `{player}`, `{action}` | The player is already banned or muted |
| `PunishCommand.Bypassed` | `{player}`, `{action}` | The player has the bypass permission |
| `PunishCommand.Error` | | The punishment couldn't be saved |

## /unban and /unmute

| Key | Placeholders | When it's sent |
|---|---|---|
| `UnbanCommand.Successful` | `{player}` | The ban was lifted |
| `UnbanCommand.PlayerNotBanned` | `{player}` | The player isn't banned |
| `UnbanCommand.Fail` | `{player}` | The ban couldn't be lifted |
| `UnmuteCommand.Successful` | `{player}` | The mute was lifted |
| `UnmuteCommand.PlayerNotMuted` | `{player}` | The player isn't muted |
| `UnmuteCommand.Fail` | `{player}` | The mute couldn't be lifted |

## Ban screen and mute notice
These are lists: each entry is one line.

| Key | Placeholders | Shown |
|---|---|---|
| `TemporaryBanMessage` | `{banner}`, `{reason}`, `{time}` | On the kick screen of a temporarily banned player |
| `PermanentBanMessage` | `{banner}`, `{reason}` | On the kick screen of a permanently banned player |
| `TemporaryMuteMessage` | `{muter}`, `{reason}`, `{time}` | To a temporarily muted player who tries to chat |
| `PermanentMuteMessage` | `{muter}`, `{reason}` | To a permanently muted player who tries to chat |

`{banner}` and `{muter}` show `Console` for punishments given from the console.

To tell players where to appeal, add a line with your server's Discord or website to any of these.
