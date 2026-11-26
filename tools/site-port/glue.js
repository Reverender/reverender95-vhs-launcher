// Frame driver: the background half of the site's animate() plus the post renderer's yl(),
// with browser plumbing (DOM, timers, adaptive quality) removed.
let continuousScroll = 0;
let smoothBass = 0;
let smoothMid = 0;
let smoothHigh = 0;
let glitchIntensity = 0;
let wpPostScroll = 0;
let rrWpPrevId = -1;

function rrWallpaperTrigger(e) {
  glitchIntensity = Math.min(1.3, Math.max(glitchIntensity, e));
}

// once per frame, before any drawing (site animate(): smoothing + scroll)
function rrAudioFrame(d) {
  let C = 0, E = 0, k = 0;
  for (let q = 0; q < 16; q++) C += freqData[q];
  for (let q = 16; q < 64; q++) E += freqData[q];
  for (let q = 64; q < 128; q++) k += freqData[q];
  smoothBass += (C / (16 * 255) - smoothBass) * 0.16;
  smoothMid += (E / (48 * 255) - smoothMid) * 0.16;
  smoothHigh += (k / (64 * 255) - smoothHigh) * 0.16;
  continuousScroll += d * (0.7 + smoothBass * 0.35);
}
function rrSmooth(i) { return i === 0 ? smoothBass : i === 1 ? smoothMid : i === 2 ? smoothHigh : continuousScroll; }

