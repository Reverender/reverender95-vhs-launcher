  "use strict";
  function _(e) {
    var s = "" + String(e || "#000").trim();
    if (s.toLowerCase().indexOf("rgb") === 0 && s.indexOf("(") > 0) {
      var l = s.slice(s.indexOf("(") + 1, s.indexOf(")")).split(",").map(function (q) { return parseFloat(q); });
      return [l[0] | 0, l[1] | 0, l[2] | 0];
    }
    var h = "" + s.replace("#", "");
    if (h.length === 3) h = "" + h.charAt(0) + h.charAt(0) + h.charAt(1) + h.charAt(1) + h.charAt(2) + h.charAt(2);
    var i = parseInt(h.slice(0, 6), 16) || 0;
    return [(i >> 16) & 255, (i >> 8) & 255, i & 255];
  }
  function U(e, c, l) {
    e = _(e);
    c = _(c);
    return [
      e[0] + (c[0] - e[0]) * l,
      e[1] + (c[1] - e[1]) * l,
      e[2] + (c[2] - e[2]) * l,
    ];
  }
  function o(e, c) {
    return c == null
      ? "rgb(" + (e[0] | 0) + "," + (e[1] | 0) + "," + (e[2] | 0) + ")"
      : "rgba(" +
          (e[0] | 0) +
          "," +
          (e[1] | 0) +
          "," +
          (e[2] | 0) +
          "," +
          c +
          ")";
  }
  function V(e, c, l, i) {
    return o(U(e, c, l), i);
  }
  function B(e, c) {
    return o(_(e), c);
  }
  function se(e) {
    var c = e >>> 0 || 1;
    return function () {
      c ^= c << 13;
      c ^= c >>> 17;
      c ^= c << 5;
      return ((c >>> 0) % 1e5) / 1e5;
    };
  }
  function ke(e) {
    var c = new Uint8Array(512),
      l;
    for (l = 0; l < 256; l++) c[l] = l;
    for (l = 255; l > 0; l--) {
      var i = (e() * (l + 1)) | 0,
        g = c[l];
      c[l] = c[i];
      c[i] = g;
    }
    for (l = 0; l < 256; l++) c[l + 256] = c[l];
    function d(T, M) {
      return c[(c[T & 255] + M) & 511] / 255;
    }
    function f(T) {
      return T * T * (3 - 2 * T);
    }
    function D(T, M) {
      var w = Math.floor(T),
        A = Math.floor(M),
        N = T - w,
        z = M - A;
      var p = d(w, A),
        k = d(w + 1, A),
        W = d(w, A + 1),
        m = d(w + 1, A + 1),
        J = f(N),
        G = f(z);
      return p + (k - p) * J + (W - p) * G + (p - k - W + m) * J * G;
    }
    return function T(M, w, A) {
      var N = 0,
        z = 0.5,
        p = 1,
        k = 0;
      for (var W = 0; W < (A || 5); W++) {
        N += D(M * p, w * p) * z;
        k += z;
        z *= 0.5;
        p *= 2.03;
      }
      return N / k;
    };
  }
  function a(e, c, l, i, g, d) {
    var f = e.createLinearGradient(0, g == null ? 0 : g, 0, d == null ? l : d);
    i.forEach(function (D) {
      f.addColorStop(D[0], D[1]);
    });
    return f;
  }
  function we(e, c, l, i, g, d) {
    var f = e.createRadialGradient(c, l, 0, c, l, i);
    f.addColorStop(0, B(g, d));
    f.addColorStop(1, B(g, 0));
    e.fillStyle = f;
    e.fillRect(c - i, l - i, i * 2, i * 2);
  }
  function ie(e, c, l, i, g, d, f, D, T) {
    e.save();
    e.globalCompositeOperation = "lighter";
    for (var M = 0; M < g; M++) {
      var w = i() * c,
        A = i() * l,
        N = (f + i() * (D - f)) * Math.min(c, l),
        z = d[(i() * d.length) | 0],
        p = T * (0.35 + i() * 0.65);
      var k = e.createRadialGradient(w, A, N * 0.55, w, A, N);
      k.addColorStop(0, B(z, p * 0.55));
      k.addColorStop(0.86, B(z, p));
      k.addColorStop(1, B(z, 0));
      e.fillStyle = k;
      e.beginPath();
      e.arc(w, A, N, 0, Math.PI * 2);
      e.fill();
    }
    e.restore();
  }
  function S(e, c, l, i) {
    e.save();
    e.globalCompositeOperation = "lighter";
    e.lineCap = "round";
    for (var g = 0; g < i.lines; g++) {
      var d = g / (i.lines - 1),
        f = (d - 0.5) * i.spread * l;
      var D = Math.sin(d * Math.PI);
      e.strokeStyle = V(i.c1, i.c2, d, i.alpha * (0.25 + 0.75 * D));
      e.lineWidth = Math.max(1, i.width * l * (0.4 + D));
      e.beginPath();
      e.moveTo(-0.1 * c, i.y0 * l + f);
      e.bezierCurveTo(
        i.cx1 * c,
        i.cy1 * l + f * i.twist,
        i.cx2 * c,
        i.cy2 * l - f * i.twist,
        1.1 * c,
        i.y1 * l + f * 0.6,
      );
      e.stroke();
    }
    e.restore();
  }
  function Y(e, c, l, i, g) {
    var d = document.createElement("canvas");
    d.width = d.height = 96;
    var f = d.getContext("2d"),
      D = f.createImageData(96, 96),
      T = D.data;
    for (var M = 0; M < T.length; M += 4) {
      var w = (i() * 255) | 0;
      T[M] = T[M + 1] = T[M + 2] = w;
      T[M + 3] = (g * 255) | 0;
    }
    f.putImageData(D, 0, 0);
    e.save();
    e.globalCompositeOperation = "overlay";
    e.fillStyle = e.createPattern(d, "repeat");
    e.fillRect(0, 0, c, l);
    e.restore();
  }
  function ce(e, c, l, i, g) {
    var d = ke(i),
      f = Math.max(64, Math.round(c / 6)),
      D = Math.max(36, Math.round(l / 6));
    var T = document.createElement("canvas");
    T.width = f;
    T.height = D;
    var M = T.getContext("2d"),
      w = M.createImageData(f, D),
      A = w.data;
    var N = _(g.color),
      z = _(g.shade),
      p = g.scale || 3.2;
    for (var k = 0; k < D; k++) {
      var W = k / D,
        m = Math.max(0, Math.min(1, (W - g.y0) / (g.y1 - g.y0)));
      var J = Math.sin(m * Math.PI);
      for (var G = 0; G < f; G++) {
        var ne = G / f;
        var ae = d(ne * p * (c / l) * 0.6, W * p * 1.7, 6);
        var de = Math.max(0, (ae - (1 - g.cover)) / g.cover) * J;
        de = Math.min(1, de * 1.8);
        var q = d(ne * p * (c / l) * 0.6, (W - 0.03) * p * 1.7, 4);
        var H = Math.max(0, Math.min(1, (ae - q) * 4 + 0.35));
        var R = (k * f + G) * 4;
        A[R] = z[0] + (N[0] - z[0]) * H;
        A[R + 1] = z[1] + (N[1] - z[1]) * H;
        A[R + 2] = z[2] + (N[2] - z[2]) * H;
        A[R + 3] = de * 255 * (g.alpha == null ? 1 : g.alpha);
      }
    }
    M.putImageData(w, 0, 0);
    e.save();
    e.imageSmoothingEnabled = true;
    e.imageSmoothingQuality = "high";
    e.drawImage(T, 0, 0, c, l);
    e.restore();
  }
  function pe(e, c, l, i, g) {
    g.forEach(function (d) {
      e.beginPath();
      e.moveTo(0, l);
      var f = 80,
        D = i() * 10,
        T = i() * 10;
      for (var M = 0; M <= f; M++) {
        var w = M / f,
          A =
            d.y +
            Math.sin(w * d.f + D) * d.amp +
            Math.sin(w * d.f * 2.7 + T) * d.amp * 0.35;
        e.lineTo(w * c, A * l);
      }
      e.lineTo(c, l);
      e.closePath();
      e.fillStyle = a(
        e,
        c,
        l,
        [
          [0, d.top],
          [1, d.bot],
        ],
        (d.y - d.amp) * l,
        l,
      );
      e.fill();
      if (d.rim) {
        e.save();
        e.clip();
        e.globalCompositeOperation = "lighter";
        e.fillStyle = a(
          e,
          c,
          l,
          [
            [0, d.rim],
            [1, "rgba(0,0,0,0)"],
          ],
          (d.y - d.amp) * l,
          (d.y + 0.06) * l,
        );
        e.fillRect(0, 0, c, l);
        e.restore();
      }
    });
  }
  var v = [0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5];
  function P(e, c, l, i, g) {
    var d = e.createImageData(c, Math.max(1, i - l)),
      f = d.data,
      D = g.map(_),
      T = D.length - 1;
    for (var M = 0; M < i - l; M++) {
      var w = (M / Math.max(1, i - l - 1)) * T,
        A = Math.min(T - 1, Math.floor(w)),
        N = w - A;
      for (var z = 0; z < c; z++) {
        var p = N * 16 > v[(M & 3) * 4 + (z & 3)] + 0.5 ? D[A + 1] : D[A];
        var k = (M * c + z) * 4;
        f[k] = p[0];
        f[k + 1] = p[1];
        f[k + 2] = p[2];
        f[k + 3] = 255;
      }
    }
    e.putImageData(d, 0, l);
  }
  var oe = [
    {
      id: "w95-sky",
      name: "Daydream Sky",
      draw: function (e, c, l, i, g) {
        e.fillStyle = a(e, c, l, [
          [0, i.sky[1]],
          [0.45, i.sky[2]],
          [0.82, V(i.sky[3], "#ffffff", 0.25)],
          [1, V(i.sky[4], "#ffffff", 0.35)],
        ]);
        e.fillRect(0, 0, c, l);
        we(e, c * 0.72, l * 0.78, l * 0.75, i.sun[2], 0.35);
        ce(e, c, l, g, {
          y0: 0.05,
          y1: 1.05,
          color: V(i.sky[4], "#ffffff", 0.78),
          shade: V(i.sky[2], i.sky[3], 0.45),
          cover: 0.46,
          scale: 2.6,
        });
        ce(e, c, l, se(7), {
          y0: 0.45,
          y1: 1.15,
          color: "#ffffff",
          shade: V(i.sky[3], "#ffffff", 0.3),
          cover: 0.34,
          scale: 4.2,
          alpha: 0.85,
        });
        Y(e, c, l, g, 0.06);
      },
    },
    {
      id: "w95-dither",
      name: "Dither Sunset",
      pixel: 4,
      draw: function (e, c, l, i, g) {
        var d = Math.round(l * 0.62);
        P(e, c, 0, d, [i.sky[0], i.sky[1], i.sky[2], i.sky[3], i.sky[4]]);
        var f = c * 0.5,
          D = d - l * 0.02,
          T = l * 0.27;
        e.save();
        e.beginPath();
        e.arc(f, D, T, Math.PI, 0);
        e.clip();
        var M = document.createElement("canvas");
        M.width = Math.ceil(T * 2);
        M.height = Math.ceil(T);
        P(M.getContext("2d"), M.width, 0, M.height, [
          i.sun[2],
          i.sun[1],
          i.sun[0],
        ]);
        e.drawImage(M, f - T, D - T);
        e.fillStyle = B(i.sunSlice);
        for (var w = 0; w < 7; w++) {
          var A = D - T * 0.08 - w * T * 0.12,
            N = Math.max(1, (7 - w) * 0.34 * (l / 160));
          e.fillRect(f - T, Math.round(A), T * 2, Math.round(N));
        }
        e.restore();
        function z(de, q, H, R, le) {
          var K = se(H);
          e.beginPath();
          e.moveTo(0, d);
          var he = 0,
            ue = de;
          while (he <= c) {
            ue = de - K() * q;
            e.lineTo(Math.round(he), Math.round(ue));
            he += 3 + K() * (c / 24);
          }
          e.lineTo(c, d);
          e.closePath();
          e.fillStyle = B(R);
          e.fill();
          if (le) {
            e.strokeStyle = B(le);
            e.lineWidth = 1;
            e.stroke();
          }
        }
        z(d - 2, l * 0.13, 11, V(i.mtnBot, i.mtnTop, 0.35), null);
        z(d, l * 0.08, 23, i.mtnBot, i.mtnStroke);
        P(e, c, d, l, [i.floor[0], i.floor[1], i.floor[2]]);
        e.strokeStyle = B(i.primary);
        e.lineWidth = 1;
        for (var p = 0; p < 12; p++) {
          var k = Math.pow(p / 11, 2.1),
            W = Math.round(d + 1 + k * (l - d));
          e.beginPath();
          e.moveTo(0, W + 0.5);
          e.lineTo(c, W + 0.5);
          e.stroke();
        }
        for (var m = -14; m <= 14; m++) {
          e.beginPath();
          e.moveTo(f + m * 3, d);
          e.lineTo(f + m * c * 0.11, l);
          e.stroke();
        }
        var J = se(5);
        e.fillStyle = "#ffffff";
        for (var G = 0; G < 60; G++) {
          var ne = J() * c,
            ae = J() * d * 0.55;
          e.globalAlpha = 0.4 + J() * 0.6;
          e.fillRect(ne | 0, ae | 0, 1, 1);
        }
        e.globalAlpha = 1;
      },
    },
    {
      id: "w95-pattern",
      name: "Desktop Weave",
      pixel: 2,
      draw: function (e, c, l, i, g) {
        var d = U(i.floor[1], i.sky[2], 0.35),
          f = U(d, [255, 255, 255], 0.24),
          D = U(d, [0, 0, 0], 0.4);
        e.fillStyle = o(d);
        e.fillRect(0, 0, c, l);
        var T = document.createElement("canvas");
        T.width = T.height = 8;
        var M = T.getContext("2d");
        M.fillStyle = o(d);
        M.fillRect(0, 0, 8, 8);
        M.fillStyle = o(f);
        [
          [0, 0, 3, 1],
          [0, 0, 1, 3],
          [4, 4, 3, 1],
          [4, 4, 1, 3],
        ].forEach(function (m) {
          M.fillRect(m[0], m[1], m[2], m[3]);
        });
        M.fillStyle = o(D);
        [
          [1, 3, 3, 1],
          [3, 1, 1, 3],
          [5, 7, 3, 1],
          [7, 5, 1, 3],
        ].forEach(function (m) {
          M.fillRect(m[0], m[1], m[2], m[3]);
        });
        e.fillStyle = e.createPattern(T, "repeat");
        e.fillRect(0, 0, c, l);
        var w = Math.round(Math.min(c, l) * 0.24),
          A = Math.round(c / 2 - w / 2),
          N = Math.round(l / 2 - w / 2);
        e.fillStyle = "#000";
        e.fillRect(A + 3, N + 3, w, w);
        e.fillStyle = B(i.sky[1]);
        e.fillRect(A, N, w, w);
        e.fillStyle = "#fff";
        e.fillRect(A, N, w, 2);
        e.fillRect(A, N, 2, w);
        e.fillStyle = "rgba(0,0,0,.6)";
        e.fillRect(A, N + w - 2, w, 2);
        e.fillRect(A + w - 2, N, 2, w);
        var z = document.createElement("canvas");
        z.width = w - 8;
        z.height = w - 8;
        P(z.getContext("2d"), z.width, 0, z.height, [
          i.sky[1],
          i.sky[2],
          i.sky[3],
          i.sky[4],
        ]);
        e.drawImage(z, A + 4, N + 4);
        e.save();
        e.beginPath();
        e.rect(A + 4, N + 4, w - 8, w - 8);
        e.clip();
        e.fillStyle = B(i.sun[0]);
        e.beginPath();
        e.arc(A + w / 2, N + w * 0.72, w * 0.28, Math.PI, 0);
        e.fill();
        e.fillStyle = B(i.sky[1]);
        for (var p = 0; p < 4; p++)
          e.fillRect(
            A,
            Math.round(N + w * 0.66 - p * w * 0.07),
            w,
            1 + (p < 2 ? 1 : 0),
          );
        e.fillStyle = B(i.floor[1]);
        e.fillRect(A, Math.round(N + w * 0.72), w, w);
        e.strokeStyle = B(i.primary);
        e.lineWidth = 1;
        for (var k = 0; k < 4; k++) {
          var W =
            Math.round(N + w * 0.72 + Math.pow(k / 3, 1.8) * w * 0.24) + 0.5;
          e.beginPath();
          e.moveTo(A, W);
          e.lineTo(A + w, W);
          e.stroke();
        }
        e.restore();
      },
    },
    {
      id: "w95-setup",
      name: "Setup.exe",
      draw: function (e, c, l, i, g) {
        var d = U(i.sky[2], [0, 0, 160], 0.45),
          f = 24;
        for (var D = 0; D < f; D++) {
          e.fillStyle = o(U(d, [0, 0, 0], D / (f - 1)));
          e.fillRect(0, Math.floor((D * l) / f), c, Math.ceil(l / f) + 1);
        }
        var T = Math.round(Math.min(c * 0.075, l * 0.12));
        e.textBaseline = "top";
        e.font =
          "italic 900 " + T + 'px "Times New Roman", Times, Georgia, serif';
        e.fillStyle = "#000";
        e.fillText("REVERENDER 95", c * 0.045 + T * 0.06, l * 0.05 + T * 0.06);
        e.fillStyle = "#fff";
        e.fillText("REVERENDER 95", c * 0.045, l * 0.05);
        e.font =
          "italic 700 " +
          Math.round(T * 0.5) +
          'px "Times New Roman", Times, Georgia, serif';
        e.fillStyle = "#000";
        e.fillText("Setup", c * 0.045 + 3, l * 0.05 + T * 1.12 + 3);
        e.fillStyle = B(i.sun[2]);
        e.fillText("Setup", c * 0.045, l * 0.05 + T * 1.12);
        var M = c * 0.26,
          w = Math.max(14, l * 0.024),
          A = c - M - c * 0.05,
          N = l - w - l * 0.12;
        e.fillStyle = "#c0c0c0";
        e.fillRect(A - 3, N - 3, M + 6, w + 6);
        e.fillStyle = "#fff";
        e.fillRect(A - 3, N - 3, M + 6, 2);
        e.fillStyle = "#404040";
        e.fillRect(A - 3, N + w + 1, M + 6, 2);
        e.fillStyle = "#000080";
        for (var z = 0; z < 14; z++)
          e.fillRect(A + 2 + z * (M / 19), N + 2, M / 19 - 3, w - 4);
        e.font =
          Math.round(w * 0.9) + 'px Tahoma, "MS Sans Serif", Arial, sans-serif';
        e.fillStyle = "rgba(255,255,255,.75)";
        e.fillText("Copying files…  74%", A, N - w * 1.6);
      },
    },
    {
      id: "w95-stars",
      name: "Starfield.scr",
      pixel: 3,
      draw: function (e, c, l, i, g) {
        e.fillStyle = "#000";
        e.fillRect(0, 0, c, l);
        var d = c / 2,
          f = l / 2;
        for (var D = 0; D < 520; D++) {
          var T = g() * Math.PI * 2,
            M = Math.pow(g(), 0.6) * Math.hypot(c, l) * 0.55,
            w = d + Math.cos(T) * M,
            A = f + Math.sin(T) * M;
          var N = Math.max(0, M / (Math.hypot(c, l) * 0.55)) * 7,
            z = g() < 0.18 ? (g() < 0.5 ? i.primary : i.secondary) : "#ffffff";
          e.strokeStyle = B(z, 0.35 + (0.65 * M) / (c * 0.6));
          e.lineWidth = 1;
          e.beginPath();
          e.moveTo(Math.round(w), Math.round(A));
          e.lineTo(
            Math.round(w + Math.cos(T) * N),
            Math.round(A + Math.sin(T) * N),
          );
          e.stroke();
        }
      },
    },
    {
      id: "w95-pipes",
      name: "Pipes.scr",
      draw: function (e, c, l, i, g) {
        e.fillStyle = "#000";
        e.fillRect(0, 0, c, l);
        var d = Math.round(Math.min(c, l) / 11),
          f = Math.ceil(c / d) + 1,
          D = Math.ceil(l / d) + 1;
        var T = [i.primary, i.secondary, i.accent, i.sun[1], i.mtnTop];
        var M = d * 0.17,
          w = [
            [1, 0],
            [-1, 0],
            [0, 1],
            [0, -1],
          ];
        function A(q, H, R, le, K) {
          var he = H === le,
            ue = he
              ? e.createLinearGradient(0, H - M, 0, H + M)
              : e.createLinearGradient(q - M, 0, q + M, 0);
          ue.addColorStop(0, V(K, "#000", 0.65));
          ue.addColorStop(0.35, V(K, "#fff", 0.45));
          ue.addColorStop(0.55, B(K));
          ue.addColorStop(1, V(K, "#000", 0.75));
          e.fillStyle = ue;
          if (he) e.fillRect(Math.min(q, R), H - M, Math.abs(R - q), M * 2);
          else e.fillRect(q - M, Math.min(H, le), M * 2, Math.abs(le - H));
        }
        function N(q, H, R) {
          var le = e.createRadialGradient(
            q - M * 0.5,
            H - M * 0.5,
            M * 0.1,
            q,
            H,
            M * 1.45,
          );
          le.addColorStop(0, V(R, "#fff", 0.7));
          le.addColorStop(0.45, B(R));
          le.addColorStop(1, V(R, "#000", 0.7));
          e.fillStyle = le;
          e.beginPath();
          e.arc(q, H, M * 1.4, 0, Math.PI * 2);
          e.fill();
        }
        for (var z = 0; z < 10; z++) {
          var p = T[z % T.length],
            k = (g() * f) | 0,
            W = (g() * D) | 0,
            m = w[(g() * 4) | 0];
          for (var J = 0; J < 48; J++) {
            var G = 1 + ((g() * 4) | 0),
              ne = k + m[0] * G,
              ae = W + m[1] * G;
            A(k * d, W * d, ne * d, ae * d, p);
            N(k * d, W * d, p);
            k = ne;
            W = ae;
            if (k < 0 || W < 0 || k > f || W > D) break;
            var de = w[(g() * 4) | 0];
            if (de[0] !== -m[0] || de[1] !== -m[1]) m = de;
          }
          N(k * d, W * d, p);
        }
      },
    },
    {
      id: "w95-mystify",
      name: "Mystify.scr",
      draw: function (e, c, l, i, g) {
        e.fillStyle = "#000";
        e.fillRect(0, 0, c, l);
        e.globalCompositeOperation = "lighter";
        e.lineJoin = "round";
        [i.primary, i.secondary, i.accent].forEach(function (d, f) {
          var D = [],
            T = [];
          for (var M = 0; M < 4; M++) {
            D.push([g() * c, g() * l]);
            T.push([(g() - 0.5) * c * 0.022, (g() - 0.5) * l * 0.022]);
          }
          for (var w = 0; w < 18; w++) {
            e.strokeStyle = B(d, 0.12 + 0.88 * (w / 17));
            e.lineWidth = Math.max(1.2, Math.min(c, l) / 500);
            e.beginPath();
            D.forEach(function (A, N) {
              if (N) e.lineTo(A[0], A[1]);
              else e.moveTo(A[0], A[1]);
            });
            e.closePath();
            e.stroke();
            D.forEach(function (A, N) {
              A[0] += T[N][0];
              A[1] += T[N][1];
              if (A[0] < 0 || A[0] > c) T[N][0] *= -1;
              if (A[1] < 0 || A[1] > l) T[N][1] *= -1;
            });
          }
        });
        e.globalCompositeOperation = "source-over";
      },
    },
  ];
  var L = [
    {
      id: "lh-aurora",
      name: "Aurora Plex",
      accent: "jade",
      draw: function (e, c, l, i, g) {
        e.fillStyle = a(e, c, l, [
          [0, "#020a10"],
          [0.55, "#06222a"],
          [1, "#010507"],
        ]);
        e.fillRect(0, 0, c, l);
        we(e, c * 0.3, l * 0.62, c * 0.55, "#1aa38a", 0.22);
        S(e, c, l, {
          lines: 70,
          spread: 0.18,
          width: 0.0016,
          alpha: 0.1,
          c1: "#2df0b0",
          c2: "#3fa8ff",
          y0: 0.78,
          cx1: 0.28,
          cy1: 0.38,
          cx2: 0.62,
          cy2: 0.92,
          y1: 0.44,
          twist: 1.6,
        });
        S(e, c, l, {
          lines: 40,
          spread: 0.08,
          width: 0.0012,
          alpha: 0.12,
          c1: "#b6ffde",
          c2: "#2bd8c0",
          y0: 0.7,
          cx1: 0.35,
          cy1: 0.5,
          cx2: 0.7,
          cy2: 0.8,
          y1: 0.52,
          twist: 1.2,
        });
        var d = l * 0.705,
          f = e.createLinearGradient(0, 0, c, 0);
        f.addColorStop(0, "rgba(120,255,220,0)");
        f.addColorStop(0.45, "rgba(190,255,236,.9)");
        f.addColorStop(1, "rgba(120,255,220,0)");
        e.fillStyle = f;
        e.fillRect(0, d, c, Math.max(1, l * 0.0018));
        we(e, c * 0.45, d, c * 0.18, "#7dffd8", 0.18);
        Y(e, c, l, g, 0.05);
      },
    },
    {
      id: "lh-jade",
      name: "Jade Horizon",
      accent: "jade",
      draw: function (e, c, l, i, g) {
        e.fillStyle = a(e, c, l, [
          [0, "#0b2f2c"],
          [0.5, "#1d6a5c"],
          [0.66, "#7fd1b2"],
          [0.665, "#d9fff0"],
          [0.7, "#0f3b33"],
          [1, "#03100e"],
        ]);
        e.fillRect(0, 0, c, l);
        e.save();
        e.globalCompositeOperation = "lighter";
        for (var d = 0; d < 9; d++) {
          e.strokeStyle = "rgba(160,255,220," + (0.05 + d * 0.008) + ")";
          e.lineWidth = l * 0.002;
          e.beginPath();
          e.ellipse(
            c * 0.5,
            l * (1.4 + d * 0.02),
            c * (0.9 + d * 0.05),
            l * (0.75 + d * 0.01),
            0,
            Math.PI * 1.08,
            Math.PI * 1.92,
          );
          e.stroke();
        }
        e.restore();
        we(e, c * 0.5, l * 0.665, c * 0.45, "#b4ffe2", 0.32);
        for (var f = 0; f < 6; f++) {
          e.fillStyle = "rgba(255,255,255," + (0.025 + g() * 0.03) + ")";
          e.save();
          e.translate(c * (0.1 + f * 0.17), 0);
          e.rotate(-0.35);
          e.fillRect(0, -l, c * 0.035, l * 3);
          e.restore();
        }
        Y(e, c, l, g, 0.05);
      },
    },
    {
      id: "lh-slate",
      name: "Slate Panes",
      accent: "slate",
      draw: function (e, c, l, i, g) {
        e.fillStyle = a(e, c, l, [
          [0, "#2a3138"],
          [1, "#0b0e11"],
        ]);
        e.fillRect(0, 0, c, l);
        we(e, c * 0.75, l * 0.25, c * 0.6, "#6f8fae", 0.25);
        for (var d = 0; d < 9; d++) {
          var f = c * (0.18 + g() * 0.25),
            D = l * (0.3 + g() * 0.5),
            T = g() * c * 0.9 - f * 0.2,
            M = g() * l * 0.8 - D * 0.2;
          e.save();
          e.translate(T + f / 2, M + D / 2);
          e.rotate(-0.22 + g() * 0.1);
          var w = e.createLinearGradient(-f / 2, -D / 2, f / 2, D / 2);
          w.addColorStop(0, "rgba(200,225,245,.16)");
          w.addColorStop(0.5, "rgba(160,190,215,.05)");
          w.addColorStop(1, "rgba(200,225,245,.11)");
          e.fillStyle = w;
          e.strokeStyle = "rgba(220,240,255,.22)";
          e.lineWidth = 1.2;
          e.beginPath();
          if (true) e.roundRect(-f / 2, -D / 2, f, D, 12);
          else e.rect(-f / 2, -D / 2, f, D);
          e.fill();
          e.stroke();
          e.restore();
        }
        Y(e, c, l, g, 0.05);
      },
    },
    {
      id: "lh-ember",
      name: "Ember Glass",
      accent: "amber",
      draw: function (e, c, l, i, g) {
        e.fillStyle = a(e, c, l, [
          [0, "#120804"],
          [0.6, "#3a1806"],
          [1, "#080302"],
        ]);
        e.fillRect(0, 0, c, l);
        S(e, c, l, {
          lines: 60,
          spread: 0.2,
          width: 0.0015,
          alpha: 0.09,
          c1: "#ffb347",
          c2: "#ff5e2b",
          y0: 0.3,
          cx1: 0.3,
          cy1: 0.85,
          cx2: 0.65,
          cy2: 0.2,
          y1: 0.7,
          twist: 1.4,
        });
        ie(e, c, l, g, 26, ["#ffb35a", "#ff7b2e", "#ffd9a0"], 0.01, 0.05, 0.16);
        Y(e, c, l, g, 0.05);
      },
    },
  ];
function rrOsWallIds() {
  return oe.concat(L).map(function (w) { return w.id; });
}
function rrOsWallPixel(id) {
  var all = oe.concat(L);
  for (var i = 0; i < all.length; i++) if (all[i].id === id) return all[i].pixel || 0;
  return 0;
}
function rrOsWallRender(id, c, l, i, g, d) {
  var all = oe.concat(L), e = null;
  for (var k = 0; k < all.length; k++) if (all[k].id === id) e = all[k];
  if (!e) return 0;
  var f = e.pixel || 0,
    D = f ? 1 / f : d || 1;
  var T = Math.max(16, Math.round(l * D)),
    M = Math.max(9, Math.round(i * D));
  if (f && l < 400) {
    T = Math.max(32, Math.round(l / 2));
    M = Math.max(18, Math.round(i / 2));
  }
  c.width = T;
  c.height = M;
  var w = c.getContext("2d");
  w.save();
  e.draw(w, T, M, g, se(2654435769 ^ (e.id.length * 2654435761)));
  w.restore();
  return f;
}
