# Installation

## Requirements
- Spigot or Paper, Minecraft **1.8.8** or newer
- Java **8** or newer, or whatever your server version needs

## Installing
1. Put `SuperReport.jar` in your server's `plugins` folder.
2. Start the server. Super-Report creates its files in `plugins/SuperReport/`:

   | File | What it's for |
   |---|---|
   | `config.yml` | Database, server name and the Discord bot. See [config.yml](configuration/config.md) |
   | `Messages.yml` | Every message. See [Messages.yml](configuration/messages.md) |
   | `ReportMenu.yml` | The `/report` menu. See [Menus](configuration/menus.md) |
   | `Ban-Reasons.yml`, `Mute-Reasons.yml` | The ban and mute reason menus. See [Menus](configuration/menus.md) |
   | `database.db` | Your data, when using the built-in SQLite database |

3. Change what you want, then restart.

That's all a single server needs. To share data between servers, see [Networks](networks.md). To post to Discord, see [Discord bot](discord.md).

## Missing settings are added back
When the plugin starts, any setting it needs that's missing from `Messages.yml`, or the size, title and border of a menu, is added back with its default. Values you've changed are never touched. So after an update, new messages appear in your files on their own.

Reasons are the exception. If you delete a reason from a menu, it stays deleted.

## Upgrading from 1.x
- **Your files:** keep them. Missing settings are added back as described above. To get 2.0's new menus and messages instead of your old ones, delete those files and restart.
- **Your data:** 2.0 uses a new database layout and leaves the 1.x tables (`superreport_playerdata`, `superreport_reports_active` and so on) untouched. 1.x reports and punishments aren't brought across, so open reports and active bans from 1.x won't show up in 2.0.
- **Permissions:** the same names are used as in 1.x, including `superreport.banbypass` and `superreport.mutebypass`.
- **Removed:** `/superreport` and `superreport.help` from 1.x are no longer part of the plugin.
