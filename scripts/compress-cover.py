"""Compress a release cover so it fits upload limits (CurseForge/Patreon, max 2 MB).

Usage: python scripts/compress-cover.py <covers-dir> <version>

Reads <covers-dir>/gpt/<version>.png (original from ChatGPT) and writes
<covers-dir>/comprimidas/<version>.png: a 256-color PNG, which keeps the art intact
at a fraction of the size. Falls back to JPEG if the PNG is still too large.
"""
import sys
from pathlib import Path

from PIL import Image

MAX_BYTES = 2 * 1024 * 1024


def main() -> None:
    if len(sys.argv) != 3:
        sys.exit(__doc__)

    covers = Path(sys.argv[1])
    version = sys.argv[2]
    src = covers / "gpt" / f"{version}.png"
    out_dir = covers / "comprimidas"

    if not src.is_file():
        sys.exit(f"Original not found: {src}")

    out_dir.mkdir(exist_ok=True)
    image = Image.open(src).convert("RGB")

    out = out_dir / f"{version}.png"
    image.quantize(colors=256, method=Image.Quantize.FASTOCTREE, dither=Image.Dither.FLOYDSTEINBERG).save(
        out, optimize=True
    )

    if out.stat().st_size > MAX_BYTES:
        out.unlink()
        out = out_dir / f"{version}.jpg"
        for quality in (90, 85, 80, 75):
            image.save(out, quality=quality, optimize=True, progressive=True)
            if out.stat().st_size <= MAX_BYTES:
                break

    size_kb = out.stat().st_size / 1024
    print(f"{src.name}: {src.stat().st_size / 1024:.0f} KB -> {out.name}: {size_kb:.0f} KB ({out})")
    if out.stat().st_size > MAX_BYTES:
        sys.exit("Still above 2 MB")


if __name__ == "__main__":
    main()
