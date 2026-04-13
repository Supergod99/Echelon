#!/usr/bin/env python3
import json
import re
from collections import OrderedDict, defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ATTR_ROOT = ROOT / "forge/src/main/resources/data/tiered/item_attributes"
TOOLTIP_FILE = ROOT / "forge/src/main/resources/assets/tiered/tooltips/tooltip_borders.json"
LANG_FILE = ROOT / "forge/src/main/resources/assets/tiered/lang/en_us.json"
REPORT_FILE = ROOT / "build/standalone-reforge-id-map.json"

ALLOWED_NS = {"minecraft", "tiered", "attributeslib"}
FORBIDDEN_TIERED = {"generic.summon_health", "generic.ars_spell_power"}
ID_RE = re.compile(
    r"^(common|uncommon|rare|epic|legendary|mythic)_(armor|tool|melee|ranged|fishing|shield|elytra|staffwand)_(\d+)$"
)
REFORGE_RE = re.compile(
    r"^tiered:(common|uncommon|rare|epic|legendary|mythic)_(armor|tool|melee|ranged|fishing|shield|elytra|staffwand)_(\d+)$"
)
SPECIAL = {
    "tiered:limestone_chunk",
    "tiered:pyrite_chunk",
    "tiered:galena_chunk",
    "tiered:charoite",
    "tiered:crown_topaz",
    "tiered:painite",
    "tiered:perfect_border",
    "tiered:perfect",
    "tiered:apex_border",
    "tiered:perfect_star_border",
}


def load(path):
    return json.loads(path.read_text(encoding="utf-8"), object_pairs_hook=OrderedDict)


def save(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=4, ensure_ascii=False) + "\n", encoding="utf-8")


def attr_ok(attr):
    raw = attr.get("type", "")
    ns, item = raw.split(":", 1) if ":" in raw else ("minecraft", raw)
    return ns in ALLOWED_NS and not (ns == "tiered" and item in FORBIDDEN_TIERED)


def replace_strings(value, id_map):
    if isinstance(value, str):
        return id_map.get(value, value)
    if isinstance(value, list):
        return [replace_strings(v, id_map) for v in value]
    if isinstance(value, dict):
        return OrderedDict((k, replace_strings(v, id_map)) for k, v in value.items())
    return value


def main():
    candidates = []
    delete_entries = []
    removed = set()
    rewritten = set()

    for file in sorted(ATTR_ROOT.rglob("*.json")):
        data = load(file)
        old_id = data["id"]
        path = old_id.split(":", 1)[1]
        match = ID_RE.match(path)
        original = list(data.get("attributes", []))
        kept = [a for a in original if attr_ok(a)]
        if not match or not kept:
            removed.add(old_id)
            delete_entries.append({"file": file, "old": old_id})
            continue
        if len(kept) != len(original):
            rewritten.add(old_id)
        data["attributes"] = kept
        candidates.append(
            {
                "file": file,
                "data": data,
                "old": old_id,
                "tier": match.group(1),
                "kind": match.group(2),
                "number": int(match.group(3)),
            }
        )

    grouped = defaultdict(list)
    for entry in candidates:
        grouped[(entry["file"].parent, entry["tier"], entry["kind"])].append(entry)

    id_map = OrderedDict()
    kept_entries = []
    for key in sorted(grouped.keys(), key=lambda k: (str(k[0]), k[1], k[2])):
        for new_num, entry in enumerate(sorted(grouped[key], key=lambda e: e["number"]), start=1):
            new_path = f"{entry['tier']}_{entry['kind']}_{new_num}"
            entry["new"] = f"tiered:{new_path}"
            entry["new_file"] = entry["file"].parent / f"{new_path}.json"
            id_map[entry["old"]] = entry["new"]
            kept_entries.append(entry)

    for entry in delete_entries:
        entry["file"].unlink()

    for entry in kept_entries:
        data = replace_strings(entry["data"], id_map)
        data["id"] = entry["new"]
        for attr in data.get("attributes", []):
            modifier = attr.get("modifier")
            if isinstance(modifier, dict) and "name" in modifier:
                modifier["name"] = entry["new"]
        save(entry["new_file"], data)

    kept_ids = set(id_map.values())
    tooltip = load(TOOLTIP_FILE)
    for tooltip_entry in tooltip.get("tooltips", []):
        out = []
        seen = set()
        for raw in tooltip_entry.get("decider", []):
            mapped = id_map.get(raw, raw)
            keep = mapped in kept_ids or mapped in SPECIAL or not REFORGE_RE.match(mapped)
            if keep and mapped not in seen:
                out.append(mapped)
                seen.add(mapped)
        tooltip_entry["decider"] = out
    save(TOOLTIP_FILE, tooltip)

    lang = load(LANG_FILE)
    new_lang = OrderedDict()
    for key, value in lang.items():
        if key.startswith("tiered:") and key.endswith(".label") and REFORGE_RE.match(key[:-6]):
            mapped = id_map.get(key[:-6])
            if mapped:
                new_lang[f"{mapped}.label"] = value
            continue
        if key.startswith("tooltip.tiered.apex_effect.mythic_armor_"):
            parts = key.split(".")
            mapped = id_map.get("tiered:" + parts[4])
            if mapped:
                parts[4] = mapped.split(":", 1)[1]
                new_lang[".".join(parts)] = value
            continue
        new_lang[key] = value
    save(LANG_FILE, new_lang)

    REPORT_FILE.parent.mkdir(parents=True, exist_ok=True)
    REPORT_FILE.write_text(
        json.dumps(
            {"kept": len(kept_entries), "removed": sorted(removed), "rewritten": sorted(rewritten), "id_map": id_map},
            indent=2,
        )
        + "\n",
        encoding="utf-8",
    )
    print(f"kept={len(kept_entries)} removed={len(removed)} rewritten={len(rewritten)}")
    print(f"report={REPORT_FILE}")


if __name__ == "__main__":
    main()
