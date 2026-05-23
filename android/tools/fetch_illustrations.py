#!/usr/bin/env python3
"""
Fetch period engravings from Wikimedia Commons for The Grand Tour cards.

Reads tools/illustrations.json, downloads images via the Commons API,
saves them as JPGs in app/src/main/res/drawable-nodpi/, and deletes the
corresponding vector placeholder in res/drawable/ when the bitmap lands.

Requirements: Python 3.6+ (stdlib only — no pip installs).

Usage (run from android/ directory):
    python3 tools/fetch_illustrations.py
    python3 tools/fetch_illustrations.py --only rome
    python3 tools/fetch_illustrations.py --dry-run

Each entry has a list of candidate titles tried in order. If none
resolves, the script falls back to a Commons search query. Edit
illustrations.json and rerun to swap any specific illustration.
"""
from __future__ import annotations

import argparse
import json
import os
import sys
import time
import urllib.parse
import urllib.request

API = "https://commons.wikimedia.org/w/api.php"
USER_AGENT = "GrandTour/0.1 (https://github.com/alejandroguerrero-oecd/Grand-tour-game)"
THUMB_WIDTH = 800  # px — Android scales down automatically

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
ANDROID_DIR = os.path.dirname(SCRIPT_DIR)
RES_DIR = os.path.join(ANDROID_DIR, "app", "src", "main", "res")
BITMAP_DIR = os.path.join(RES_DIR, "drawable-nodpi")
VECTOR_DIR = os.path.join(RES_DIR, "drawable")
MANIFEST_PATH = os.path.join(SCRIPT_DIR, "illustrations.json")


def api_call(params: dict) -> dict:
    params = {**params, "format": "json", "formatversion": "2"}
    url = API + "?" + urllib.parse.urlencode(params)
    req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    with urllib.request.urlopen(req, timeout=20) as r:
        return json.loads(r.read())


def resolve_title(title: str) -> str | None:
    """Look up a Commons file title; return its thumbnail URL or None if missing."""
    if not title.lower().startswith("file:"):
        title = "File:" + title
    try:
        data = api_call({
            "action": "query",
            "titles": title,
            "prop": "imageinfo",
            "iiprop": "url",
            "iiurlwidth": str(THUMB_WIDTH),
            "redirects": "1",
        })
    except Exception as e:
        print(f"    ! API error: {e}")
        return None
    pages = data.get("query", {}).get("pages", [])
    if not pages or pages[0].get("missing"):
        return None
    info = pages[0].get("imageinfo", [{}])[0]
    return info.get("thumburl") or info.get("url")


def search_first(query: str) -> str | None:
    """Search Commons; return the thumbnail URL of the first File: match."""
    try:
        data = api_call({
            "action": "query",
            "list": "search",
            "srnamespace": "6",  # File namespace
            "srsearch": query,
            "srlimit": "5",
        })
    except Exception as e:
        print(f"    ! search error: {e}")
        return None
    hits = data.get("query", {}).get("search", [])
    for hit in hits:
        url = resolve_title(hit["title"])
        if url:
            return url
    return None


def download(url: str, dest: str) -> bool:
    try:
        req = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
        with urllib.request.urlopen(req, timeout=30) as r, open(dest, "wb") as out:
            out.write(r.read())
        size = os.path.getsize(dest)
        print(f"    ✓ {os.path.basename(dest)} ({size // 1024} KB)")
        return True
    except Exception as e:
        print(f"    ! download failed: {e}")
        return False


def process_entry(key: str, entry: dict, dry: bool) -> bool:
    print(f"  {key}:")
    print(f"    {entry.get('credit', '(no credit)')}")
    titles = entry.get("titles", [])
    url = None
    for t in titles:
        print(f"    trying: {t}")
        url = resolve_title(t)
        if url:
            print(f"    -> resolved")
            break
        time.sleep(0.1)
    if not url and entry.get("search"):
        q = entry["search"]
        print(f"    falling back to search: {q}")
        url = search_first(q)
    if not url:
        print(f"    ✗ no candidate worked for {key}")
        return False
    if dry:
        print(f"    [dry-run] would download {url}")
        return True
    os.makedirs(BITMAP_DIR, exist_ok=True)
    dest = os.path.join(BITMAP_DIR, f"{key}.jpg")
    if not download(url, dest):
        return False
    vector = os.path.join(VECTOR_DIR, f"{key}.xml")
    if os.path.exists(vector):
        os.remove(vector)
        print(f"    removed placeholder {os.path.basename(vector)}")
    return True


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("--only", help="city to fetch (e.g. rome). Default: all.")
    p.add_argument("--dry-run", action="store_true", help="resolve only, don't write")
    args = p.parse_args()

    with open(MANIFEST_PATH) as f:
        manifest = json.load(f)
    cities = [args.only] if args.only else [k for k in manifest if not k.startswith("_")]

    total = success = 0
    for city in cities:
        if city not in manifest:
            print(f"unknown city: {city}")
            continue
        print(f"\n[{city}]")
        for key, entry in manifest[city].items():
            total += 1
            if process_entry(key, entry, args.dry_run):
                success += 1

    print(f"\n{success}/{total} illustrations resolved.")
    return 0 if success == total else 1


if __name__ == "__main__":
    sys.exit(main())
