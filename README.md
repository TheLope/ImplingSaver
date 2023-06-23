# Impling Saver

Impling Saver will switch out the right click "open" option on Impling Jars to "use" if the corresponding Clue Scroll has been found in your inventory.

### Limitations
* Without the "one tick cooldown" option enabled multiple Impling Jars can be opened in a single tick. Due to the limitations of the RuneLite API the plugin cannot detect inventory changes this fast resulting in Impling Jars possibly being opened after a clue was found.
* With the "one tick cooldown" option enabled and while spam clicking it is possible the first 2 Impling Jars of an inventory are opened in one tick due to the clicking starting right before a tick ends.
* With the "one tick cooldown" option enabled and while spam clicking it is possible a single Impling Jar is opened after a clue has been found due to an "open" click already being registered before the right options were switched.