# Contribution Guide

# ⚠️ SOLO DEVELOPMENT FOR NOW ⚠️

---

## Branch Structure

> **Note (temporary):** for now all work happens on `feature-initial`, which is merged into `master` through a
> pull request. The `release` branch is skipped until the flow below is needed again.

### `master`
- Main branch containing the **latest features/content**.
- **Does not guarantee** the most up-to-date Forge or Minecraft version.

### `release`
- Branch for features with **content currently under development**.

### `feature-{name}` / `hotfix-{name}`
- Branches for developing new features or fixing bugs based on `release`.
- **Example**: `feature-servos`

### `forge-{version}` / `fabric-{version}`
- Main branches used when necessary for specific versions of Forge/Fabric/NeoForge.
- These branches are used when newer features need to be adapted to older versions.
- **Example**: `forge-1.21.3`

### `hotfix-forge-{version}` / `hotfix-fabric-{version}`
- Branches for bug fixes in Forge/Fabric/NeoForge branches.
- **Example**: `hotfix-forge-1.21.3`

---

## Development Rules

1. **New features** should be implemented in feature branches, merged into `release`, and later `release` should be merged into `master` to ensure a stable version with all available content.

2. Whenever possible, merge `master` into the main branches (`forge-{version}` / `fabric-{version}`) using an intermediate branch:
    - Name: `merge-master-{forge/fabric}-{version}`
    - Create it from the target main branch (forge/fabric) and merge `master` into it
    - This branch is used to fix issues before the final merge, ensuring the main branches remain stable during adjustments

3. **Create Forge/Fabric main branches** from `master`.

4. **Keep the changelog updated**.

---

## Version-Specific Structure

### More Detailed Versions
If necessary, create a more specific version for main branches:
- **Format**: `{forge/fabric}-{version}-{specific-version}`
- **Example**: `forge-1.21.3-53.0.19`

Hotfix follows the same rule:
- **Example**: `hotfix-forge-1.21.3-53.0.19`

**Important**: **Never** merge any main branch (`forge` or `fabric`) into `master`.

---

## Release Tags

### Tag Format

althera-{mod-version}-{forge/fabric}-{version}-{specific-version (if applicable)}

---

### Examples

- `althera-0.0.0-forge-1.21.3`
- `althera-0.0.0-forge-1.21.3-53.0.19`

---

### Versioning Rules

- **Minor features**: increment the second number  
  Example: `althera-0.1.0-forge-1.21.3`

- **Major or breaking updates**: increment the first number  
  Example: `althera-1.0.0-forge-1.21.3`

- **Bug fixes**: increment the third number  
  Example: `althera-0.0.1-forge-1.21.3`

**Note**: All main branches should share the same first and second numbers as `master` (if updated), but the third number may vary due to specific fixes.

---

## Importance of the Changelog

The changelog allows easy tracking of which features are present in each Forge/Fabric version. For example:

- If the mod version is `1.1.0` for Forge `1.21.3`, all related versions will be `1.1.x`.

---

## Changelog Structure

`CHANGELOG.md` follows this layout:

```
## [Unreleased]

### Added
### Changed
### Fixed
```

- Every change is added under `[Unreleased]` while developing.
- On release, `[Unreleased]` becomes `## [{mod version}] - {YYYY-MM-DD} - {Title} ({feature/hotfix or both})`
  and a new empty `[Unreleased]` is added above it.
- Hotfixes for specific versions use the same header with `hotfix - {forge/fabric}-{version}-{specific-version}`.

---

## Release Flow

Claude Code skills automate this flow: `/init-desen` prepares the work branch and `/publish` runs steps 1–5
(optionally starting from the work branch, reconciling the changelog and opening/merging the PR).

1. Merge the work branch into `master` through a pull request.
2. On `master`, cut the release: version line in `CHANGELOG.md`, `mod_version` bump,
   notes in `release-notes/{version}/` (`github.md`, `curseforge.md`, `discord.md`, `patreon.md`), wiki pages in
   `docs/wiki/`, build check, `Release {version}` commit and tag.
3. Push `master` and the tag. The tag triggers `.github/workflows/release.yml`, which builds the jar and publishes:
   - GitHub Release (notes from `github.md` + jar)
   - CurseForge (needs secret `CURSEFORGE_TOKEN` and variable `CURSEFORGE_PROJECT_ID`)
   - Discord announcement (needs secret `DISCORD_WEBHOOK`)
4. Publish the wiki with `scripts/sync-wiki.sh {version}`.
5. Post `patreon.md` on Patreon (manual: Patreon has no API for posts).

---

## Questions or Suggestions

If you have any questions or suggestions, feel free to reach out!