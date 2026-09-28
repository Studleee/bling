# Bling

Jewelry for Minecraft 26.3 (Fabric). Make earrings, chains, bracelets, and watches at a jeweler's bench, wear them in three new inventory slots, and show them off in a display case.

- **Jewelry slots:** three slots next to your player in the inventory: ears, neck, and wrist. They also appear on the creative inventory tab. Right-click a piece to put it on (it swaps with whatever you're wearing), or drop it into its slot. Shift-click moves jewelry straight to its slot.
- **Visible:** whatever you wear shows on your player, on both wide and slim skins.
- **Materials:** iron, gold, copper, or netherite, with an optional diamond, emerald, or amethyst. Netherite jewelry survives lava.
- **Pieces:**
  - **Earrings:** metal hoops, with or without a gem.
  - **Stud earrings:** a gem on a small metal setting. Always need a gem.
  - **Chain:** with or without a gem pendant.
  - **Iced out chain:** a thick chain covered in diamonds, with a big diamond pendant.
  - **Bracelet:** with or without a gem.
  - **Watch:** its face shows the real in-game time, like a clock. Wearing one puts a small clock in the corner of your screen.
  - **Iced out watch:** a watch with a diamond bezel and band. Also tells the time.
- **Jeweler's bench:** put ingots in the metal slot (and a gem if you want one), pick a piece, and take it out. Each button previews what you'd get, and hovering one shows what it costs.
- **Jewelry display case:** a wooden counter with red velvet under a glass top. Right-click to open it and put in up to 4 pieces. They lie on the velvet and spread out to fit however many there are. Breaking the case drops it and its jewelry.
- **Dying:** your jewelry drops with the rest of your things, unless `keepInventory` is on.

**Costs at the bench**

```
Earrings:        1 ingot  (+ optional gem)
Stud earrings:   1 ingot  + 1 gem
Chain:           3 ingots (+ optional gem)
Iced out chain:  3 ingots + 6 diamonds
Bracelet:        2 ingots (+ optional gem)
Watch:           2 ingots
Iced out watch:  2 ingots + 4 diamonds
```

**Crafting**

```
Jeweler's Bench:        I G I      I = iron ingot, G = gold ingot
                        P P P      P = any planks
                        P   P
Jewelry Display Case:   G G G      G = glass
                        P R P      R = red wool
                        P P P      P = any planks
```

## Quick start

Double-click `run.bat`, or run it from a terminal in this folder. Minecraft opens with the mod loaded, and everything shows up in the **Bling** creative tab. The first launch downloads Minecraft and takes a few minutes.

## Where things live

```
src/main/java/com/bling/
  jewelry/Piece.java              the pieces: slot, ingot cost, gem use
  jewelry/Metal.java, Gem.java    materials
  jewelry/Worn.java               what a player is wearing (saved and synced)
  item/JewelryItem.java           right-click to wear
  menu/JewelersBenchMenu.java     the bench's crafting rules
  block/JewelryDisplayCase*.java  the display case and its storage
  mixin/                          the inventory slots and dropping jewelry on death

src/client/java/com/bling/client/
  render/JewelryModels.java       the 3D jewelry on the player
  render/JewelryLayer.java        draws it
  render/JewelryDisplayCaseRenderer.java  jewelry on the velvet
  screen/                         the bench and display case screens
  WatchHud.java                   the on-screen clock
```

All textures, models, recipes, and names come from `tools/gen-bling.ps1`. Edit the colors or pixel maps there and run:

```
powershell -ExecutionPolicy Bypass -File tools\gen-bling.ps1
```

## Sharing the mod

Run `build.bat`. The mod is `build/libs/bling-1.0.0.jar`. Players need Fabric Loader and Fabric API for Minecraft 26.3, plus the jar in their `mods` folder.