function rrWallpaperFrame(bgCtx, P, I, y, d, wp, c, L) {
  const _ = I * 0.56 + mouseY * 10, B = P * 0.5 + mouseX * 18;
  if (L >= 2 || wp === 14 || wp === 16 || wp === 18 || wp === 19 || wp === 20 || wp === 21 || wp === 22 || wp === 23) {
    rrPostFrame(bgCtx, P, I, y, d, wp, c, L);
    glitchIntensity = 0;
    return;
  }
  const U = bgCtx.createLinearGradient(0, 0, 0, _);
  U.addColorStop(0, c.sky[0]);
  U.addColorStop(0.55, c.sky[1]);
  U.addColorStop(0.82, c.sky[2]);
  U.addColorStop(0.95, c.sky[3]);
  U.addColorStop(1, c.sky[4]);
  bgCtx.fillStyle = U;
  bgCtx.fillRect(0, 0, P, I);
  if (bgLayers.starWarp) {
    bgCtx.fillStyle = "#ffffff";
    STARS.forEach((V, D) => {
      if (L === 1 && D % 2) return;
      V.z -= d * (80 + smoothBass * 260);
      if (V.z <= 10) {
        V.z = 1e3;
        V.x = (Math.random() - 0.5) * 2e3;
        V.y = (Math.random() - 0.5) * 1200;
      }
      const K = B + (V.x / V.z) * 500, $ = _ * 0.45 + (V.y / V.z) * 500;
      if (K > 0 && K < P && $ > 0 && $ < _) {
        const z = Math.max(0.8, (1 - V.z / 1e3) * 2.8);
        bgCtx.fillRect(K, $, z, z);
      }
    });
  }
  switch (wp) {
    case 0: renderScene0_SunsetOutrun(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 1: renderScene1_CyberCity(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 2: renderScene2_HeliosMall(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 3: renderScene3_TwinMoonOcean(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 4: renderScene4_FujiCanyon(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 5: renderScene5_HyperAtrium(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 6: renderScene6_ArcadeOdyssey(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 7: renderScene7_PoseidonSpa(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 8: renderScene8_ChronoDrift(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 9: renderScene9_BackroomsLevel0(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 10: renderScene10_BackroomsHalls(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 11: renderScene11_PoolroomsSpa(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 12: renderScene12_PoolroomsAbyss(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 13: renderScene13_NeonRainAlley(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 15: renderScene15_ChromeCathedral(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 17: renderScene17_LiminalAtrium(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 24: renderScene24_AetherSpires(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 25: renderScene25_PrismCitadel(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 26: renderScene26_CelestialAtrium(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 27: renderScene27_TidalVault(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
    case 28: renderScene28_ChronosphereHall(bgCtx, P, I, B, _, y, continuousScroll, smoothBass, smoothMid, c); break;
  }
  if (L < 2) renderExtraBackgroundDepth(bgCtx, P, I, y, smoothBass, smoothMid, smoothHigh, c, B, _);
  if (bgLayers.cyberRain) {
    bgCtx.strokeStyle = c.primary;
    RAIN_DROPS.forEach((V, D) => {
      if (L === 1 && D % 2) return;
      V.y += d * (0.42 + smoothHigh * 0.9) * V.z;
      if (V.y > 1) {
        V.y = -0.05;
        V.x = Math.random();
      }
      const K = V.x * P, $ = V.y * I;
      bgCtx.globalAlpha = V.z * 0.5;
      bgCtx.lineWidth = V.z * 1.8;
      bgCtx.beginPath();
      bgCtx.moveTo(K, $);
      bgCtx.lineTo(K - mouseX * 4, $ + V.len * V.z);
      bgCtx.stroke();
    });
    bgCtx.globalAlpha = 1;
  }
  rrRipples(bgCtx, c);
  if (glitchIntensity > 0.02) {
    const V = glitchIntensity;
    glitchIntensity *= 0.8;
    bgCtx.save();
    const D = 4 + Math.floor(V * 4);
    for (let K = 0; K < D; K++) {
      const $ = Math.random() * I;
      const z = 12 + Math.random() * 48;
      const X = (Math.random() - 0.5) * 68 * V;
      bgCtx.shiftBand(0, $, P, z, X);
    }
    bgCtx.globalCompositeOperation = "screen";
    for (let K = 0; K < 3; K++) {
      const $ = Math.random() * I;
      const z = 4 + Math.random() * 18;
      bgCtx.fillStyle = K % 2 === 0 ? `rgba(${c.waveRgb}, ${(0.35 * V).toFixed(3)})` : `rgba(${c.altWaveRgb}, ${(0.35 * V).toFixed(3)})`;
      bgCtx.fillRect(0, $, P, z);
    }
    bgCtx.restore();
  } else {
    glitchIntensity = 0;
  }
}

function rrRipples(e, M) {
  for (let q = clickRipples.length - 1; q >= 0; q--) {
    const S = clickRipples[q];
    S.r += 5;
    S.alpha -= 0.025;
    if (S.alpha > 0) {
      e.strokeStyle = "rgba(" + M.waveRgb + ", " + S.alpha.toFixed(3) + ")";
      e.lineWidth = 2;
      e.beginPath();
      e.arc(S.x, S.y, S.r, 0, Math.PI * 2);
      e.stroke();
    } else clickRipples.splice(q, 1);
  }
  e.globalAlpha = 1;
}

function rrAddRipple(x, y) {
  clickRipples.push({ x: x, y: y, r: 0, alpha: 1 });
}

// rr-wallpaper-renderer.js: yl()
function rrPostFrame(D, h, M, _, R, a, k, s) {
  const b = s >= 2;
  const f = smoothBass, r = smoothMid, y = smoothHigh;
  wpPostScroll += Math.min(0.05, Math.max(0.001, R)) * (1.55 + f * 1.4);
  const Q = wpPostScroll;
  const v = h * 0.5 + mouseX * 18;
  const w = M * 0.52 + mouseY * 10;
  if (a === 20 || (b && (a === 0 || a === 4 || a === 24))) {
    wp_ol(D, h, M, v, w, _, Q, f, r, k);
  } else if (a === 21 || (b && (a === 1 || a === 6 || a === 8 || a === 13 || a === 16 || a === 28))) {
    wp_al(D, h, M, v, w, _, Q, f, r, k);
  } else if (a === 22 || (b && (a === 2 || a === 5 || a === 9 || a === 10 || a === 15 || a === 17 || a === 25 || a === 26))) {
    wp_el(D, h, M, v, w, _, Q, f, r, k);
  } else if (b && a === 11) {
    wp_j(D, h, M, v, w, _, Q, f, r, k, false);
  } else if (b && a === 12) {
    wp_j(D, h, M, v, w, _, Q, f, r, k, true);
  } else if (a === 23 || b) {
    wp_j(D, h, M, v, w, _, Q, f, r, k, false);
  } else if (a === 14) {
    wp_Y(D, h, M, v, w, _, Q, f, r, k);
  } else if (a === 16) {
    wp_z(D, h, M, v, w, _, Q, f, r, k);
  } else if (a === 18) {
    wp_x(D, h, M, v, w, _, Q, f, r, k);
  } else if (a === 19) {
    wp_ll(D, h, M, v, w, _, Q, f, r, k);
  }
  if (s < 2) {
    renderExtraBackgroundDepth(D, h, M, _, f, r, y, k, v, w);
    if (bgLayers.cyberRain) {
      D.strokeStyle = k.primary;
      D.beginPath();
      for (var g = 0; g < RAIN_DROPS.length; g++) {
        var m = RAIN_DROPS[g];
        m.y += R * (0.42 + y * 0.9) * m.z;
        if (m.y > 1) {
          m.y = -0.05;
          m.x = Math.random();
        }
        if (s === 1 && g % 2) continue;
        var dd = m.x * h, yy = m.y * M;
        D.globalAlpha = m.z * (s === 1 ? 0.28 : 0.5);
        D.lineWidth = m.z * 1.8;
        D.beginPath();
        D.moveTo(dd, yy);
        D.lineTo(dd - mouseX * 4, yy + m.len * m.z);
        D.stroke();
      }
      D.globalAlpha = 1;
    }
    rrRipples(D, k);
  }
}

function rrThemeByKey(key) {
  return THEMES[key] || THEMES.vaporwave;
}

function rrSetLayer(key, on) {
  bgLayers[key] = on;
}
