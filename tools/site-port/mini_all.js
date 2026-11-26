  const T = [
    "WAVEFORM SCOPE",
    "LED SPECTRUM + PEAKS",
    "MIRROR NEON RIBBON",
    "STEREO VU + PHASE",
    "CYBER MATRIX PULSE",
  ];
  let m = 3;
  let O = 0;
  const E = new Float32Array(32);
  let x = 0,
    k = 0;
function rrMiniOscClick() {
    m = (m + 1) % T.length;
    O = performance.now() + 1800;
    const r = document.getElementById("lcd-status");
    if (r) {
      r.textContent = `VIZ [${m + 1}/${T.length}]: ${T[m]}`;
    }
}
  function R(a, r) {
    const g = typeof freqData !== "undefined" && freqData ? freqData[a] : 0;
    if (g > 1) return g / 255;
    const n =
      (typeof isPlaying !== "undefined" && isPlaying) ||
      window.__retroEmuAudioActive ||
      window.__rrVideo95AudioActive;
    if (!n) return 0.04;
    return (
      0.18 +
      0.35 *
        Math.abs(Math.sin(r * 4.2 + a * 0.37)) *
        Math.abs(Math.cos(r * 2.7 - a * 0.19))
    );
  }
  function v(a, r) {
    const g =
      typeof timeData !== "undefined" && timeData && timeData.length
        ? timeData[a]
        : 128;
    if (Math.abs(g - 128) > 1) return (g - 128) / 128;
    const n =
      (typeof isPlaying !== "undefined" && isPlaying) ||
      window.__retroEmuAudioActive ||
      window.__rrVideo95AudioActive;
    if (!n) return 0;
    return (
      Math.sin(r * 7 + a * 0.22) * 0.45 + Math.cos(r * 13 - a * 0.11) * 0.25
    );
  }
  let W = 0;
  let C = 0;
  function rrMiniOscFrame(a) {
    const f = document.getElementById("mini-osc");
    if (!f) return;
    const t = f.getContext("2d");
    const r = null;
    if (
      false
    )
      return;
    const g =
      Number(window.__rrVisualQualityStage || 0) >= 1 ||
      document.body.classList.contains("rr-adaptive-performance")
        ? 50
        : 1e3 / 30;
    if (a - W < g) return;
    W = a;
    const n = Math.max(
      220,
      Math.round(
        C || f.width || 260,
      ),
    );
    const o = Math.max(32, Math.round(f.height || f.clientHeight || 32));
    if (f.width !== n) f.width = n;
    const u = a * 0.001;
    const h =
      typeof THEMES !== "undefined" && THEMES[currentThemeKey]
        ? THEMES[currentThemeKey]
        : { primary: "#00f7ff", secondary: "#ff00a0", accent: "#ffff00" };
    t.save();
    t.fillStyle = "#060c10";
    t.fillRect(0, 0, n, o);
    t.strokeStyle = "rgba(0, 247, 255, 0.09)";
    t.lineWidth = 1;
    t.beginPath();
    t.moveTo(0, o * 0.5);
    t.lineTo(n, o * 0.5);
    t.stroke();
    if (m === 0) {
      t.strokeStyle = h.secondary;
      t.lineWidth = 2.2;
      t.beginPath();
      const l = 64;
      for (let e = 0; e <= l; e++) {
        const s = (e / l) * n;
        const i = o * 0.5 + v(e * 2, u) * (o * 0.42);
        if (e === 0) t.moveTo(s, i + 1);
        else t.lineTo(s, i + 1);
      }
      t.stroke();
      t.strokeStyle = "#d8ffff";
      t.lineWidth = 1.5;
      t.beginPath();
      for (let e = 0; e <= l; e++) {
        const s = (e / l) * n;
        const i = o * 0.5 + v(e * 2, u) * (o * 0.42);
        if (e === 0) t.moveTo(s, i);
        else t.lineTo(s, i);
      }
      t.stroke();
    } else if (m === 1) {
      const l = 32;
      const e = n / l;
      const s = Math.max(2, e - 2);
      for (let i = 0; i < l; i++) {
        const M = Math.min(1, R(i * 2, u) * 1.15);
        if (M >= E[i]) E[i] = M;
        else E[i] = Math.max(0, E[i] - 0.012);
        const y = Math.round(M * (o - 3));
        const d = Math.round(i * e + 1);
        for (let p = o - 2; p > o - 2 - y; p -= 3) {
          const b = (o - p) / o;
          t.fillStyle =
            b > 0.78 ? h.accent : b > 0.45 ? h.secondary : h.primary;
          t.fillRect(d, p - 2, s, 2);
        }
        const w = Math.max(1, Math.round(o - 2 - E[i] * (o - 3)));
        t.fillStyle = "#ffffff";
        t.fillRect(d, w, s, 1.5);
      }
    } else if (m === 2) {
      const l = 48;
      [
        { scale: 0.46, col: h.secondary, alpha: 0.48, shift: 14 },
        { scale: 0.36, col: h.primary, alpha: 0.78, shift: 0 },
      ].forEach((e) => {
        t.fillStyle = e.col;
        t.globalAlpha = e.alpha;
        t.beginPath();
        t.moveTo(0, o * 0.5);
        for (let s = 0; s <= l; s++) {
          const i = (s / l) * n;
          const M = Math.abs(v(s * 2 + e.shift, u)) * o * e.scale;
          t.lineTo(i, o * 0.5 - M);
        }
        for (let s = l; s >= 0; s--) {
          const i = (s / l) * n;
          const M = Math.abs(v(s * 2 + e.shift, u)) * o * e.scale;
          t.lineTo(i, o * 0.5 + M);
        }
        t.closePath();
        t.fill();
      });
      t.globalAlpha = 1;
      t.fillStyle = "#ffffff";
      t.fillRect(0, o * 0.5 - 0.5, n, 1);
    } else if (m === 3) {
      let l = 0,
        e = 0;
      for (let c = 0; c < 16; c++) {
        l += R(c * 2, u);
        e += R(c * 2 + 1, u);
      }
      const s = Math.min(1, (l / 16) * 1.35);
      const i = Math.min(1, (e / 16) * 1.35);
      x = s >= x ? s : Math.max(0, x - 0.01);
      k = i >= k ? i : Math.max(0, k - 0.01);
      const M = n * 0.38;
      const y = 18;
      const d = (M - 14) / y;
      [
        { label: "L", y: 3, val: s, pk: x },
        { label: "R", y: o - 12, val: i, pk: k },
      ].forEach((c) => {
        t.fillStyle = h.primary;
        t.font = 'bold 9px "Courier New", monospace';
        t.fillText(c.label, 3, c.y + 8);
        const _ = Math.round(c.val * y);
        for (let S = 0; S < y; S++) {
          const A = 13 + S * d;
          t.fillStyle =
            S < _
              ? S > y * 0.78
                ? "#ff2a55"
                : S > y * 0.55
                  ? h.accent
                  : h.primary
              : "rgba(255,255,255,0.12)";
          t.fillRect(A, c.y, Math.max(2, d - 1.5), 8);
          const P = n - 13 - (S + 1) * d;
          t.fillRect(P, c.y, Math.max(2, d - 1.5), 8);
        }
      });
      const w = n * 0.5,
        p = o * 0.5,
        b = o * 0.42;
      t.strokeStyle = h.secondary;
      t.strokeRect(w - b * 1.3, 2, b * 2.6, o - 4);
      t.strokeStyle = "#aaffff";
      t.lineWidth = 1.3;
      t.beginPath();
      for (let c = 0; c < 36; c++) {
        const _ = v(c * 3, u);
        const S = v(c * 3 + 9, u + 0.2);
        const A = w + _ * b * 1.1;
        const P = p + S * b * 0.85;
        if (c === 0) t.moveTo(A, P);
        else t.lineTo(A, P);
      }
      t.stroke();
    } else if (m === 4) {
      const l = 20,
        e = 4;
      const s = n / l,
        i = o / e;
      const M = Math.floor(u * 8);
      for (let d = 0; d < l; d++) {
        const w = R(d * 3, u);
        for (let p = 0; p < e; p++) {
          const b = (d + p + M) % 3 === 0 ? w : w * 0.5;
          t.fillStyle = p < 2 ? h.secondary : h.primary;
          t.globalAlpha = Math.min(0.95, 0.12 + b * 0.88);
          t.fillRect(d * s + 1, p * i + 1, s - 2, i - 2);
        }
      }
      t.globalAlpha = 1;
      const y = 4 + R(1, u) * 10;
      t.strokeStyle = "#ffffff";
      t.lineWidth = 1.6;
      t.beginPath();
      t.moveTo(n * 0.5, o * 0.5 - y);
      t.lineTo(n * 0.5 + y * 1.6, o * 0.5);
      t.lineTo(n * 0.5, o * 0.5 + y);
      t.lineTo(n * 0.5 - y * 1.6, o * 0.5);
      t.closePath();
      t.stroke();
    }
    if (a < O) {
      const l = `${m + 1}/5 ${T[m]}`;
      t.font = 'bold 9px "Courier New", monospace';
      const e = t.measureText(l).width + 8;
      t.fillStyle = "rgba(0, 0, 0, 0.82)";
      t.fillRect(n - e - 2, 2, e, 13);
      t.strokeStyle = h.primary;
      t.strokeRect(n - e - 2, 2, e, 13);
      t.fillStyle = "#ffffff";
      t.fillText(l, n - e + 2, 11);
    }
    t.restore();
  }
  
function rrMiniOscMode() { return m; }
