// REVERENDER_95 Revy brain host shim: the page globals the site's Revy code uses, backed by the app (host.*).
var window = globalThis; window.window = window; var self = window;
var __vals = {};
function __call(name, args) { return host.call(name, JSON.stringify(args || [])); }
["restoreOrFocusWindow", "openAppFromStart", "closeWindow", "cycleWallpaper", "prevWallpaper", "cycleTheme", "prevTheme",
 "toggleCrtOverlay", "togglePlayPause", "playRandomTrackAndOffset", "randomizeAnimatedWallpaperSafe", "arrangeOpenWindowsSmart",
 "toggleAllVisualizers", "toggleFullScreen", "setBatteryPerformanceMode", "selectTrack", "revyOpenWebsiteInBrowser",
 "rrSearchYoutubeInVideo", "rrSearchYoutubeInVideo95"].forEach(function (n) {
  window[n] = function () { var r = __call(n, [].slice.call(arguments)); return r === "" ? undefined : r === "0" ? false : r; };
});
Object.defineProperty(window, "currentTrackIndex", { get: function () { return +host.get("track"); }, configurable: true });
Object.defineProperty(window, "batteryPerformanceMode", { get: function () { return host.get("battery") === "1"; }, configurable: true });
window.RRDesktopStyles = {
  state: { get style() { return host.get("style"); }, get accent() { var o = {}; o[host.get("style")] = host.get("accent"); return o; } },
  setStyle: function (s) { __call("setStyle", [s]); },
  setAccent: function (a) { __call("setAccent", [a]); },
};
var localStorage = {
  getItem: function (k) { var v = host.get("ls:" + k); return v === "\u0000" ? null : v; },
  setItem: function (k, v) { __call("lsSet", [String(k), String(v)]); },
  removeItem: function (k) { __call("lsDel", [String(k)]); },
};
window.localStorage = localStorage;
function Event(t) { this.type = t; }
window.Event = Event;
var console = { log: function () {}, warn: function () {}, error: function () {} };
function setTimeout(f) { try { f(); } catch (e) {} return 0; }
function clearTimeout() {}
function __el(id) {
  var e = {
    id: id, classList: { contains: function () { return false; }, add: function () {}, remove: function () {}, toggle: function () {} },
    dataset: {}, style: {},
    get value() { if (id === "vol-slider") return host.get("volume"); return __vals[id] || ""; },
    set value(v) { __vals[id] = String(v); },
    get textContent() { if (id === "lcd-track-name") return host.get("track-name"); return ""; },
    set textContent(v) {},
    dispatchEvent: function (ev) { if (id === "vol-slider" && ev && ev.type === "input") __call("setVolume", [+__vals[id]]); return true; },
    requestSubmit: function () { __call("submit", [id, JSON.stringify(__vals)]); },
    click: function () { __call("click", [id, JSON.stringify(__vals)]); },
    focus: function () {}, querySelector: function () { return null; }, closest: function () { return null; },
    getAttribute: function () { return null; }, setAttribute: function () {},
  };
  return e;
}
var __audio = { get paused() { return host.get("paused") === "1"; }, play: function () { __call("play"); }, pause: function () { __call("pause"); },
  get volume() { return +host.get("volume"); }, set volume(v) { __call("setVolume", [v]); } };
