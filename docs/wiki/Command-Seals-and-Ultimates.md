# Command Seals & Ultimates

Command Seals and Ultimates are active abilities you trigger for your Hero. Each one has its own key and costs mana.

All keys can be changed in **Options → Controls**, under the **Althera** category.

---

## 🖥️ Skill HUD

A **golden sword** next to your Hero's portrait in the Hero HUD shows the Ultimate: bright when it is still available
today, faded once it has been used. Command Seal charges left today show in the Command Seal wheel (hold **R**).

### ⏱️ Skill timers

Every Command Seal or Ultimate that is running shows up as a small see-through square with its icon and the seconds
left, next to the crosshair:

- **Green**: active (e.g. Overdrive's remaining time); the green fill drains as it runs out
- **Red**: on cooldown; the red fill drains until the skill is ready again

Icons: Overdrive 🔥 blaze powder, Revive 🗿 totem of undying, Heal 🍉 glistering melon, Ultimate ⭐ nether star.
Active timers come first. Prefer them under the Hero HUD? Change **Skill Timers Position** in the Althera config
(**Options → Mods → Althera → Config**).

The HUD is hidden together with the Hero HUD (hero screen settings) and with **F1**.

---

## 🖐 Seal key (R)

One key gives orders to your Hero:

| Action | What it does |
|---|---|
| **Tap R** | Marks the creature or player under your crosshair as your Hero's target |
| **Hold R** | Opens the **Command Seal wheel** |

### 🎯 Mark a target

Tap **R** while looking at a creature or player (up to **32 blocks**, in line of sight). Your summoned Hero attacks it,
ignoring other fights, until it dies, gets more than **48 blocks** away or **30 seconds** pass.

- Marking is free (no mana, no charges)
- Only you see the mark (a burst of sparks and a chime)
- It needs a summoned Hero
- Marking a player only works where your Hero could hurt them: PvP on and not on your team
- Tapping at nothing does nothing

### 🔘 Command Seal wheel

Hold **R** to open a wheel with **Overdrive**, **Revive** and **Heal**. Point at a seal with the mouse and **release R**
to cast it. Releasing in the centre (or pressing **Esc**) cancels.

Each seal shows its mana cost and its state: ready, active, on cooldown or blocked (no charges, not enough mana,
Hero summoned or not, Hero not defeated for Revive). You can't walk while the wheel is open.

The wheel is the only way to cast a seal. The Ultimate stays on **U** and is not on the wheel.

---

## ✋ Command Seal tattoo

Every player has a Command Seal tattoo on the back of the right hand, in a random colour. Casting a Command Seal or
your Hero's Ultimate makes it glow in that colour, with sparks and a chime, and raises your hand in first person.
Other players see the tattoo and the glow too.

Don't like the colour? `/althera_seal color` rolls a new one.

---

## 🔱 Command Seals

Command Seals are limited orders given to your Hero.

### Charges

- You have **3 Command Seal charges per day**
- Each use consumes **1 charge** plus the seal's mana cost
- Charges are restored at the start of a new day (sleeping through the night also counts)
- Each seal has a short cooldown after use

A seal only spends mana and charges if it can actually be used. If a requirement is not met, you receive a message and nothing is consumed.

---

### ⚡ Overdrive

| Key | Mana | Requires summoned Hero |
|---|---|---|
| **J** | 100 | Yes |

For **30 seconds**, your Hero gains:
- **Strength I**
- **Resistance I**
- **Speed I**

⚠️ Overdrive has a price: when it ends — or if the Hero is dismissed before it ends — the Hero is **defeated**.
After an Overdrive, the Hero **cannot be revived** until it has fully recovered in spirit form.

---

### ✨ Revive

| Key | Mana | Requires summoned Hero |
|---|---|---|
| **K** | 150 | No (the Hero must be defeated) |

Revives a **defeated** Hero at full health and summons it next to you.
Summoning the revived Hero does not cost any extra mana.

Revive cannot be used if:
- The Hero is not defeated
- The Hero was defeated by Overdrive and has not fully recovered yet

---

### 💚 Heal

| Key | Mana | Requires summoned Hero |
|---|---|---|
| **V** | 50 | Yes |

Fully restores the summoned Hero's health.

---

## 💥 Ultimates

Ultimates are the Hero's strongest abilities. Each Hero has its own Ultimate (**U**): Aria and Zaya use Explosion,
Clare uses Crimson Requiem and Valka uses Tempest Charge. To see how your Hero's Ultimate works, open the hero screen (**G**) and click
**Ultimate**: it shows its name, key, mana cost, channel time, whether it is ready today and a short description.
Datapacks can create new ones: see [Creating Custom Ultimates](Creating-Custom-Ultimates).

- The Ultimate can be used **once per day**
- Ultimates are restored at the start of a new day (sleeping through the night also counts)
- Ultimates do not use Command Seal charges
- A disguised Hero can't use its Ultimate (see [Disguise](Core-Systems#-disguise))

---

### 🔥 Explosion (Aria, Zaya)

| Key | Mana | Requires summoned Hero |
|---|---|---|
| **U** | 200 | Yes |

The Hero slows down and plays its casting animation (if its model has one) while channeling for about
**1 second**, then explodes:
- Damages every nearby creature within **6 blocks** (except you)
- Sets them on fire
- Knocks them back

If the Hero is defeated or dismissed while channeling, the explosion is cancelled.

---

### 🗡 Crimson Requiem (Clare)

| Key | Mana | Requires summoned Hero |
|---|---|---|
| **U** | 200 | Yes |

Clare stands still over a crimson mist with her magic circle open for about **1.5 seconds**, then dashes **8 blocks**
in a straight line towards where **you** are looking (the creature or block under your crosshair; aim while she
channels), stopping before walls:
- Deals heavy damage to every creature in her path (except you)
- Inflicts **Wither II** for 4 seconds
- Leaves a crimson trail behind her

If Clare is defeated or dismissed while channeling, the attack is cancelled.

---

### 🌀 Tempest Charge (Valka)

| Key | Mana | Requires summoned Hero |
|---|---|---|
| **U** | 200 | Yes |

Valka braces her lance over a teal gale for about **1.5 seconds**, then charges **8 blocks** in a straight line
towards where **you** are looking (aim while she charges up), stopping before walls:
- Deals heavy damage to every creature in her path (except you)
- Throws them aside with a strong knockback
- Leaves a teal trail behind her

If Valka is defeated or dismissed while channeling, the attack is cancelled.

---

## 🔗 Related Pages

- [Core Systems](Core-Systems)
- [Commands](Commands)
- [Creating Custom Ultimates](Creating-Custom-Ultimates)
