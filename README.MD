# 🛡️ IP Whitelist

A lightweight, server-side Fabric mod designed for extra security, especially useful for **cracked servers**. Instead of just whitelisting usernames (which can be changed), this mod locks access to specific **IP addresses** so only your friends can join ;).

By default, the mod is **disabled** when first installed to prevent you from locking yourself out. Join your server, then run `/ipwhitelist add <yourname>` followed by `/ipwhitelist on` to secure your server.

## Features

- **IP-Based Security:** Blocks connections before the player even joins the world.
- **Cracked Friendly:** Doesn't rely on UUIDs or official Mojang accounts.
## Commands

All commands require OP.

|**Command**|**Description**|
|---|---|
|`/ipwhitelist on`|Enables the IP check. Players not on the list will be kicked.|
|`/ipwhitelist off`|Disables the check. Anyone can join.|
|`/ipwhitelist add <player>`|Grabs the IP of an **online** player and adds it to the list.|
|`/ipwhitelist addip <ip>`|Manually adds a raw IP address (e.g., `1.2.3.4`) to the list.|
|`/ipwhitelist remove <player>`|Removes the IP of an online player from the list.|
|`/ipwhitelist removeip <ip>`|Removes a specific IP from the list (supports tab-completion).|
|`/ipwhitelist list`|Displays all currently whitelisted IP addresses.|
|`/ipwhitelist reload`|Forces a refresh of the config and whitelist files from the disk.|
## Configuration

The mod creates a folder at `/config/ipwhitelist/` with two files:

1. **`ip-whitelist.txt`**: A simple list of allowed IPs (one per line). You can edit this manually and run `/ipwhitelist reload`.
2. **`ip-whitelist-config.properties`**: Stores the `enabled=true/false` state.


## Motivation behind this

My motivation behind this mod is the f\*\*\*ers at omgur who join cracked servers for fun to destroy them, with this mod you should be safe. 

Also all other auth mods (EasyAuth and that's it lol) are overly complicated for what a simple whitelist should be.
