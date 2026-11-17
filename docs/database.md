# Database

For anyone reading Super-Report's data directly, e.g. for a website or another plugin. Changing the data by hand while servers are running isn't supported.

The same tables are used with SQLite and MySQL.

## Conventions
- UUIDs are stored as text, e.g. `069a79f4-44e9-4726-a5be-fca90e38aaf5`.
- Times are epoch milliseconds (`BIGINT`).
- A UUID of all zeros, `00000000-0000-0000-0000-000000000000`, means the console.
- Nothing is deleted. Reports are closed by changing their status, and punishments by setting `lifted_at`.

## superreport_players
One row per player who has joined any server sharing the database.

| Column | Type | What it holds |
|---|---|---|
| `uuid` | `VARCHAR(36)` | The player's UUID. Primary key |
| `name` | `VARCHAR(16)` | Their name when they last joined |
| `first_seen` | `BIGINT` | When they first joined |
| `last_seen` | `BIGINT` | When they last joined or left |
| `online` | `BOOLEAN` | Whether they're online now |
| `server` | `VARCHAR(64)` | The `ServerName` they're on, or were last on |
| `last_ip` | `VARCHAR(45)` | Their IP address when they last joined |

## superreport_reports

| Column | Type | What it holds |
|---|---|---|
| `id` | `VARCHAR(36)` | The report's ID. Primary key |
| `reporter` | `VARCHAR(36)` | Who sent it |
| `reported` | `VARCHAR(36)` | Who was reported |
| `reason` | `VARCHAR(255)` | The reason |
| `server` | `VARCHAR(64)` | The `ServerName` it was sent from |
| `created_at` | `BIGINT` | When it was sent |
| `status` | `VARCHAR(16)` | `OPEN`, `PUNISHED` or `DISMISSED` |
| `resolved_by` | `VARCHAR(36)` | Who closed it. Empty while open |
| `resolved_at` | `BIGINT` | When it was closed. Empty while open |
| `punishment_id` | `VARCHAR(36)` | The punishment it led to, if any |

## superreport_punishments

| Column | Type | What it holds |
|---|---|---|
| `id` | `VARCHAR(36)` | The punishment's ID. Primary key |
| `type` | `VARCHAR(16)` | `BAN` or `MUTE` |
| `player` | `VARCHAR(36)` | Who was punished |
| `reason` | `VARCHAR(255)` | The reason |
| `issued_by` | `VARCHAR(36)` | Who issued it |
| `issued_at` | `BIGINT` | When it was issued |
| `expires_at` | `BIGINT` | When it ends. Empty for permanent |
| `lifted_by` | `VARCHAR(36)` | Who lifted it early, if anyone |
| `lifted_at` | `BIGINT` | When it was lifted, if it was |
| `lift_reason` | `VARCHAR(255)` | Why it was lifted, if a reason was given |
| `report_id` | `VARCHAR(36)` | The report it came from. Empty for `/punish` |
| `server` | `VARCHAR(64)` | The `ServerName` it was issued on |
| `notified` | `BOOLEAN` | Whether the other servers have acted on it yet |

A punishment is **active** while `lifted_at` is empty and `expires_at` is either empty or in the future.

## superreport_discord_messages
Which Discord message shows which report or punishment, so it can be updated. A row is removed once its message no longer needs updating.

| Column | Type | What it holds |
|---|---|---|
| `kind` | `VARCHAR(16)` | `report` or `punishment` |
| `ref_id` | `VARCHAR(36)` | The report's or punishment's `id` |
| `channel_id` | `BIGINT` | The Discord channel |
| `message_id` | `BIGINT` | The Discord message |

## superreport_schema
A single row holding the schema `version`. When an update changes the tables, the change runs once on startup and this number goes up.
