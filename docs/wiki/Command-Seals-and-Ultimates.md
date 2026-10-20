# Command Seals & Ultimates

Command Seals and Ultimates are active abilities you trigger for your Hero. Each one has its own key and costs mana.

All keys can be changed in **Options → Controls**, under the **Althera** category.

---

## 🖥️ Skill HUD

Next to the hotbar (right side) there is one slot per skill, labeled with its key (**J**, **K**, **V**, **U**):

- **◆◆◆** above the seal slots: Command Seal charges left today
- **Gold border + seconds**: the skill is active (e.g. Overdrive's remaining time)
- **Light overlay + seconds**: the skill is on cooldown
- **Grayed out**: not enough mana, or no Command Seal charges left
- **Red ✕** on an Ultimate: already used today

The HUD is hidden together with the Hero HUD (hero screen settings) and with **F1**.

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
Clare and Valka use Crimson Requiem. To see how your Hero's Ultimate works, open the hero screen (**G**) and click
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

### 🗡 Crimson Requiem (Clare, Valka)

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

## 🔗 Related Pages

- [Core Systems](Core-Systems)
- [Commands](Commands)
- [Creating Custom Ultimates](Creating-Custom-Ultimates)
