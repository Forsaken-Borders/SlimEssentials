# Fabrissentials
Fabrissentials is a mod aiming to add essential commands to Fabric servers.
Essential commands include, for example, /home, /warp, /tpr, /mail, and so on.

## Permissions
In the future, we'd like to support LuckPerms as an optional dependency, but all commands can also be used with vanilla operator levels,
which can be checked in the 'ops.json' file in your server directory.
You can find an explanation of the operator levels below.

- Operators with level 1 can make changes in the otherwise protected spawn area.
- Operators with level 2 can edit command blocks, use the commands /clear, /difficulty, /effect, /gamemode, /gamerule, /give, /summon, /setblock and /tp and perform all cheat commands, except those of level 3 and 4.
- Operators with level 3 can execute server commands for player sanctions (/kick and /ban) and for appointing further operators (/op and /deop).
- Operators with level 4 can use commands for server management, such as the command /stop to stop the server.

## Commands
| Commands  | Implementation status |
|-----------|-----------------------|
| /home     | ✅                     |
| /sethome  | ✅                     |
| /unban    | ✅                     |
| /unban-ip | ✅                     |
