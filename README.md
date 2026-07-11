# Momentum Blade

Adds the **Momentum Knife** — a throwing knife that deals more damage the farther it
travels before hitting a target. Snipe distant enemies for massive damage, or keep it
as a weak close-range poke.

## How it works

- Craft with 2 iron ingots stacked over a stick (yields 2 knives).
- Right-click to "throw" the knife. It raycasts up to 24 blocks in the direction you're
  aiming.
- The farther the ray travels before it hits a living entity, the more damage it deals
  (base 2.0, scaling up to ~15+ at max range).
- Missing (hitting a wall or nothing) still consumes the knife but on a shorter cooldown.

---

## Building the mod for FREE with GitHub (no Java install needed)

You do **not** need Java, Gradle, or any developer tools on your computer. GitHub will
build the `.jar` for you automatically and for free.

### Steps

1. **Create a GitHub account** at https://github.com (free).
2. Click the **+** in the top-right → **New repository**. Give it any name and click
   **Create repository**.
3. On the new empty repo page, click the link **"uploading an existing file"**.
4. **Extract the downloaded zip** of this mod on your computer.
5. **IMPORTANT (macOS users):** the `.github` folder is **hidden/invisible** by default
   in Finder. Press **Cmd + Shift + .** (period) in Finder to show hidden files. If you
   skip this, the build workflow will NOT be uploaded and the build will **never run**.
6. Open the extracted folder and **select ALL files and folders from INSIDE it**
   (including the hidden `.github` folder). Do **not** drag the outer folder itself —
   drag its **contents**.
7. Drag everything into the GitHub upload area.
8. Scroll down and click **Commit changes**.
9. Click the **Actions** tab at the top of your repo.
10. Wait about **2 minutes** for the build to finish (green checkmark).
11. Click the completed build → scroll to **Artifacts** → download **mod-jar**.
12. Unzip it and copy the `.jar` file into your `.minecraft/mods/` folder.

### Requirements to run

- Minecraft **1.20.1**
- **Fabric Loader** 0.15.11+
- **Fabric API** (download from Modrinth/CurseForge for 1.20.1)

Enjoy your momentum-powered throws!