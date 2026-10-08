#!/usr/bin/env bash
# Publishes docs/wiki/*.md to the GitHub wiki repository.
# Usage: scripts/sync-wiki.sh <version>
# Run only after the release tag was pushed: the wiki documents what is on master.
set -euo pipefail

VERSION="${1:?usage: scripts/sync-wiki.sh <version>}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
WIKI_URL="https://github.com/gabriellaurindo/althera.wiki.git"
WORK_DIR="$(mktemp -d)"
trap 'rm -rf "$WORK_DIR"' EXIT

git clone -q "$WIKI_URL" "$WORK_DIR/wiki"

# Pages removed from docs/wiki are removed from the wiki as well.
find "$WORK_DIR/wiki" -maxdepth 1 -name '*.md' -delete
cp "$ROOT"/docs/wiki/*.md "$WORK_DIR/wiki/"

cd "$WORK_DIR/wiki"
git add -A
if git diff --cached --quiet; then
    echo "Wiki already up to date."
    exit 0
fi

git commit -q -m "Update wiki for v${VERSION}"
git push -q origin HEAD
echo "Wiki updated for v${VERSION}."
