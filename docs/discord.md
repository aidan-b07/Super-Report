# Discord bot

Super-Report can post every report, ban and mute to Discord, and keeps each message up to date: when a report is dealt with, or a punishment is lifted or runs out, the original message changes to show it.

<!-- SHOWCASE: a ban embed going from BANNED to LIFTED after /unban -->

## Setting it up

### 1. Create the bot
1. Open the [Discord Developer Portal](https://discord.com/developers/applications) and click **New Application**. Give it a name, e.g. `Reports`.
2. Go to **Bot** and click **Reset Token**. Copy the token and keep it private: anyone with it can control your bot.
3. Leave the **Privileged Gateway Intents** off. The bot only sends and edits its own messages.

### 2. Invite it to your server
1. Go to **OAuth2** → **URL Generator**.
2. Under **Scopes**, tick `bot`.
3. Under **Bot Permissions**, tick:
   - `View Channels`
   - `Send Messages`
   - `Embed Links`
   - `Mention Everyone`, only to ping a role that isn't mentionable
4. Open the generated link and add the bot to your server.

Make sure it can see and send messages in each channel you'll use.

### 3. Copy the channel IDs
1. In Discord, open **User Settings** → **Advanced** and turn on **Developer Mode**.
2. Right-click each channel and click **Copy Channel ID**.

Reports, bans and mutes can each have their own channel or share one.

### 4. Fill in config.yml
```yaml
DiscordBot:
  Enabled: true
  Token: "your bot token"
  ReportMessage:
    ChannelID: "123456789012345678"
  BanMessage:
    ChannelID: "123456789012345678"
  MuteMessage:
    ChannelID: "123456789012345678"
```

Restart the server. The bot connects in the background, so startup isn't slowed down. The console warns if the token is wrong or a channel can't be found.

A message type whose `ChannelID` is left as `CHANNELIDHERE` isn't posted.

## The messages

| Section | Posted when |
|---|---|
| `ReportMessage` | A player is reported |
| `BanMessage` | A player is banned |
| `MuteMessage` | A player is muted |

Each one has a `ChannelID` and a `Layout`:

| Setting | What it does |
|---|---|
| `ChannelID` | Where it's posted |
| `Layout.Message` | Text above the embed, e.g. a role ping. Leave empty for none |
| `Layout.Color` | The bar down the side, as a hex colour, e.g. `E74C3C` |
| `Layout.Author` | Small text at the top |
| `Layout.AuthorIcon` | Image next to the author |
| `Layout.Title` | The heading. Cut at 256 characters |
| `Layout.Thumbnail` | Image in the top right |
| `Layout.Description` | Text under the title, as a list of lines |
| `Layout.Fields` | A list of fields, each with `Name`, `Value` and `Inline` |
| `Layout.Footer` | Small text at the bottom |
| `Layout.FooterIcon` | Image next to the footer |
| `Layout.Timestamp` | `true` shows when it was posted, in each reader's own time zone |

Every text setting can use the message's [placeholders](#placeholders), images included.

- **Empty settings:** anything set to `none` or left empty is left out.
- **Images:** an image setting that isn't an `http://` or `https://` address is left out instead of stopping the message.
- **Fields:** `Inline: true` places fields side by side, up to three in a row. `Inline` defaults to `true`. A field whose value ends up empty shows `-`, because Discord refuses empty fields.

### Pinging a role
Set `Layout.Message` to `<@&ROLE_ID>`. To get a role's ID, turn on Developer Mode as above, then right-click the role in **Server Settings** → **Roles** and click **Copy Role ID**.

The role is only pinged if it's mentionable, or if the bot has `Mention Everyone`.

## Placeholders

### Players
Each player in a message has the same four placeholders. For example, the reported player:

| Placeholder | Shows |
|---|---|
| `{reported.name}` | Their name. `Console` for actions taken from the console |
| `{reported.uuid}` | Their UUID |
| `{reported.head}` | A link to an image of their face, for `Thumbnail`, `AuthorIcon` or `FooterIcon` |
| `{reported.ip}` | Their last known IP address |

IP addresses aren't shown by default. Everyone who can read the channel would see them, so think carefully before adding them.

### ReportMessage
| Placeholder | Shows |
|---|---|
| `{reported.*}` | The reported player |
| `{reporter.*}` | The player who sent the report |
| `{reason}` | The reason |
| `{report.id}` | A short ID for the report, e.g. `3f2a9c1e` |
| `{status}` | The [status](#status) |
| `{server}` | The `ServerName` from `config.yml` |

### BanMessage and MuteMessage
`MuteMessage` uses `muted` and `mute` in place of `banned` and `ban`.

| Placeholder | Shows |
|---|---|
| `{banned.*}` | The banned player |
| `{punisher.*}` | The staff member who issued it |
| `{ban.reason}` | The reason |
| `{ban.duration}` | The full length, e.g. `3 days` or `Permanent` |
| `{ban.expires}` | When it ends, as a date and a live countdown (e.g. `in 3 days`), or `Never` |
| `{status}` | The [status](#status) |
| `{server}` | The `ServerName` from `config.yml` |

## Status
`{status}` shows the state of the report or punishment, and the message is edited as it changes. The default layouts show it as the first field.

| Message | Status |
|---|---|
| Report | `OPEN`, then `RESOLVED` or `DISMISSED` |
| Ban | `BANNED`, then `LIFTED` or `EXPIRED` |
| Mute | `MUTED`, then `LIFTED` or `EXPIRED` |

Once a report is closed, or a punishment is lifted or has run out, its embed turns `Status.Color` (grey by default). When a punishment ends, `{ban.expires}` / `{mute.expires}` changes from the countdown to when it was lifted or expired.

The texts are in `DiscordBot.Status`:

| Setting | Default | Extra placeholders |
|---|---|---|
| `Color` | `95A5A6` | |
| `Open` | `` `OPEN` `` | |
| `Punished` | `` `RESOLVED` by {resolver.name} {time}\n{outcome} `` | `{resolver.*}`, `{outcome}` (e.g. `Banned for 3 days`), `{time}` |
| `Dismissed` | `` `DISMISSED` by {resolver.name} {time} `` | `{resolver.*}`, `{time}` |
| `Banned` | `` `BANNED` `` | |
| `Muted` | `` `MUTED` `` | |
| `Lifted` | `` `LIFTED` by {lifter.name} {time} `` | `{lifter.*}`, `{time}` |
| `Expired` | `` `EXPIRED` {time} `` | `{time}` |

- **`{time}`** is when it changed, shown as e.g. `2 minutes ago`.
- **Backticks** make the text a rounded box on Discord.
- **All the message's own placeholders** can be used in a status text too.

Expired punishments are checked every minute, so a message shows `EXPIRED` within about a minute of it running out.

A layout without `{status}` anywhere (such as one kept from before 2.0) still works. When it's updated, the status is added as an extra field at the bottom.

If an updated message was deleted on Discord, or its channel is gone, it's skipped and forgotten.

If the bot isn't connected at the moment a report is closed or a punishment is lifted, that message isn't updated. Expiry is the exception: it's checked every minute, so it catches up once the bot reconnects.
