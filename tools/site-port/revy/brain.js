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
