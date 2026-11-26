// Builds one JS source with everything the wallpapers need, taken verbatim from the site.
const acorn = require('/opt/node-tools/node_modules/acorn');
const fs = require('fs');

function walk(n, f) { if (!n || typeof n.type !== 'string') return; f(n); for (const k in n) { const v = n[k]; if (Array.isArray(v)) v.forEach(c => walk(c, f)); else if (v && typeof v.type === 'string') walk(v, f); } }

const coreSrc = fs.readFileSync('/home/claude/pretty/rr-app-core.js', 'utf8');
const core = acorn.parse(coreSrc, { ecmaVersion: 'latest' });
const top = {};
const topStmts = [];
for (const st of core.body) {
  if (st.type === 'FunctionDeclaration') top[st.id.name] = st;
  else if (st.type === 'VariableDeclaration') for (const d of st.declarations) if (d.id.type === 'Identifier') top[d.id.name] = st;
  else topStmts.push(st);
}
for (const st of core.body) if (st.type !== 'FunctionDeclaration' && st.type !== 'VariableDeclaration') topStmts.push(st);

const seeds = process.argv[2].split(',');
const skip = new Set((process.argv[3] || '').split(','));
const need = new Set(); const q = [...seeds];
while (q.length) {
  const n = q.pop(); if (need.has(n) || !top[n] || skip.has(n)) continue; need.add(n);
  walk(top[n], x => { if (x.type === 'Identifier' && top[x.name] && !need.has(x.name)) q.push(x.name); });
}
const emitted = new Set(); const out = [];
for (const st of core.body) {
  if (st.type === 'FunctionDeclaration') { if (need.has(st.id.name)) out.push(coreSrc.slice(st.start, st.end)); continue; }
  if (st.type === 'VariableDeclaration') {
    const ds = st.declarations.filter(d => d.id.type === 'Identifier' && need.has(d.id.name));
    for (const d of ds) out.push(`${st.kind} ${coreSrc.slice(d.start, d.end)};`);
    continue;
  }
  // top-level statements (data initialisers) that touch only needed names
  let touches = false, foreign = false;
  walk(st, x => { if (x.type === 'Identifier' && top[x.name]) { if (need.has(x.name)) touches = true; else foreign = true; } });
  if (touches && !foreign && st.type !== 'ExpressionStatement') out.push(coreSrc.slice(st.start, st.end));
  else if (touches && !foreign && st.type === 'ExpressionStatement' && !/window\.|document\./.test(coreSrc.slice(st.start, st.end))) out.push(coreSrc.slice(st.start, st.end));
}
process.stdout.write(out.join('\n') + '\n');
