// The send path of rr-revy.js Ce(): brain skill → command → (search) → built-in reply. Gemini Nano is not available here.
function revyHandle(msg, histJson) {
  var n = String(msg || "").trim();
  if (n.length > 1e3) n = n.slice(0, 1e3);
  try { A = JSON.parse(histJson || "[]"); } catch (x) { A = []; }
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
