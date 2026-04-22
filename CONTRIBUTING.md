# Contribution Guide

# ⚠️ SOLO DEVELOPMENT FOR NOW ⚠️

---

## Branch Structure

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

### Features or Updates

{mod version} - {Title} {type: feature/hotfix or both}

{Detailed description of changes}

---

### Hotfix for Specific Versions

{mod version} - {Title} hotfix - {forge/fabric}-{version}-{specific-version (if applicable)}

{Detailed description of changes}

---

## Questions or Suggestions

If you have any questions or suggestions, feel free to reach out!