# Ritual System

The ritual **swaps your Hero** for another one. You offer it a diamond block, and optionally a **Hero Catalyst** to
choose the class and rank of the Hero that answers.

---

## 🧱 The Summoning Circle

Place a **Ritual Core**: it draws a glowing summoning circle on the ground around it, 5 x 5 blocks. Place a
**Ritual Pedestal** on each of its **four points**, two blocks away from the core, one on each side:

```
. . P . .
. . . . .
P . C . P
. . . . .
. . P . .
```

`C` = Ritual Core, `P` = Ritual Pedestal, `.` = must stay **empty** (air) so the circle stays visible.

- Each point lights up once it holds a pedestal, so the circle shows you where the missing ones go.
- When all four pedestals are in place and the rest of the circle is clear, the whole circle glows and pulses, and
  the crystal above the core lights up.
- Any block on the circle breaks the structure.

---

## 🛠 Crafting

**Ritual Core**

```
 . Y .
 G O G
 D D D
```

**Ritual Pedestal**

```
 G A G
 . D .
 D D D
```

`Y` = amethyst block, `A` = amethyst shard, `G` = gold ingot, `O` = obsidian, `D` = polished deepslate, deepslate
bricks or deepslate tiles (they can be mixed).

---

## 🏺 Offerings

A Ritual Pedestal holds a single item, floating and slowly turning above it.

- **Right-click** a pedestal with an item to place one.
- **Right-click** it again to take it back.
- Breaking a pedestal drops what it holds.

The ritual takes its ingredients from the pedestals, in any order:

| Offering | Result |
|---|---|
| Diamond block | A random Hero, different from your current one (rank chances apply) |
| Diamond block + Hero Catalyst | A random Hero **of the catalyst's class and rank**, different from your current one |

Only one catalyst can be offered at a time. Today the ritual uses two of the four pedestals; the other two are kept
for future ingredients.

---

## 💠 Hero Catalyst

Catalysts are dropped by yellow and red Nobles (see [Nobles](Nobles#-reward)): each one holds the **class and
rank** of the Hero that served the Noble (S from a yellow Noble, EX from a red one).

- Its name shows the class ("Saber Catalyst") in the rank's colour, and its tooltip the rank.
- Each class has its own icon.
- The catalyst guarantees the class and rank; which Hero of them answers is still random.
- A catalyst whose class no longer exists (its datapack was removed) becomes a **Faded Catalyst** and the ritual
  refuses it.

---

## 🔮 Mana

Every ritual costs **300 mana**. Your max mana starts at 200 and grows while you regenerate, so a new player has to
grow it past 300 first. Players in creative mode skip the cost.

---

## ✨ Performing the Ritual

1. Place the offerings on the pedestals.
2. **Right-click the Ritual Core**.
3. The ritual **channels for three seconds**: the circle brightens, its inner sigil spins faster and the offerings
   rise from their pedestals into the core.
4. It ends in a burst of light: the offerings and the mana are spent and your new Hero answers.

Stay within **8 blocks** of the core while it channels. Walking away, taking an offering back, breaking a pedestal or
blocking the circle interrupts the ritual **at no cost**. Only one ritual can channel on a core at a time.

### What changes

- Your current Hero leaves; your **Hero level and XP carry over** to the new one.
- The new Hero starts at full health. Summon it as usual.
- Without a Hero, the ritual gives you your first one.

---

## ⚠️ When the Ritual Fails

Nothing is spent when the ritual fails, and each failure tells you what is missing:

| Message | Fix |
|---|---|
| Place a Ritual Pedestal on each of the four points of the circle and keep the circle clear. | Complete the circle |
| The ritual needs a diamond block offered on a ritual pedestal. | Offer a diamond block |
| The ritual accepts a single catalyst. | Take the extra catalysts back |
| The faded catalyst no longer answers the ritual. | Use another catalyst |
| No other `<rank>` `<class>` answers the ritual. | No other Hero of that class and rank exists: use another catalyst |
| No other hero answers the ritual. | There is no other Hero to roll |
| Your mana is too weak to sustain the ritual (max X/300). | Regenerate mana to grow your max mana |
| Not enough mana for the ritual (X/300). | Wait for your mana to refill |

---

## 🔗 Related Pages

- [Core Systems](Core-Systems)
- [Nobles](Nobles)
- [Heroes](Heroes)