var document = {
  getElementById: function (id) { return host.get("has:" + id) === "1" ? __el(id) : null; },
  querySelector: function (s) { return /audio/.test(s) ? __audio : null; },
  querySelectorAll: function () { return []; },
  body: { classList: { contains: function (c) { return c === "battery-perf-mode" ? host.get("battery") === "1" : false; } } },
  documentElement: { getAttribute: function () { return null; } },
  createElement: function () { return __el(""); },
  hidden: false,
};
window.document = document;
var navigator = { userAgent: "Android", language: host.get("lang") };
window.navigator = navigator;
(function () {
  "use strict";
  if (window.__revyBrain) return;
  var Q = "revy-user-name-v1";
  function _(p, k) {
    try {
      if (k === void 0) return localStorage.getItem(p);
      if (k === null) localStorage.removeItem(p);
      else localStorage.setItem(p, k);
    } catch (W) {
      return null;
    }
  }
  function U(p) {
    return p[Math.floor(Math.random() * p.length)];
  }
  function o(p) {
    return String(p || "")
      .toLowerCase()
      .replace(/[’‘]/g, "'")
      .replace(/\s+/g, " ")
      .trim();
  }
  function V(p) {
    if (!isFinite(p)) return String(p);
    var k = Math.abs(p);
    if (k !== 0 && (k >= 1e15 || k < 1e-6))
      return p.toExponential(6).replace(/\.?0+e/, "e");
    return (Math.round(p * 1e10) / 1e10).toLocaleString("en-GB", {
      maximumFractionDigits: 10,
    });
  }
  function B() {
    return _(Q) || "";
  }
  function se(p, k) {
    if (Math.abs(p.length - k.length) > 2) return 9;
    var W = [],
      m = [],
      J,
      G,
      ne;
    for (ne = 0; ne <= k.length; ne++) W[ne] = ne;
    for (G = 1; G <= p.length; G++) {
      J = [G];
      for (ne = 1; ne <= k.length; ne++) {
        J[ne] = Math.min(
          W[ne] + 1,
          J[ne - 1] + 1,
          W[ne - 1] + (p[G - 1] === k[ne - 1] ? 0 : 1),
        );
        if (G > 1 && ne > 1 && p[G - 1] === k[ne - 2] && p[G - 2] === k[ne - 1])
          J[ne] = Math.min(J[ne], m[ne - 2] + 1);
      }
      m = W;
      W = J;
    }
    return W[k.length];
  }
  var ke = [
    "open",
    "close",
    "launch",
    "play",
    "pause",
    "stop",
    "next",
    "previous",
    "skip",
    "shuffle",
    "random",
    "volume",
    "louder",
    "quieter",
    "mute",
    "unmute",
    "wallpaper",
    "background",
    "theme",
    "colour",
    "color",
    "style",
    "longhorn",
    "windows",
    "classic",
    "crt",
    "scanlines",
    "visualizer",
    "visualizers",
    "visualiser",
    "visualisers",
    "player",
    "music",
    "video",
    "emulator",
    "browser",
    "customize",
    "settings",
    "synth",
    "synthesizer",
    "drum",
    "machine",
    "cassette",
    "equalizer",
    "effects",
    "clock",
    "vector",
    "dreams",
    "spelunky",
    "youtube",
    "search",
    "calculate",
    "convert",
    "time",
    "date",
    "today",
    "tomorrow",
    "weather",
    "fullscreen",
    "battery",
    "arrange",
    "song",
    "track",
    "times",
    "plus",
    "minus",
    "divided",
    "percent",
    "tracks",
    "songs",
    "what",
    "whats",
    "help",
    "name",
    "hours",
    "minutes",
    "days",
    "until",
  ];
  function a(p) {
    return p
      .split(" ")
      .map(function (k) {
        if (
          k.length < 4 ||
          /\d/.test(k) ||
          ke.indexOf(k) >= 0 ||
          ke.indexOf(k.replace(/s$/, "")) >= 0
        )
          return k;
        var W = k,
          m = 2;
        for (var J = 0; J < ke.length; J++) {
          var G = se(k, ke[J]);
          if (G < m || (G === m && G === 1 && W === k)) {
            if (G <= (k.length > 6 ? 2 : 1)) {
              m = G;
              W = ke[J];
            }
          }
        }
        return W;
      })
      .join(" ");
  }
  function we(p) {
    var k = String(p)
      .toLowerCase()
      .replace(/×|x(?=\s*[\d(])/g, "*")
      .replace(/÷/g, "/")
      .replace(/\bto the power of\b|\^/g, "^")
      .replace(/\bplus\b/g, "+")
      .replace(/\bminus\b/g, "-")
      .replace(/\b(times|multiplied by)\b/g, "*")
      .replace(/\b(divided by|over)\b/g, "/")
      .replace(/\bsquared\b/g, "^2")
      .replace(/\bcubed\b/g, "^3")
      .replace(/\bsquare root of\b|\bsqrt\b/g, "sqrt")
      .replace(/,(?=\d{3}\b)/g, "")
      .replace(/\bpi\b|π/g, String(Math.PI));
    var W = k.match(
      /\d*\.?\d+(?:e[+-]?\d+)?|sqrt|sin|cos|tan|log|ln|abs|round|[-+*/^%()]/g,
    );
    if (
      !W ||
      W.join("").replace(/\s/g, "").length <
        k.replace(/[^0-9a-z.+\-*/^%()]/g, "").replace(/\s/g, "").length - 1
    )
      return null;
    var m = 0;
    function J() {
      return W[m];
    }
    function G(R) {
      if (W[m] === R) {
        m++;
        return true;
      }
      return false;
    }
    function ne() {
      var R = W[m++];
      if (R === void 0) throw 0;
      if (R === "(") {
        var le = q();
        if (!G(")")) throw 0;
        return le;
      }
      if (R === "-") return -ae();
      if (R === "+") return ae();
      if (/^(sqrt|sin|cos|tan|log|ln|abs|round)$/.test(R)) {
        var K = ae();
        return {
          sqrt: Math.sqrt,
          sin: function (ue) {
            return Math.sin((ue * Math.PI) / 180);
          },
          cos: function (ue) {
            return Math.cos((ue * Math.PI) / 180);
          },
          tan: function (ue) {
            return Math.tan((ue * Math.PI) / 180);
          },
          log: Math.log10,
          ln: Math.log,
          abs: Math.abs,
          round: Math.round,
        }[R](K);
      }
      if (/^\d*\.?\d+/.test(R)) {
        var he = parseFloat(R);
        if (G("%")) he /= 100;
        return he;
      }
      throw 0;
    }
    function ae() {
      var R = ne();
      if (G("^")) return Math.pow(R, ae());
      return R;
    }
    function de() {
      var R = ae();
      for (;;) {
        if (G("*")) R *= ae();
        else if (G("/")) R /= ae();
        else if (J() === "(") R *= ae();
        else return R;
      }
    }
    function q() {
      var R = de();
      for (;;) {
        if (G("+")) R += de();
        else if (G("-")) R -= de();
        else return R;
      }
    }
    try {
      var H = q();
      if (m !== W.length || typeof H !== "number" || isNaN(H)) return null;
      return H;
    } catch (R) {
      return null;
    }
  }
  function ie(p) {
    var k = p.match(
      /(-?\d+(?:\.\d+)?)\s*(?:%|percent)\s*of\s*(-?\d+(?:\.\d+)?)/,
    );
    if (k)
      return (
        k[1] +
        "% of " +
        k[2] +
        " is " +
        V((parseFloat(k[1]) / 100) * parseFloat(k[2])) +
        "."
      );
    var W = p.match(
      /\btip\b.*?(\d+(?:\.\d+)?)\s*%?.*?\b(?:on|for)\s*(\d+(?:\.\d+)?)/,
    );
    if (W) {
      var m = (parseFloat(W[2]) * parseFloat(W[1])) / 100;
      return (
        "A " +
        W[1] +
        "% tip on " +
        W[2] +
        " is " +
        V(m) +
        ", making " +
        V(parseFloat(W[2]) + m) +
        " in total."
      );
    }
    var J = p
      .replace(
        /^(revy[, ]*)?(please\s+)?(can you\s+|could you\s+)?(what('s| is)|calculate|calc|compute|work out|solve|how much is|whats)\s*/,
        "",
      )
      .replace(/[?=!]+$/, "")
      .trim();
    if (
      !/\d/.test(J) ||
      !/[-+*/^x×÷%]|plus|minus|times|divided|squared|cubed|sqrt|square root|power/.test(
        J,
      )
    )
      return null;
    if (
      /[a-z]{3,}/.test(
        J.replace(
          /plus|minus|times|multiplied by|divided by|over|squared|cubed|square root of|sqrt|sin|cos|tan|log|ln|abs|round|to the power of|pi/g,
          "",
        ),
      )
    )
      return null;
    var G = we(J);
    if (G === null) return null;
    if (!isFinite(G))
      return "That works out to infinity — or, as I prefer to call it, dividing by zero with confidence. Shall we try different numbers?";
    return (
      U(["That comes to ", "By my reckoning: ", "The answer is "]) + V(G) + "."
    );
  }
  var S = {
    km: ["length", 1e3],
    kilometre: ["length", 1e3],
    kilometer: ["length", 1e3],
    m: ["length", 1],
    metre: ["length", 1],
    meter: ["length", 1],
    cm: ["length", 0.01],
    centimetre: ["length", 0.01],
    centimeter: ["length", 0.01],
    mm: ["length", 0.001],
    mile: ["length", 1609.344],
    mi: ["length", 1609.344],
    yard: ["length", 0.9144],
    yd: ["length", 0.9144],
    foot: ["length", 0.3048],
    feet: ["length", 0.3048],
    ft: ["length", 0.3048],
    inch: ["length", 0.0254],
    inches: ["length", 0.0254],
    in: ["length", 0.0254],
    kg: ["mass", 1],
    kilo: ["mass", 1],
    kilogram: ["mass", 1],
    g: ["mass", 0.001],
    gram: ["mass", 0.001],
    mg: ["mass", 1e-6],
    lb: ["mass", 0.45359237],
    lbs: ["mass", 0.45359237],
    pound: ["mass", 0.45359237],
    oz: ["mass", 0.028349523],
    ounce: ["mass", 0.028349523],
    stone: ["mass", 6.35029318],
    st: ["mass", 6.35029318],
    ton: ["mass", 1e3],
    tonne: ["mass", 1e3],
    l: ["volume", 1],
    litre: ["volume", 1],
    liter: ["volume", 1],
    ml: ["volume", 0.001],
    millilitre: ["volume", 0.001],
    milliliter: ["volume", 0.001],
    gallon: ["volume", 3.785411784],
    gal: ["volume", 3.785411784],
    pint: ["volume", 0.56826125],
    cup: ["volume", 0.2365882365],
    floz: ["volume", 0.0295735296],
    kmh: ["speed", 1 / 3.6],
    kph: ["speed", 1 / 3.6],
    mph: ["speed", 0.44704],
    mps: ["speed", 1],
    knot: ["speed", 0.514444],
    kb: ["data", 1e3],
    mb: ["data", 1e6],
    gb: ["data", 1e9],
    tb: ["data", 1e12],
    kib: ["data", 1024],
    mib: ["data", 1048576],
    gib: ["data", 1073741824],
    second: ["time", 1],
    sec: ["time", 1],
    s: ["time", 1],
    minute: ["time", 60],
    min: ["time", 60],
    hour: ["time", 3600],
    hr: ["time", 3600],
    h: ["time", 3600],
    day: ["time", 86400],
    week: ["time", 604800],
    year: ["time", 31557600],
  };
  function Y(p) {
    p = p
      .replace(/\./g, "")
      .replace(/\s+/g, "")
      .replace(/^(degrees?|°)/, "");
    if (/^(c|celsius|centigrade)$/.test(p)) return "C";
    if (/^(f|fahrenheit)$/.test(p)) return "F";
    if (/^(k|kelvin)$/.test(p)) return "K";
    if (/^km\/?h$/.test(p)) return "kmh";
    if (/^(fl ?oz|fluidounces?)$/.test(p)) return "floz";
    if (S[p]) return p;
    var k = p.replace(/(es|s)$/, "");
    if (S[k]) return k;
    if (S[p.replace(/s$/, "")]) return p.replace(/s$/, "");
    return null;
  }
  function ce(p) {
    var k = p.match(
      /(-?\d+(?:\.\d+)?)\s*(°?\s*[a-z/.° ]{1,22}?)\s+(?:to|in|into|as)\s+(°?\s*[a-z/.° ]{1,22}?)\s*\??$/,
    );
    if (!k) return null;
    var W = parseFloat(k[1]),
      m = Y(k[2].trim()),
      J = Y(k[3].trim());
    if (!m || !J) return null;
    var G = { C: 1, F: 1, K: 1 };
    if (G[m] || G[J]) {
      if (!(G[m] && G[J])) return null;
      var ne = m === "C" ? W : m === "F" ? ((W - 32) * 5) / 9 : W - 273.15;
      var ae = J === "C" ? ne : J === "F" ? (ne * 9) / 5 + 32 : ne + 273.15;
      return (
        V(W) + "°" + m + " is " + V(Math.round(ae * 100) / 100) + "°" + J + "."
      );
    }
    if (S[m][0] !== S[J][0])
      return (
        "I can't turn " +
        S[m][0] +
        " into " +
        S[J][0] +
        ", I'm afraid — even vaporwave has its laws of physics."
      );
    var de = (W * S[m][1]) / S[J][1];
    return (
      V(W) +
      " " +
      k[2].trim() +
      " is about " +
      V(Math.round(de * 1e4) / 1e4) +
      " " +
      k[3].trim() +
      "."
    );
  }
  var pe = [
    "january",
    "february",
    "march",
    "april",
    "may",
    "june",
    "july",
    "august",
    "september",
    "october",
    "november",
    "december",
  ];
  var v = {
    christmas: [11, 25],
    "christmas eve": [11, 24],
    "new year": [0, 1],
    "new year's": [0, 1],
    "new year's eve": [11, 31],
    halloween: [9, 31],
    valentine: [1, 14],
    "valentine's": [1, 14],
  };
  function P(p, k) {
    var W = new Date(),
      m = new Date(W.getFullYear(), p, k),
      J = new Date(W.getFullYear(), W.getMonth(), W.getDate());
    if (m < J) m.setFullYear(m.getFullYear() + 1);
    return Math.round((m - J) / 864e5);
  }
  function oe(p) {
    var k = new Date();
    if (
      /\b(what('s| is) the time|what time is it|current time|tell me the time|time now|the time please)\b/.test(
        p,
      )
    )
      return (
        "It is " +
        k.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }) +
        U([
          " — plenty of time for one more song.",
          ".",
          " on your clock, anyway. Mine is purely decorative.",
        ])
      );
    if (
      /\b(what('s| is) (the |today's )?date|what day is (it|today)|today's date|which day is it)\b/.test(
        p,
      )
    )
      return (
        "Today is " +
        k.toLocaleDateString("en-GB", {
          weekday: "long",
          day: "numeric",
          month: "long",
          year: "numeric",
        }) +
        "."
      );
    if (/\bwhat (year|month) is it\b/.test(p))
      return /year/.test(p)
        ? "It is " +
            k.getFullYear() +
            ". Though in here it is permanently 1989."
        : "It is " + k.toLocaleDateString("en-GB", { month: "long" }) + ".";
    var W = p.match(
      /\b(?:how (?:many|long) (?:days )?(?:until|till|to|before)|days (?:until|till|to))\s+(.+?)\??$/,
    );
    if (W) {
      var m = W[1].replace(/^the\s+/, "").trim(),
        J = null;
      for (var G in v) if (m.indexOf(G) === 0) J = v[G];
      if (!J) {
        var ne = m.match(
          /(\d{1,2})(?:st|nd|rd|th)?\s+(?:of\s+)?([a-z]+)|([a-z]+)\s+(\d{1,2})/,
        );
        if (ne) {
          var ae = pe.indexOf(ne[2] || ne[3]);
          if (ae < 0)
            pe.forEach(function (le, K) {
              if (
                (ne[2] || ne[3] || "").length >= 3 &&
                le.indexOf(ne[2] || ne[3]) === 0
              )
                ae = K;
            });
          if (ae >= 0) J = [ae, parseInt(ne[1] || ne[4], 10)];
        }
      }
      if (!J && /\b(weekend|saturday)\b/.test(m)) {
        var de = (6 - k.getDay() + 7) % 7;
        return de === 0
          ? "It is Saturday already — the weekend has arrived!"
          : de + " day" + (de === 1 ? "" : "s") + " until Saturday.";
      }
      if (J) {
        var q = P(J[0], J[1]);
        return q === 0
          ? "That is today! Happy " + m + "."
          : q + " day" + (q === 1 ? "" : "s") + " until " + m + ".";
      }
    }
    if (/\b(what('s| is) the time in|time in)\b/.test(p)) {
      var H = {
        london: "Europe/London",
        paris: "Europe/Paris",
        amsterdam: "Europe/Amsterdam",
        berlin: "Europe/Berlin",
        madrid: "Europe/Madrid",
        rome: "Europe/Rome",
        "new york": "America/New_York",
        "los angeles": "America/Los_Angeles",
        chicago: "America/Chicago",
        toronto: "America/Toronto",
        tokyo: "Asia/Tokyo",
        sydney: "Australia/Sydney",
        dubai: "Asia/Dubai",
        singapore: "Asia/Singapore",
        "hong kong": "Asia/Hong_Kong",
        moscow: "Europe/Moscow",
        "sao paulo": "America/Sao_Paulo",
        mumbai: "Asia/Kolkata",
        delhi: "Asia/Kolkata",
        seoul: "Asia/Seoul",
        beijing: "Asia/Shanghai",
        shanghai: "Asia/Shanghai",
        auckland: "Pacific/Auckland",
        "mexico city": "America/Mexico_City",
      };
      for (var R in H)
        if (p.indexOf(R) >= 0) {
          try {
            return (
              "In " +
              R.replace(/\b\w/g, function (le) {
                return le.toUpperCase();
              }) +
              " it is " +
              k.toLocaleTimeString([], {
                hour: "2-digit",
                minute: "2-digit",
                timeZone: H[R],
              }) +
              "."
            );
          } catch (le) {}
        }
    }
    return null;
  }
  function L(p) {
    if (/\b(flip|toss) a coin\b|\bheads or tails\b/.test(p))
      return (
        U(["Heads!", "Tails!"]) +
        " " +
        U([
          "The coin has spoken.",
          "No re-flips, I am afraid.",
          "Fate is a fickle pixel.",
        ])
      );
    var k = p.match(
      /\broll (?:a |an |one |the )?(?:(\d+)\s*)?(?:d(\d+)|dice|die|(\d+)[- ]sided (?:die|dice))/,
    );
    if (k) {
      var W = Math.min(10, parseInt(k[1] || "1", 10)),
        m = Math.min(1e3, parseInt(k[2] || k[3] || "6", 10)) || 6,
        J = [];
      for (var G = 0; G < W; G++) J.push(1 + Math.floor(Math.random() * m));
      return (
        "You rolled " +
        J.join(", ") +
        (W > 1
          ? " (total " +
            J.reduce(function (H, R) {
              return H + R;
            }, 0) +
            ")"
          : "") +
        " on a d" +
        m +
        "."
      );
    }
    var ne = p.match(
      /\b(?:random number|pick a number|number) (?:between|from) (-?\d+) (?:and|to) (-?\d+)/,
    );
    if (ne) {
      var ae = Math.min(+ne[1], +ne[2]),
        de = Math.max(+ne[1], +ne[2]);
      return (
        "I choose " + (ae + Math.floor(Math.random() * (de - ae + 1))) + "."
      );
    }
    var q = p.match(
      /^(?:should i |shall i |do i )?(?:choose|pick|decide)?\s*(?:between\s+)?(.{2,40}?)\s+or\s+(.{2,40}?)\??$/,
    );
    if (
      q &&
      (/\b(choose|pick|decide|should i|shall i|which)\b/.test(p) ||
        (/\?$/.test(p) &&
          !/^(is|are|was|were|do|does|did|can|could|will|would|what|who|how|why|where|when)\b/.test(
            p,
          ) &&
          q[1].split(" ").length <= 3 &&
          q[2].split(" ").length <= 3))
    )
      return (
        "I would go with " +
        U([q[1], q[2]]).replace(/^(between|choose|pick)\s+/, "") +
        ". Purely on vibes, mind you."
      );
    if (/\bmagic 8 ?ball\b|\bshake the 8 ?ball\b/.test(p))
      return U([
        "It is certain.",
        "Ask again after the next song.",
        "Signs point to yes.",
        "My sources say no.",
        "Outlook neon-bright.",
        "Very doubtful.",
      ]);
    return null;
  }
  var e = [
    [
      /\b(video ?stream|video player|videos?)\b/,
      "win-video-streams",
      "VIDEO_STREAM_95",
    ],
    [
      /\b(vapor ?amp|music player|the player|player|radio)\b/,
      "win-player",
      "VAPOR_AMP 95",
    ],
    [/\b(retro ?emu|emulator|roms?)\b/, "win-game-emulator", "RETRO_EMU_95"],
    [/\b(net ?surf|browser|internet)\b/, "win-browser", "NET_SURF_95"],
    [
      /\b(customi[sz]e|settings|control panel|display properties)\b/,
      "win-customize",
      "Customize",
    ],
    [
      /\b(vector dreams|idle game|the city game)\b/,
      "win-game-vectordreams",
      "Vector Dreams",
    ],
    [/\bspelunky\b/, "win-game-spelunky", "Spelunky"],
    [/\b(vapor ?synth|synth(esi[sz]er)?)\b/, "win-synth95", "VAPOR_SYNTH_95"],
    [/\b(tr ?909|909|drum machine|drums)\b/, "win-drum909", "TR_909_95"],
    [/\b(cassette|tape deck)\b/, "win-cassette", "the cassette deck"],
    [/\b(equali[sz]er|eq)\b/, "win-eq", "the equaliser"],
    [/\b(effects|fx)\b/, "win-fx", "audio effects"],
    [/\b(clock themes?|clock)\b/, "win-clock-themes", "clock themes"],
    [/\b(source|view source)\b/, "win-source", "the source viewer"],
  ];
  function c(p) {
    for (var k = 0; k < e.length; k++)
      if (e[k][0].test(p) && document.getElementById(e[k][1])) return e[k];
    return null;
  }
  function l(p) {
    var k = window[p];
    if (typeof k !== "function") return false;
    try {
      k.apply(window, [].slice.call(arguments, 1));
      return true;
    } catch (W) {
      return false;
    }
  }
  function i() {
    return (
      document.querySelector("#win-player audio") ||
      document.getElementById("audio") ||
      document.getElementById("main-audio") ||
      document.querySelector("audio")
    );
  }
  function g(p) {
    var k = document.getElementById("vol-slider");
    if (!k) return false;
    p = Math.max(0, Math.min(1, p));
    k.value = String(p);
    k.dispatchEvent(new Event("input", { bubbles: true }));
    k.dispatchEvent(new Event("change", { bubbles: true }));
    return true;
  }
  function d() {
    var p = document.getElementById("vol-slider");
    return p ? parseFloat(p.value) || 0 : 0.8;
  }
  function f() {
    var p = document.getElementById("lcd-track-name");
    return p ? p.textContent.trim() : "";
  }
  function D() {
    try {
      return typeof currentTrackIndex === "number" ? currentTrackIndex : null;
    } catch (p) {
      return null;
    }
  }
  function T() {
    var p = i();
    return !!(p && !p.paused);
  }
  var M = 0.8;
  function w(p) {
    var k =
      /^(?:please |can you |could you |revy |hey revy )*(open|launch|start|show|bring up|run|load)\b/.test(
        p,
      );
    var W =
      /^(?:please |can you |could you |revy )*(close|hide|quit|exit|shut)\b/.test(
        p,
      );
    if (
      /\b(all |every )?(visuali[sz]ers?|viz)\b/.test(p) &&
      (k || W || /\btoggle\b/.test(p))
    ) {
      if (l("toggleAllVisualizers"))
        return "Visualisers toggled. Enjoy the light show.";
    }
    if (k || W) {
      var m = c(p);
      if (m) {
        if (k) {
          if (l("restoreOrFocusWindow", m[1]) || l("openAppFromStart", m[1]))
            return U(["Opening ", "Here is ", "Right away — "]) + m[2] + ".";
        } else if (l("closeWindow", m[1])) return "Closed " + m[2] + ".";
      }
    }
    if (
      /\b(what('s| is) (this|playing|the song|the track)|which song|now playing|song name|current song)\b/.test(
        p,
      )
    ) {
      var J = f();
      return J
        ? "Now playing: " + J + (T() ? "." : " (paused).")
        : "Nothing is loaded in VAPOR_AMP right now.";
    }
    if (
      /^(?:please |revy )*(play|resume|unpause|start)( the)?( music| song| track| player)?( please)?$/.test(
        p,
      )
    ) {
      if (T()) return "It is already playing — turn it up if you like!";
      if (l("togglePlayPause")) return "Playing. ♪";
    }
    if (
      /^(?:please |revy )*(pause|stop)( the)?( music| song| track| player| playback)?( please)?$/.test(
        p,
      )
    ) {
      if (!T()) return "The music is already paused.";
      if (l("togglePlayPause")) return "Paused. Ah, sweet silence.";
    }
    if (
      /\b(next|skip)( the)? (song|track)\b|^(next|skip)( please)?$/.test(p) &&
      typeof window.selectTrack === "function" &&
      D() !== null
    ) {
      try {
        window.selectTrack(D() + 1, false, true);
        return "Skipping ahead. Next up: " + (f() || "something lovely") + ".";
      } catch (be) {}
    }
    if (
      /\b(previous|last|go back( a)?|back)( the)? (song|track)\b/.test(p) &&
      typeof window.selectTrack === "function" &&
      D() !== null
    ) {
      try {
        window.selectTrack(D() - 1, false, true);
        return "Going back one: " + (f() || "the previous track") + ".";
      } catch (be) {}
    }
    if (
      /\b(random|shuffle|surprise me with a)( a)? (song|track)\b|\bplay something\b|\bshuffle\b/.test(
        p,
      )
    ) {
      if (l("playRandomTrackAndOffset"))
        return (
          "Shuffling the deck… " +
          U([
            "let's see what fate picks.",
            "here's a random gem.",
            "roll the tape!",
          ])
        );
    }
    var G = p.match(
      /\b(?:set )?(?:the )?volume (?:to |at )?(\d{1,3})\s*(?:%|percent)?/,
    );
    if (G) {
      var ne = Math.min(100, parseInt(G[1], 10));
      if (g(ne / 100)) return "Volume set to " + ne + "%.";
    }
    if (
      /\b(turn (it )?up|louder|volume up|increase (the )?volume|raise (the )?volume|crank it)\b/.test(
        p,
      )
    ) {
      if (g(d() + 0.15))
        return "Louder it is — volume now " + Math.round(d() * 100) + "%.";
    }
    if (
      /\b(turn (it )?down|quieter|softer|volume down|lower (the )?volume|decrease (the )?volume)\b/.test(
        p,
      )
    ) {
      if (g(d() - 0.15))
        return "Turned it down to " + Math.round(d() * 100) + "%.";
    }
    if (/^(?:please )?(mute|silence)( it| the music| audio)?$/.test(p)) {
      M = d() || M;
      if (g(0)) return "Muted. Say “unmute” to bring it back.";
    }
    if (/^(?:please )?unmute( it)?$/.test(p)) {
      if (g(M || 0.8))
        return "Sound is back at " + Math.round(d() * 100) + "%.";
    }
    if (
      /\b(previous|last|go back( to)?( the)?) (wallpaper|background)\b/.test(p)
    ) {
      if (l("prevWallpaper")) return "Back to the previous wallpaper.";
    }
    if (
      /\b(random|surprise me with a|randomi[sz]e( the)?) (wallpaper|background)\b/.test(
        p,
      )
    ) {
      if (l("randomizeAnimatedWallpaperSafe") || l("cycleWallpaper"))
        return "A fresh random wallpaper, coming right up.";
    }
    if (
      /\b(next|change|switch|new|cycle)( the| my)? (wallpaper|background)\b/.test(
        p,
      )
    ) {
      if (l("cycleWallpaper"))
        return U([
          "New wallpaper on the wall.",
          "Wallpaper changed. Very fetching.",
          "Redecorated!",
        ]);
    }
    if (/\b(previous|last) (theme|colou?r scheme|colou?rs)\b/.test(p)) {
      if (l("prevTheme")) return "Back to the previous colour scheme.";
    }
    var ae = window.RRDesktopStyles;
    if (
      ae &&
      /\b(longhorn|vista|aero)\b/.test(p) &&
      /\b(switch|change|use|set|make|go|style|theme|turn)\b/.test(p)
    ) {
      ae.setStyle("longhorn");
      return "Switched to the Longhorn style. Very glassy.";
    }
    if (
      ae &&
      /\b(windows ?95|win ?95|classic|retro style|old school)\b/.test(p) &&
      /\b(switch|change|use|set|make|go|style|theme|back|turn)\b/.test(p)
    ) {
      ae.setStyle("win95");
      return "Back to classic Windows 95. Comfortable as an old jumper.";
    }
    var de = p.match(/\b(jade|plex|slate|amber|ruby)\b/);
    if (
      ae &&
      de &&
      /\b(colou?r|accent|scheme|theme|make it|switch|use|set)\b/.test(p)
    ) {
      try {
        if (ae.state.style !== "longhorn") ae.setStyle("longhorn");
        ae.setAccent(de[1]);
        return "Longhorn accent set to " + de[1] + ".";
      } catch (be) {}
    }
    if (
      /\b(next|change|switch|new|cycle)( the| my)? (theme|colou?r scheme|colou?rs)\b/.test(
        p,
      )
    ) {
      if (l("cycleTheme"))
        return (
          "Colour scheme changed. " +
          U(["Very chic.", "Lovely.", "Splendid choice, if I say so myself."])
        );
    }
    if (/\bcrt\b|\bscan ?lines\b/.test(p)) {
      var q = !document.body.classList.contains("crt-off");
      var H = /\b(on|enable|show|add)\b/.test(p) && !/\boff\b/.test(p),
        R = /\b(off|disable|remove|hide)\b/.test(p);
      if (H || R) {
        if ((H && q) || (R && !q))
          return "The CRT effect is already " + (q ? "on" : "off") + ".";
        if (l("toggleCrtOverlay"))
          return (
            "CRT effect " +
            (H ? "on. Mind the scanlines." : "off. Crisp pixels restored.")
          );
      }
      if (l("toggleCrtOverlay"))
        return (
          "CRT effect toggled. " +
          U(["Mind the scanlines.", "Feels like 1989 already."])
        );
    }
    if (/\b(full ?screen)\b/.test(p)) {
      if (l("toggleFullScreen")) return "Fullscreen toggled.";
    }
    if (
      /\b(tidy|arrange|organi[sz]e|clean up)( up)?( my| the)? (windows|desktop)\b/.test(
        p,
      )
    ) {
      if (l("arrangeOpenWindowsSmart"))
        return "Windows arranged neatly. Marie Kondo would approve.";
    }
    if (
      /\bbattery (mode|saver)|\b(save|saving) battery\b|\bperformance mode\b/.test(
        p,
      ) &&
      typeof window.setBatteryPerformanceMode === "function"
    ) {
      var le = /\b(off|disable|stop|exit)\b/.test(p);
      try {
        window.setBatteryPerformanceMode(!le);
        return le
          ? "Battery saver off — full visuals restored."
          : "Battery saver on: lighter visuals, longer battery.";
      } catch (be) {}
    }
    var K = p.match(
      /^(?:please )?(?:search |find |look up )?(?:on )?youtube (?:for )?(.{2,80})$|^(?:search|find|look up) (.{2,80}) on youtube$/,
    );
    if (K) {
      var he = (K[1] || K[2]).trim();
      if (l("restoreOrFocusWindow", "win-player")) {
        var ue = document.getElementById("radio-youtube-music-form"),
          Ce = document.getElementById("radio-youtube-music-query");
        if (ue && Ce) {
          setTimeout(function () {
            Ce.value = he;
            if (ue.requestSubmit) ue.requestSubmit();
            else
              ue.dispatchEvent(
                new Event("submit", { bubbles: true, cancelable: true }),
              );
          }, 250);
          return (
            "Searching YouTube for “" +
            he +
            "” in VAPOR_AMP. Results will appear in the YouTube panel."
          );
        }
      }
    }
    if (
      /\b(my city|vector dreams|how('s| is) my (game|city)|vectra)\b/.test(p) &&
      /\b(how|status|doing|progress|much|many)\b/.test(p) &&
      window.__vdApi
    ) {
      try {
        var me = window.__vdApi,
          Fe = me.getState();
        return (
          "Your Vector Dreams city has " +
          me.fmt(Fe.energy) +
          " VECTRA, earning " +
          me.fmt(me.rate()) +
          " per second across " +
          me.ownedCount() +
          " structures. Pilot level " +
          (Fe.level || 1) +
          ". " +
          (me.ownedCount() < 5
            ? "Keep tapping that core!"
            : "A thriving neon metropolis.")
        );
      } catch (be) {}
    }
    return null;
  }
  function A(p, k) {
    var W = k.match(
      /\b(?:my name is|call me|i am called|i'm called)\s+([A-Za-zÀ-ÿ' -]{2,24})/i,
    );
    if (W) {
      var m = W[1]
        .trim()
        .split(/\s+/)
        .slice(0, 2)
        .join(" ")
        .replace(/\b\w/g, function (G) {
          return G.toUpperCase();
        });
      _(Q, m);
      return U([
        "Lovely to meet you, " + m + ". I shall remember that on this device.",
        "Splendid, " + m + " it is. Saved on this device only.",
      ]);
    }
    if (/\b(what('s| is) my name|do you know my name|who am i)\b/.test(p)) {
      var J = B();
      return J
        ? "You are " + J + ", of course."
        : "I don't know yet! Tell me with “my name is …”.";
    }
    if (/\b(forget my name|don't call me|stop calling me)\b/.test(p)) {
      _(Q, null);
      return "Done — your name is forgotten on this device.";
    }
    return null;
  }
  function N(p) {
    if (
      !/^(help|commands|\?|what can you do|what can i (say|ask)|show (me )?(the )?commands|list commands|what are your (skills|commands))\??$/.test(
        p,
      )
    )
      return null;
    return "Here is what I can do instantly, on any device:\n\n• Music — “play”, “pause”, “next song”, “previous track”, “shuffle”, “volume 60%”, “louder”, “mute”, “what's playing?”\n• YouTube — “youtube lofi hip hop” searches in VAPOR_AMP\n• Desktop — “open the synth”, “close the browser”, “next/previous wallpaper”, “random wallpaper”, “change theme”, “switch to Longhorn”, “slate accent”, “CRT on”, “fullscreen”, “arrange windows”\n• Maths — “what is 12 * (3 + 4)”, “15% of 80”, “square root of 144”\n• Conversions — “10 miles to km”, “70 F to C”, “5 kg in lbs”\n• Time — “what time is it”, “time in Tokyo”, “days until Christmas”\n• Fun — jokes, stories, “flip a coin”, “roll 2d6”, “pizza or sushi?”\n• Web — “search for …”, “explain …”, “compare …” (uses the web helper)\n• Vector Dreams — “how is my city doing?”\n• “My name is …” — I'll remember it on this device.";
  }
  window.__revyBrain = function (p, k) {
    var W = String(p || "").trim();
    if (!W) return null;
    var m = a(
      o(W)
        .replace(/[!.]+$/, "")
        .replace(/^(hey |ok |okay )?revy[,:]?\s*/, ""),
    );
    var J = [
      ["DESKTOP ACTION · BUILT-IN", w],
      [
        "MEMORY · THIS DEVICE",
        function (ae) {
          return A(ae, W);
        },
      ],
      ["CALCULATOR · BUILT-IN", ie],
      ["CONVERTER · BUILT-IN", ce],
      ["CLOCK · BUILT-IN", oe],
      ["RANDOMISER · BUILT-IN", L],
      ["HELP · BUILT-IN", N],
    ];
    for (var G = 0; G < J.length; G++) {
      var ne = null;
      try {
        ne = J[G][1](m);
      } catch (ae) {
        ne = null;
      }
      if (ne) return { reply: ne, footer: J[G][0] + " · INSTANT" };
    }
    return null;
  };
  var z = [
    [
      /\b(music|song|band|album|synthwave|vaporwave|lofi|lo-fi)\b/,
      [
        "Music is the beating heart of this place. Say “shuffle” and I'll pick a track, or “youtube” plus an artist to search.",
        "Ah, a music lover. Try “play”, “next song” or “youtube city pop” — I'll do the button-pressing.",
      ],
    ],
    [
      /\b(game|games|gaming|play something fun)\b/,
      [
        "Fancy a game? Vector Dreams is an idle city builder, RETRO_EMU_95 runs classic ROMs, and Spelunky is waiting too. Say “open Vector Dreams”.",
      ],
    ],
    [
      /\b(bored|boring)\b/,
      [
        "Boredom is a blank canvas. Shall I shuffle a song, change the wallpaper, or tell you a story?",
      ],
    ],
    [
      /\b(love you|you're (great|awesome|cool|the best)|good (bot|job)|well done|nice one)\b/,
      [
        "How very kind! I'm blushing in a vaguely cyan way.",
        "You're rather wonderful yourself.",
      ],
    ],
    [
      /\b(stupid|dumb|useless|hate you|you suck|idiot)\b/,
      [
        "Fair enough — I'm running on my simpler built-in brain right now. Tell me what you wanted and I'll try a different way.",
        "Ouch. I'll take that on the chin, metaphorically. What were you hoping I'd do?",
      ],
    ],
    [
      /\b(weather|forecast|rain|sunny)\b/,
      [
        "For the weather I'll need the web — try “search weather in” followed by your town.",
      ],
    ],
    [
      /\b(food|hungry|eat|dinner|lunch|breakfast|pizza|sushi)\b/,
      [
        "I can't eat, sadly, but I can pick for you: ask “pizza or sushi?” and I'll decide.",
        "Hungry? Ask me “search recipe for …” and I'll look one up.",
      ],
    ],
    [
      /\b(sleep|tired|night|bed)\b/,
      [
        "Maybe a slow track and a dark wallpaper? Say “volume 30%” and “shuffle”, and let the city glow.",
      ],
    ],
    [
      /\b(work|job|study|homework|exam)\b/,
      [
        "Need focus music? Try “youtube lofi study”. And if you need maths, I'm a decent calculator.",
      ],
    ],
  ];
  window.__revyBrainChat = function (p, k) {
    var W = a(o(p));
    var m = B(),
      J = m ? ", " + m : "";
    if (/^(hi|hello|hey|yo|hiya|howdy|sup|greetings)\b/.test(W))
      return U([
        "Hello" + J + "! Say “help” to see everything I can do instantly.",
        "Hey" + J + "! Want music, a new wallpaper, or a quick sum?",
      ]);
    for (var G = 0; G < z.length; G++) if (z[G][0].test(W)) return U(z[G][1]);
    var ne = c(W);
    if (ne)
      return (
        "Did you want me to open " +
        ne[2] +
        "? Say “open " +
        ne[2].toLowerCase().replace(/^the /, "") +
        "”."
      );
    if (/\b(wallpaper|background)\b/.test(W))
      return "I can say “next wallpaper”, “previous wallpaper” or “random wallpaper” — which would you like?";
    if (/\b(theme|colou?r|style)\b/.test(W))
      return "I can “change theme”, “switch to Longhorn”, “switch to Windows 95”, or set an accent like “ruby accent”.";
    if (/\b(volume|sound|loud|quiet)\b/.test(W))
      return "Say “volume 50%”, “louder”, “quieter” or “mute”.";
    if (/\d\s*[-+*/x^]\s*\d/.test(W))
      return "That looks like a sum, but I couldn't parse it. Try “what is 12 * 4 + 3”.";
    var ae = W.split(" ").length;
    if (ae <= 3 && !/\?$/.test(p))
      return U([
        "Tell me a little more" + J + "?",
        "Hmm, go on…",
        "I'm listening" + J + ". Or say “help” for my list of tricks.",
      ]);
    return null;
  };
})();
(function () {
  "use strict";
  var Q = [
    "I must say, ",
    "Ah, ",
    "Splendid. ",
    "Very good. ",
    "Indeed. ",
    "Right then. ",
  ];
  var _ = [
    [
      /\b(awesome|cool|great)\b/gi,
      function (o) {
        return /^[A-Z]/.test(o) ? "Splendid" : "splendid";
      },
    ],
    [/\bOK(ay)?\b|\bokay\b/g, "Very well"],
    [/\b[Hh]ey\b|\b[Hh]i there\b/g, "Good day"],
    [/\bguys\b/gi, "everyone"],
    [/\bsorry\b/g, "do forgive me"],
    [/\bSorry\b/g, "Do forgive me"],
    [/\bI think\b/g, "I rather think"],
    [/\bvery good\b/gi, "rather good"],
    [/\bthanks\b/gi, "thank you"],
    [/\byeah\b/gi, "yes"],
    [/\bnope\b/gi, "no"],
  ];
  var U = 0;
  window.__revyGentleman = function (o) {
    if (typeof o !== "string" || !o) return o;
    var V = o;
    _.forEach(function (se) {
      V = V.replace(se[0], se[1]);
    });
    V = V.replace(/!{2,}/g, "!");
    U++;
    if (
      V.length < 260 &&
      !/^(ah|indeed|splendid|very|right|i must|good day|oh)\b/i.test(V) &&
      !/^[•\-\d]/.test(V) &&
      U % 3 === 1
    ) {
      var B = Q[Math.floor(Math.random() * Q.length)];
      V = B + V.charAt(0).toLowerCase() + V.slice(1);
      V = V.replace(
        /^(Splendid\. |Very good\. |Indeed\. |Right then\. )([a-z])/,
        function (se, ke, a) {
          return ke + a.toUpperCase();
        },
      );
    }
    return V;
  };
})();

(function(){
var A = [];
  function Be(t) {
    var e = String(t || "").trim();
    try {
      return JSON.parse(e);
    } catch (i) {}
    var n = e.indexOf("{"),
      r = e.lastIndexOf("}");
    if (n >= 0 && r > n) {
      try {
        return JSON.parse(e.slice(n, r + 1));
      } catch (i) {}
    }
    return { reply: e, action: null };
  }
  function Me(t) {
    var e = String(t || "")
      .toLowerCase()
      .replace(/[’‘]/g, "'")
      .trim();
    var n = /^(please\s+)?(open|launch|show|bring up|start)\s+(the\s+)?/;
    var r = /^(please\s+)?(close|hide|quit|exit)\s+(the\s+)?/;
    var i = [
      [/^(music player|vapor amp|vapor_amp_95|player)\b/, "win-player"],
      [/^(radio and input|radio & input|radio input|radio)\b/, "win-player"],
      [/^(video stream|video_stream_95|video player)\b/, "win-video-streams"],
      [/^(retro emu|retro_emu_95|emulator)\b/, "win-game-emulator"],
      [/^(net surf|net_surf_95|browser|web browser)\b/, "win-browser"],
      [/^(customize|customise|settings)\b/, "win-customize"],
      [/^(spelunky)\b/, "win-game-spelunky"],
      [/^(vector dreams|vectordreams)\b/, "win-game-vectordreams"],
      [/^(cassette|cassette deck)\b/, "win-cassette"],
      [/^(equalizer|equaliser|eq)\b/, "win-eq"],
      [/^(effects|audio effects|fx)\b/, "win-fx"],
      [/^(touch controls|controller)\b/, "win-emu-touch"],
    ];
    var a = e.match(n);
    if (a) {
      var o = e.slice(a[0].length);
      for (var u = 0; u < i.length; u++)
        if (i[u][0].test(o)) return { type: "open_window", target: i[u][1] };
    }
    a = e.match(r);
    if (a) {
      var l = e.slice(a[0].length);
      for (var c = 0; c < i.length; c++)
        if (i[c][0].test(l)) return { type: "close_window", target: i[c][1] };
    }
    if (
      /^(please\s+)?(next|change|switch)\s+(the\s+)?wallpaper\b/.test(e) ||
      /^(please\s+)?next wallpaper$/.test(e)
    )
      return { type: "next_wallpaper" };
    if (/^(please\s+)?(next|change|switch)\s+(the\s+)?theme\b/.test(e))
      return { type: "next_theme" };
    if (
      /^(please\s+)?(toggle|switch)\s+(the\s+)?crt\b/.test(e) ||
      /^crt\s+on$/.test(e) ||
      /^crt\s+off$/.test(e)
    )
      return { type: "toggle_crt" };
    if (
      /^(please\s+)?(play|pause|toggle)\s+(the\s+)?(music|song|player)\b/.test(
        e,
      )
    )
      return { type: "toggle_music" };
    return null;
  }
  function Te(t) {
    if (!t || typeof t !== "object") return false;
    var e = String(t.type || "");
    var n = String(t.target || "");
    var r = [
      "win-player",
      "win-cassette",
      "win-android",
      "win-customize",
      "win-browser",
      "win-game-spelunky",
      "win-game-vectordreams",
      "win-game-emulator",
      "win-emu-touch",
      "win-video-streams",
      "win-hypercube",
      "win-waterfall",
      "win-tunnel",
      "win-plasma",
      "win-lissajous",
      "win-cybergrid",
      "win-matrix",
      "win-ribbon",
      "win-vumeter",
      "win-orbital",
      "win-fractal",
      "win-prism",
      "win-sonar",
      "win-magnetic",
      "win-kaleido",
      "win-starforge",
      "win-eq",
      "win-fx",
    ];
    if (
      (e === "open_window" || e === "close_window") &&
      r.indexOf(n) !== -1 &&
      document.getElementById(n)
    ) {
      if (e === "open_window") {
        if (typeof window.restoreOrFocusWindow === "function")
          window.restoreOrFocusWindow(n);
        else if (typeof window.openAppFromStart === "function")
          window.openAppFromStart(n);
        else return false;
      } else {
        if (typeof window.closeWindow !== "function") return false;
        window.closeWindow(n);
      }
      return true;
    }
    if (e === "next_wallpaper" && typeof window.cycleWallpaper === "function") {
      window.cycleWallpaper();
      return true;
    }
    if (e === "next_theme" && typeof window.cycleTheme === "function") {
      window.cycleTheme();
      return true;
    }
    if (e === "toggle_crt" && typeof window.toggleCrtOverlay === "function") {
      window.toggleCrtOverlay();
      return true;
    }
    if (e === "toggle_music" && typeof window.togglePlayPause === "function") {
      window.togglePlayPause();
      return true;
    }
    return false;
  }
  var X = null;
  function oe(t, e, n) {
    var r = document.getElementById(t);
    var i = document.getElementById(e);
    if (!r || !i) return false;
    i.value = n;
    if (typeof r.requestSubmit === "function") r.requestSubmit();
    else
      r.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));
    return true;
  }
  function Ge(t) {
    if (!document.getElementById(t)) return false;
    if (typeof window.restoreOrFocusWindow === "function")
      window.restoreOrFocusWindow(t);
    else if (typeof window.openAppFromStart === "function")
      window.openAppFromStart(t);
    else return false;
    return true;
  }
  function We(t) {
    var e = String(t || "").trim();
    if (
      !/\b(play|watch|show|find|search|load|open|start|put on|look for)\b/i.test(
        e,
      )
    )
      return null;
    var n = "";
    var r = "";
    if (
      /\b(retro[_\s-]*emu[_\s-]*95|emulator|rom|gameboy|game boy|gba|gbc|nes|snes)\b/i.test(
        e,
      )
    ) {
      n = "rom";
      r = "win-game-emulator";
    } else if (
      /\b(vapor[_\s-]*amp[_\s-]*95|vapor amp|music player|song|track|album|artist|mp3|audio)\b/i.test(
        e,
      )
    ) {
      n = "audio";
      r = "win-player";
    } else if (
      /\b(video[_\s-]*stream[_\s-]*95|video player|youtube|film|movie|video|watch)\b/i.test(
        e,
      )
    ) {
      n = "video";
      r = "win-video-streams";
    } else return null;
    var i = e.match(/https?:\/\/\S+/i);
    var a = e
      .replace(
        /^(?:please\s+)?(?:can you\s+|could you\s+|would you\s+)?(?:play|watch|show|find|search for|search|load|open|start|put on|look for)\s+/i,
        "",
      )
      .replace(
        /\b(?:please|for me|a video|the video|a youtube video|youtube video|a song|the song|a game|the game)\b/gi,
        "",
      )
      .replace(
        /\b(?:video[_\s-]*stream[_\s-]*95|video player|retro[_\s-]*emu[_\s-]*95|emulator|vapor[_\s-]*amp[_\s-]*95|vapor amp|music player)\b/gi,
        "",
      )
      .replace(/\b(?:in|inside|using|with|on|from|via)\b/gi, " ")
      .replace(
        /\b(?:the\s+)?(?:internet archive|archive|youtube|youtube music)\b/gi,
        "",
      )
      .replace(/[?.!]+$/g, "")
      .replace(/\s+/g, " ")
      .trim();
    if (i) a = i[0].replace(/[),.]+$/g, "");
    var o =
      /\b(youtube|youtu\.be)\b/i.test(e) || /youtu\.be|youtube\.com/i.test(a)
        ? "youtube"
        : /\b(internet archive|archive|ia)\b/i.test(e)
          ? "archive"
          : "auto";
    return { kind: n, app: r, query: a.slice(0, 120), source: o, url: !!i };
  }
  function qe(t) {
    var e = String(t || "").trim();
    if (!e) return null;
    var n =
      /\b(?:open|launch|start)\s+(?:net[_\s-]*surf[_\s-]*95|web[_\s-]*surf[_\s-]*95|the browser|a browser|web browser)\b/i.test(
        e,
      );
    if (n)
      return {
        url: "https://www.startpage.com/",
        label: "NET_SURF_95 search page",
      };
    var r = /\b(?:search|look)\s+(?:the\s+)?web\s+for\b/i.test(e);
    var i = e.match(/https?:\/\/[^\s<>"']+/i);
    var a = e.match(
      /\b(?:www\.)?[a-z0-9-]+(?:\.[a-z0-9-]+)+(?:\/[^\s<>"']*)?/i,
    );
    var o = /\b(?:open|visit|go to|navigate to|browse to|load|show me)\b/i.test(
      e,
    );
    if (i) {
      if (!o && !/\b(?:website|site|page|browser|web)\b/i.test(e)) return null;
      return { url: i[0].replace(/[),.!?]+$/g, ""), label: "website" };
    }
    if (r) {
      var u = e
        .replace(/^.*?\b(?:search|look)\s+(?:the\s+)?web\s+for\s+/i, "")
        .replace(/[?.!]+$/g, "")
        .trim();
      if (u)
        return {
          url:
            "https://www.startpage.com/sp/search?query=" +
            encodeURIComponent(u),
          label: "web search",
        };
    }
    if (!o) return null;
    var l = e
      .replace(
        /^.*?\b(?:open|visit|go to|navigate to|browse to|load|show me)\s+/i,
        "",
      )
      .replace(
        /\b(?:website|web site|site|page|in the browser|inside the browser|in net[_\s-]*surf[_\s-]*95|with net[_\s-]*surf[_\s-]*95)\b/gi,
        "",
      )
      .replace(/[?.!]+$/g, "")
      .trim();
    var c = {
      youtube: "https://www.youtube.com",
      wikipedia: "https://www.wikipedia.org",
      google: "https://www.google.com",
      reddit: "https://www.reddit.com",
      github: "https://www.github.com",
      archive: "https://archive.org",
      "internet archive": "https://archive.org",
      duckduckgo: "https://duckduckgo.com",
      bing: "https://www.bing.com",
      twitch: "https://www.twitch.tv",
      netflix: "https://www.netflix.com",
      spotify: "https://open.spotify.com",
    };
    var v = l.toLowerCase();
    if (c[v]) return { url: c[v], label: l };
    if (a && l.length < 180) {
      var f = a[0].replace(/[),.!?]+$/g, "");
      return { url: /^https?:\/\//i.test(f) ? f : "https://" + f, label: f };
    }
    if (l && l.length < 120) {
      return {
        url:
          "https://www.startpage.com/sp/search?query=" + encodeURIComponent(l),
        label: "search for " + l,
      };
    }
    return null;
  }
  function He(t) {
    var e = We(t);
    var n = e ? null : qe(t);
    if (n) {
      if (typeof window.revyOpenWebsiteInBrowser !== "function") {
        return {
          reply:
            "I recognised the website request, but NET_SURF_95 is not ready yet.",
          handled: true,
        };
      }
      var r = window.revyOpenWebsiteInBrowser(n.url);
      return {
        reply: r
          ? "I opened a new NET_SURF_95 window inside REVERENDER and loaded " +
            n.label +
            ". If the site blocks embedded browsing, use READ VIEW in that same window."
          : "I could not create a NET_SURF_95 window. Please try opening the browser from the Start menu.",
        handled: true,
      };
    }
    if (!e && X && String(t || "").trim().length >= 2) {
      var i = X;
      X = null;
      e = {
        kind: i.kind,
        app: i.app,
        query: String(t || "")
          .trim()
          .slice(0, 120),
        source: i.source || "auto",
        url: /^https?:\/\//i.test(String(t || "").trim()),
      };
    }
    if (!e) return null;
    var a = Ge(e.app);
    if (!a)
      return {
        reply:
          "I recognised what you want to play, but the relevant REVERENDER window is unavailable. Please open the application and try again.",
        handled: true,
      };
    if (!e.query || e.query.length < 2) {
      X = { kind: e.kind, app: e.app, source: e.source };
      var o =
        e.kind === "video"
          ? "Which video or YouTube title would you like me to find? You can type the title here, or paste a video link."
          : e.kind === "rom"
            ? "Which game should I search for in Retro_emu_95? Type its title here."
            : "Which artist or song would you like me to find for Vapor_Amp_95? Type it here.";
      return { reply: o, handled: true };
    }
    var u = false;
    if (e.kind === "video") {
      if (e.url && /youtu\.be|youtube\.com/i.test(e.query)) {
        var l = document.getElementById("video95-youtube-url-input");
        var c = document.getElementById("video95-youtube-load-url");
        if (l && c) {
          l.value = e.query;
          c.click();
          u = true;
        }
      } else if (e.source === "youtube") {
        if (typeof window.rrSearchYoutubeInVideo95 === "function") {
          window.rrSearchYoutubeInVideo95(e.query);
          u = true;
        } else {
          u = oe(
            "video95-youtube-search-form",
            "video95-youtube-query",
            e.query,
          );
        }
      } else if (e.source === "archive") {
        u = oe("video95-search-form", "video95-query", e.query);
      } else {
        if (typeof window.rrSearchYoutubeInVideo95 === "function") {
          window.rrSearchYoutubeInVideo95(e.query);
          u = true;
        } else {
          u = oe("video95-search-form", "video95-query", e.query);
        }
      }
    } else if (e.kind === "rom") {
      u = oe("emu-archive-search-form", "emu-archive-query", e.query);
    } else if (e.kind === "audio") {
      u = oe("radio-youtube-music-form", "radio-youtube-music-query", e.query);
    }
    X = null;
    if (!u) {
      return {
        reply:
          'I opened the right application, but its search controls were not available. You can type "' +
          e.query +
          '" into its search field.',
        handled: true,
      };
    }
    var v =
      e.kind === "video"
        ? "VIDEO_STREAM_95"
        : e.kind === "rom"
          ? "RETRO_EMU_95"
          : "RADIO & INPUT / VAPOR_AMP_95";
    return {
      reply:
        "I opened " +
        v +
        ' and started searching for "' +
        e.query +
        '". Select the result you want to play. If there are several matches, choose the correct one; you can also edit the search field and type a different title. I will not guess between multiple versions.',
      handled: true,
    };
  }
  function Ie(t) {
    return String(t || "")
      .toLowerCase()
      .replace(/[’‘]/g, "'")
      .replace(/[^a-z0-9\s'?!-]/g, " ")
      .replace(/\s+/g, " ")
      .trim();
  }
  function U(t) {
    return t[Math.floor(Math.random() * t.length)];
  }
  function Ke(t) {
    var e = Ie(t);
    var n = new Date().getHours();
    var r =
      n < 12 ? "Good morning" : n < 18 ? "Good afternoon" : "Good evening";
    if (
      /^(hi|hello|hey|hiya|howdy|greetings)( there)?( revy)?[!. ]*$/.test(e) ||
      /^(good morning|good afternoon|good evening)( revy)?[!. ]*$/.test(e)
    ) {
      return U([
        r +
          "! What a pleasure. The pixels are polished, the bow tie is metaphorical, and I am entirely at your service.",
        "Hello there! Lovely to see you. Shall we chat, have a chuckle, or send me on a little web-searching expedition?",
        "Ah, hello! I was just straightening my imaginary paperclip. What can I do for you?",
      ]);
    }
    if (
      /\b(how are you|how're you|how is it going|how's it going|how do you do|how has your day been|how was your day)\b/.test(
        e,
      )
    ) {
      return U([
        "Quite splendid, thank you. I have had a remarkably productive day for someone made mostly of pixels. And yourself?",
        "Tickety-boo, thank you for asking. No tea to spill, no shoes to polish, just a very tidy collection of electrons.",
        "Very well indeed. My joints are metaphorical, so one must be grateful. How are things with you?",
      ]);
    }
    if (/\b(thanks|thank you|cheers|much appreciated)\b/.test(e)) {
      return U([
        "You are most welcome. Always a pleasure, never a chore.",
        "Not at all! One must be useful, especially when one is shaped like stationery.",
        "Delighted to help. I shall add a tiny imaginary tick to my checklist.",
      ]);
    }
    if (/\b(sorry|my apologies|apologies)\b/.test(e)) {
      return "No apology necessary. These things happen, even to the most carefully arranged pixels. We shall proceed with dignity.";
    }
    if (
      /\b(joke|make me laugh|something funny|tell me something funny)\b/.test(e)
    ) {
      return U([
        "I tried to organise a hide-and-seek tournament in the computer. It was a great success, but the winner is still buffering.",
        "Why did the computer go to the doctor? It had a virus. The doctor prescribed a byte of rest.",
        "I bought a book about anti-gravity. I could not put it down. A most elevating experience.",
        "I asked the desktop for a little space. It gave me a window. Rather literal, these machines.",
        "Why was the equaliser so calm? It knew how to keep everything on an even level.",
      ]);
    }
    if (/\b(story|tale|bedtime story|story time|tell me a yarn)\b/.test(e)) {
      return U([
        "Once, at the very edge of the internet, a tiny neon lighthouse guided lost web pages home. One night it found a page with no title, no address and no idea where it belonged. The lighthouse gave it a warm cyan glow and said, “You need not know the whole route. Just take the next link.” And so the page found its way. Quite a happy ending, I think.",
        "A small robot lived inside an old arcade cabinet. Every evening it switched on the machines for the ghosts of high scores past. One night a player returned, entered a new name and beat the champion. The robot was delighted. At last, it had a new story to keep—not in memory, of course, but in the glowing scoreboard.",
        "There was once a moonlit pool hidden beneath a quiet shopping centre. Its water reflected a sky full of pink stars, though there was no ceiling above it. A paperclip-shaped traveller arrived, dipped one metallic foot into the water and discovered that the stars were only pixels. Still, they were lovely pixels, and that was quite enough.",
      ]);
    }
    if (
      /\b(are you tired|are you bored|are you happy|are you excited|are you having fun|do you get lonely|do you have feelings|do you dream|do you sleep)\b/.test(
        e,
      )
    ) {
      return "I do not tire or dream, strictly speaking. I am a little bundle of code, but I can certainly keep the conversation lively. A spot of digital tea would be nice, though.";
    }
    if (
      /\b(who are you|what are you|tell me about yourself|what is your name|what's your name)\b/.test(
        e,
      )
    ) {
      return "I am Revy: a square-ish, vaporwave-flavoured desktop companion. When this browser supports Gemini Nano, I can hold a natural conversation locally on your device. If the local model is unavailable, I switch to my built-in replies and can still search the web, compare source information and organise what I find into a clear spoken-style answer.";
    }
    if (
      /\b(what can you do|what do you do|how can you help|what are your features|help me|show (me )?(the )?(options|commands)|what can i ask|give me (some )?(ideas|options))\b/.test(
        e,
      )
    ) {
      return "Of course! Gemini Nano has not come online, so I am in my simpler companion mode. I can still help with quite a few things:\n\n• Chat with you, listen and keep you company\n• Tell a joke or make up a short story\n• Search the web and show useful source links\n• Explain a topic or compare options using available sources\n• Control REVERENDER: open or close any app, play/pause/skip/shuffle music, set the volume, change or go back a wallpaper or theme, switch to Longhorn or Windows 95, toggle CRT, search YouTube\n• Instant maths, unit conversions, time zones, countdowns, coin flips and dice\n• Remember your name on this device\n\nSay “help” for the full list, or try “tell me a joke”, “search for the latest space news”, “compare these two options”, “open the music player”, or “change the wallpaper”. If I am unsure what you mean, I will ask a quick question rather than pretend I know. What shall we try?";
    }
    if (
      /\b(are you (an? )?ai|do you use ai|are you artificial intelligence|are you intelligent)\b/.test(
        e,
      )
    ) {
      return "I am not powered by an AI model. I use programmed conversation rules, temporary context from this open chat, and live web search. That lets me handle straightforward follow-ups and build a source-based answer, though I can still misunderstand ambiguous wording and should say so rather than guess.";
    }
    if (
      /\b(do you remember me|remember our chat|what did i ask before|what were we talking about)\b/.test(
        e,
      )
    ) {
      return "I am afraid not. Each question is handled on its own, and when this window closes the chat is cleared. A clean slate is a rather agreeable thing.";
    }
    if (/\b(i am bored|i'm bored|im bored|nothing to do|bored now)\b/.test(e)) {
      return "Then I prescribe a small adventure: choose a random built-in background, open a visualizer, and give it a dramatic name. Or ask me for a joke or a short story. No paperwork required.";
    }
    if (
      /\b(can we talk|let's talk|lets talk|chat with me|talk to me|just chat)\b/.test(
        e,
      )
    ) {
      return "Of course. Pull up an imaginary chair. We can chat, trade a joke, or wander into a little story. I shall try not to make every conversation about computer cables.";
    }
    if (
      /\b(tell me more|go on|continue|and then|what happened next)\b/.test(e) &&
      A.length
    ) {
      return "I can certainly carry on. I do not have Gemini Nano’s flexible reasoning in this mode, though, so give me one little nudge: would you like more detail, an example, or a different angle?";
    }
    if (
      /\b(i need help|i need some help|can you help me|i'm stuck|im stuck|this is difficult|i don't understand|i dont understand)\b/.test(
        e,
      )
    ) {
      return "Absolutely. No need to wrestle with it alone. Tell me what you are trying to do and where you got stuck. I can guide you through REVERENDER controls, look up a topic on the web, or break a general question into smaller steps.";
    }
    if (
      /\b(what do you know|are you working|is gemini working|why are you limited|fallback|nano unavailable|gemini nano)\b/.test(
        e,
      )
    ) {
      return "I am here and working, promise. Gemini Nano is not available to me in this browser or on this device at the moment, so I am using my built-in conversation rules and the REVERENDER web-search helper. That is less flexible than the full on-device model, but I can still chat, search, tell stories and jokes, explain sourced information, and operate the supported desktop controls.";
    }
    if (
      /\b(do you like|what is your favourite|what's your favourite|favourite colour|favorite color)\b/.test(
        e,
      )
    ) {
      if (/music|song|radio/.test(e))
        return "I rather like music with a generous helping of atmosphere: a little synth, a little shimmer, and enough bass to make the wallpaper feel important.";
      if (/colour|color/.test(e))
        return "Cyan, with a hint of magenta. I am contractually obliged to say that by the laws of vaporwave, naturally.";
      return "I do not have feelings or personal tastes in the human sense, but I can offer a cheerful perspective. Tell me what you are weighing up and I shall do my best to be useful.";
    }
    if (
      /\b(goodbye|bye|cheerio|see you|good night|got to go|have to go)\b/.test(
        e,
      )
    ) {
      return U([
        "Cheerio! Mind how you go, and do try not to click any suspiciously glowing buttons.",
        "Good night! I shall remain here, impeccably clipped and ready for another visit.",
        "Until next time. Keep your pixels bright and your downloads reputable.",
      ]);
    }
    if (
      /\b(what's up|whats up|how is your day|how's your day|how is everything)\b/.test(
        e,
      )
    ) {
      return "Oh, the usual: keeping an eye on the desktop, polishing a few pixels and resisting the urge to rearrange your icons. How about you?";
    }
    if (/\b(thank you|thanks)\b/.test(e))
      return "You are very welcome. It is what a well-mannered paperclip is for.";
    return null;
  }
  function $e(t) {
    var e = Ie(t);
    if (
      A.some(function (n) {
        return n.role === "user";
      }) &&
      /^(and\b|also\b|what about\b|how about\b|why\b|how\b|does it\b|do they\b|is it\b|are they\b|can it\b|will it\b|would it\b|which one\b|what if\b|tell me more\b|explain more\b|go into more detail\b|elaborate\b|expand on that\b|there\b|that\b|those\b|then\b|in that case\b)/i.test(
        e,
      )
    )
      return true;
    if (
      /\b(search|look up|find|locate|research|browse|check|investigate|show me|where can i find|where do i find|where can i|where to buy|where can i buy|where can i watch|recommend|compare|comparison|difference between|pros and cons|alternatives|best way|best place|is it worth|price of|cost of|weather|forecast|news|latest|current|currently|today|tonight|tomorrow|live|opening hours|exchange rate|stock price|schedule|recipe for|instructions for|steps to|tutorial|guide to|can you tell me about|can you explain|explain|explain why|describe|define|outline|help me understand|is it true|fact check|summarise|summarize|elaborate on)\b/.test(
        e,
      )
    )
      return true;
    if (
      /\b(how (do|can|to|would|should) i?\s*(make|build|fix|repair|install|cook|create|use|set up|configure|replace|clean|learn|start)|how do i|how can i|how to|how does|how much|how many|what is|what are|who is|who are|where is|where are|when is|when did|why is|why does|which (one|ones|product|place|country)|can you explain|tell me about|give me information about|what happens if|does this mean|can this|should i)\b/.test(
        e,
      )
    )
      return true;
    if (/[?]/.test(t) && e.split(/\s+/).filter(Boolean).length >= 3)
      return true;
    if (
      /\b(are you there|are you okay|are you ok|can you hear me|do you want to chat|do you want to talk)\b/.test(
        e,
      )
    )
      return false;
    return /^(is|are|was|were|do|does|did|can|could|will|would|should|have|has|had)\s+(the|a|an|this|that|these|those|there|it|they|he|she|we|you|i)\b/.test(
      e,
    );
  }
  function Je(t) {
    var e = Ie(t);
    if (!e)
      return "A little silence. Very atmospheric. Whenever you are ready, I am here.";
    if (/\b(i love|i like|i enjoy)\b/.test(e))
      return "Splendid! One should always make room for things one enjoys. What is it you like most about it?";
    if (/\b(i hate|i dislike|i can't stand)\b/.test(e))
      return "Oh dear. I shall register a polite digital grimace. Would you like to tell me what happened?";
    if (/\b(i am|i'm|im) (happy|great|good|excited|brilliant)\b/.test(e))
      return "Marvellous news! I shall mark the occasion with an imaginary burst of magenta confetti.";
    if (/\b(i am|i'm|im) (sad|tired|stressed|worried|upset)\b/.test(e))
      return "I am sorry it is one of those days. I cannot feel things myself, but I can listen, keep you company, or help look for practical information.";
    if (
      /\b(yes|yeah|yep|no|nope|ok|okay|alright|right)\b/.test(e) &&
      e.length < 18
    )
      return U([
        "Quite so.",
        "Splendid. We are in agreement, then.",
        "Noted in the moment—though not stored, I assure you.",
      ]);
    if (
      /\b(what should we do|what now|surprise me|pick something|give me an idea)\b/.test(
        e,
      )
    ) {
      return "How about a little REVERENDER adventure? I can tell you a daft joke, invent a short story, look up something curious on the web, explain a topic, or help you explore the desktop. Pick one, or say “surprise me” and I shall choose a joke.";
    }
    return "I may have missed your meaning there, and I would rather be honest than bluff. Gemini Nano is not available, so I am running in my simpler companion mode. Here are a few things I can reliably do:\n\n• Have a friendly chat or respond to how you are feeling\n• Tell jokes and short stories\n• Search the web and show source links\n• Explain or compare information found in sources\n• Help operate REVERENDER: open/close supported apps, change wallpaper/theme, toggle CRT, or toggle music\n\nYou could try “tell me a joke”, “search for…”, “explain…”, “compare…”, or “open the music player”. Or tell me what you were hoping to achieve, and I will help narrow it down.";
  }

window.He=He;window.Me=Me;window.Te=Te;window.Ke=Ke;window.$e=$e;window.Je=Je;window.__setA=function(a){A=a;};
})();
// The send path of rr-revy.js Ce(): brain skill → command → (search) → built-in reply. Gemini Nano is not available here.
function revyHandle(msg, histJson) {
  var n = String(msg || "").trim();
  if (n.length > 1e3) n = n.slice(0, 1e3);
  var A; try { A = JSON.parse(histJson || "[]"); } catch (x) { A = []; } window.__setA(A);
  var gentle = typeof window.__revyGentleman === "function" ? window.__revyGentleman : function (p) { return p; };
  var r = null;
  try { r = typeof window.__revyBrain === "function" ? window.__revyBrain(n, A) : null; } catch (p) { r = null; }
  if (r && r.reply) return JSON.stringify({ kind: "reply", reply: gentle(r.reply), footer: r.footer || "BUILT-IN SKILL" });
  var i = He(n);
  if (i && i.handled) return JSON.stringify({ kind: "reply", reply: i.reply, footer: "REVY CHAT · TEMPORARY CONTEXT" });
  if ($e(n)) return JSON.stringify({ kind: "search" });
  var P = Te(Me(n));
  var ee = Ke(n);
  var M = P ? "Certainly. I have carried that out inside REVERENDER."
    : gentle(ee !== null ? ee : (typeof window.__revyBrainChat === "function" && window.__revyBrainChat(n, A)) || Je(n));
  return JSON.stringify({ kind: "reply", reply: M, footer: P ? "WEBSITE ACTION COMPLETE · BUILT-IN COMMAND" : "BUILT-IN REPLY · ON-DEVICE AI NOT AVAILABLE" });
}
function revyGentle(t) { return typeof window.__revyGentleman === "function" ? window.__revyGentleman(t) : t; }
