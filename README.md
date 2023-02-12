# Fancy Holograms (fabric)

Make fancy holograms with commands using the new text display entities.

Current minecraft version: **23w06a**.

Requires [Fabric API](https://modrinth.com/mod/fabric-api).

## Command syntax

### Create a new hologram
/hologram create (name) (text)<br>
Example: /hologram create myHologram "This is a very cool hologram"

### Remove a hologram
/hologram remove (name)<br>
Example: /hologram remove myHologram

### Edit a hologram
/hologram edit (name) (property) (value)<br>
Examples:
- /hologram edit myHologram text "This is the new text"
- /hologram edit myHologram position ~ ~5 ~
- /hologram edit myHologram background red
- /hologram edit myHologram billboard fixed
- /hologram edit myHologram scale 5.0
