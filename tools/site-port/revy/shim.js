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
