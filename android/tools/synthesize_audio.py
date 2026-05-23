"""
Pre-render the Web Audio SFX from index.html:889-984 as 16-bit mono WAVs.
Pure Python stdlib (no numpy/scipy). Matches the web's:
  - sawtooth oscillator
  - biquad lowpass at 2kHz, Q=2
  - exponential gain ramp (0.12 -> 0.001 over the note duration)
"""
import math
import struct
import wave
import os
import random

SR = 44100  # sample rate

def sawtooth(freq, duration):
    n = int(duration * SR)
    out = [0.0] * n
    for i in range(n):
        phase = (i * freq / SR) % 1.0
        out[i] = 2 * phase - 1  # sawtooth: -1 to +1
    return out

def biquad_lowpass(samples, cutoff, q):
    """Direct-form II biquad lowpass, matching Web Audio's BiquadFilterNode."""
    omega = 2 * math.pi * cutoff / SR
    cos_o = math.cos(omega)
    sin_o = math.sin(omega)
    alpha = sin_o / (2 * q)
    b0 = (1 - cos_o) / 2
    b1 = 1 - cos_o
    b2 = (1 - cos_o) / 2
    a0 = 1 + alpha
    a1 = -2 * cos_o
    a2 = 1 - alpha
    # normalize
    b0 /= a0; b1 /= a0; b2 /= a0
    a1 /= a0; a2 /= a0
    out = [0.0] * len(samples)
    x1 = x2 = y1 = y2 = 0.0
    for i, x in enumerate(samples):
        y = b0*x + b1*x1 + b2*x2 - a1*y1 - a2*y2
        x2 = x1; x1 = x
        y2 = y1; y1 = y
        out[i] = y
    return out

def exp_envelope(samples, start, end=0.001):
    """Exponential gain ramp like Web Audio's exponentialRampToValueAtTime."""
    n = len(samples)
    if n == 0: return samples
    ratio = end / start
    out = [0.0] * n
    for i in range(n):
        t = i / max(1, n - 1)
        out[i] = samples[i] * start * (ratio ** t)
    return out

def note(freq, duration, volume=0.12):
    raw = sawtooth(freq, duration)
    filtered = biquad_lowpass(raw, 2000, 2)
    return exp_envelope(filtered, volume)

def silence(duration):
    return [0.0] * int(duration * SR)

def concat(*chunks):
    return [s for c in chunks for s in c]

def mix(*chunks_with_offset):
    """Mix multiple sample chunks at given offsets (in samples)."""
    total_len = max(off + len(c) for c, off in chunks_with_offset)
    out = [0.0] * total_len
    for chunk, offset in chunks_with_offset:
        for i, s in enumerate(chunk):
            if offset + i < total_len:
                out[offset + i] += s
    return out

def write_wav(path, samples):
    # Soft-clip to prevent harsh distortion
    peak = max(abs(s) for s in samples) if samples else 1.0
    scale = 0.95 / peak if peak > 0.95 else 1.0
    with wave.open(path, 'wb') as wf:
        wf.setnchannels(1)
        wf.setsampwidth(2)
        wf.setframerate(SR)
        frames = bytearray()
        for s in samples:
            v = int(s * scale * 32767)
            if v > 32767: v = 32767
            elif v < -32768: v = -32768
            frames += struct.pack('<h', v)
        wf.writeframes(bytes(frames))
    print(f"  wrote {path} ({len(samples)/SR:.1f}s, {os.path.getsize(path)/1024:.0f}KB)")

# --- The four SFX (web: index.html:925-957) ----------------------------
# Each "playNote" call followed by setTimeout's of 50/100ms or 200ms or 120ms.
# Notes overlap because the JS doesn't await — so we mix at those offsets.

print("Rendering SFX...")

# swipeLeft: 392→330→294 at 0/50/100ms
left = mix(
    (note(392, 0.15, 0.12), 0),
    (note(330, 0.15, 0.10), int(0.05 * SR)),
    (note(294, 0.20, 0.08), int(0.10 * SR)),
)
write_wav('sfx_swipe_left.wav', left)

# swipeRight: 392→494→587 at 0/50/100ms
right = mix(
    (note(392, 0.15, 0.12), 0),
    (note(494, 0.15, 0.10), int(0.05 * SR)),
    (note(587, 0.20, 0.08), int(0.10 * SR)),
)
write_wav('sfx_swipe_right.wav', right)

# gameOver: 5 notes at 200ms apart
go_chunks = []
for i, f in enumerate([392, 370, 330, 294, 262]):
    go_chunks.append((note(f, 0.4, 0.12), int(i * 0.20 * SR)))
write_wav('sfx_game_over.wav', mix(*go_chunks))

# victory: 6 notes at 120ms apart
vc_chunks = []
for i, f in enumerate([392, 440, 494, 523, 587, 659]):
    vc_chunks.append((note(f, 0.25, 0.12), int(i * 0.12 * SR)))
write_wav('sfx_victory.wav', mix(*vc_chunks))

# --- Ambient music (web: index.html:963-985) ---------------------------
# 32-note pattern, 1.2s notes, 800-1200ms random gaps, volume 0.06.
# Pre-render as a single ~48s loop. Use a fixed seed for reproducibility.
print("Rendering ambient music loop...")
random.seed(1788)  # the year of the Grand Tour

base_notes = [
    196, 247, 294, 330, 392, 330, 294, 247,
    220, 277, 330, 370, 440, 370, 330, 277,
    185, 233, 277, 330, 370, 330, 277, 233,
    196, 247, 294, 370, 392, 370, 294, 247,
]

music_chunks = []
offset_sec = 0.5
for f in base_notes:
    music_chunks.append((note(f, 1.2, 0.06), int(offset_sec * SR)))
    offset_sec += 0.8 + random.random() * 0.4
# Add a half-second of silence at the end so the loop wraps cleanly
total_dur = offset_sec + 0.5
music = mix(*music_chunks)
# Pad to total_dur if needed
target_len = int(total_dur * SR)
if len(music) < target_len:
    music += [0.0] * (target_len - len(music))

write_wav('music_ambient.wav', music)

print("\nDone.")
