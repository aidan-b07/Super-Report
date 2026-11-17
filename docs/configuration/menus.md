# Menus

Three menus can be changed:

| File | Menu |
|---|---|
| `ReportMenu.yml` | The reasons players pick from with `/report <player>` |
| `Ban-Reasons.yml` | The reasons staff pick from when banning |
| `Mute-Reasons.yml` | The reasons staff pick from when muting |

The other menus (the report list, the review panel, ban or mute, and the length menu) aren't configurable.

## Items
Every item in these files uses the same settings:

| Setting | What it does |
|---|---|
| `Material` | The item, by its modern name, e.g. `diamond_sword` or `red_stained_glass_pane` |
| `Name` | Its name. Supports `&` colour codes |
| `Lore` | Lines under the name, as a list. Leave it out for none |
| `Amount` | How many show in the stack. Defaults to 1 |
| `Head` | Only for `player_head`: whose skin to show, e.g. `{player}` |
| `Reason` | For reason items: the reason saved and shown to the player and in Discord. Defaults to the item's name with colours removed |

Use modern material names on every version. They're translated for older servers, so `red_stained_glass_pane` works on 1.8 too. A name that doesn't exist on the server shows as a barrier, and the console says which setting to fix.

Slots count from 0 in the top left, left to right. A 5-row menu has slots 0 to 44.

## ReportMenu.yml

```yaml
ReportMenu:
  Title: "&8Report » {player}"
  Size: 45
  Items:
    Fill:
      Material: "black_stained_glass_pane"
      Name: " "
    Slot:
      '4':
        Material: "player_head"
        Head: "{player}"
        Name: "&b&lReporting &f{player}"
        Lore:
          - "&7Pick what {player} did wrong."
        Option: false
      '11':
        Material: "diamond_sword"
        Name: "&b&lHacking"
        Reason: "Hacking"
        Lore:
          - "&7Kill aura, reach, fly, speed or"
          - "&7any other unfair advantage."
          - ""
          - "&e» Click to report {player}"
        Option: true
```

| Setting | What it does |
|---|---|
| `ReportMenu.Title` | The menu's title. 1.8 cuts titles at 32 characters |
| `ReportMenu.Size` | Number of slots: 9, 18, 27, 36, 45 or 54 |
| `ReportMenu.Items.Fill` | The item that fills every empty slot |
| `ReportMenu.Items.Slot.<slot>` | An item in that slot, see [Items](#items) |
| `Option` | `true` makes the item a reason that sends the report when clicked. `false` makes it display only |

`{player}` is the reported player's name, and works in the title and in every item's name, lore and `Head`.

## Ban-Reasons.yml and Mute-Reasons.yml
Both files work the same way.

```yaml
Title: "&8Ban » Pick a reason"
Size: 45
Fill:
  Material: "black_stained_glass_pane"
  Name: " "
Info:
  Slot: 4
  Material: "book"
  Name: "&c&lBan Reasons"
  Lore:
    - "&7Pick the rule this player broke."
Items:
  Slot:
    '11':
      Material: "diamond_sword"
      Name: "&c&lHacking"
      Reason: "Hacking"
      Lore:
        - "&7Kill aura, reach, fly, speed or"
        - "&7any other unfair client or mod."
        - ""
        - "&8Recorded as: &fHacking"
        - "&e» Click to select"
```

| Setting | What it does |
|---|---|
| `Title` | The menu's title. 1.8 cuts titles at 32 characters |
| `Size` | Number of slots: 18, 27, 36, 45 or 54 |
| `Fill` | The item that fills every empty slot |
| `Info` | An item that explains the menu and does nothing when clicked. `Slot` sets where it goes. Leave the section out for none |
| `Items.Slot.<slot>` | A reason in that slot, see [Items](#items). Every item here is a reason |

The bottom row holds the **Back** and **Next** buttons in its corners, coloured light blue. Don't put reasons in the first or last slot of the bottom row, as the buttons take their place.

## Adding and removing reasons
- **To add a reason,** add an item under `Slot` with a free slot number.
- **To remove one,** delete its section. It won't come back.
- **To start again from the defaults,** delete the file and restart.
