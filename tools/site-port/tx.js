// JS (the site's canvas drawing code) -> Kotlin transpiler.
// Numbers become Double, strings String, booleans Boolean; the canvas becomes Ctx, gradients Grad;
// anything else (objects, arrays, values read out of them) is dynamic (Any?) and coerced at use.
const acorn = require('/opt/node-tools/node_modules/acorn');
const fs = require('fs');

const [, , inFile, outFile, pkg, objName] = process.argv;
const EXT = JSON.parse(process.argv[7] || '{"fns":[],"vars":[]}');
const EXT_FN = new Set(EXT.fns), EXT_VAR = new Set(EXT.vars);
const src = fs.readFileSync(inFile, 'utf8');
const ast = acorn.parse(src, { ecmaVersion: 'latest', locations: true });

// ------------------------------------------------------------------ helpers
function kids(n) { const r = []; for (const k in n) { if (k === '_s' || k === '_b' || k === '_fn' || k === '_scope' || k === '_parentFn') continue; const v = n[k]; if (Array.isArray(v)) v.forEach(c => c && typeof c.type === 'string' && r.push(c)); else if (v && typeof v.type === 'string') r.push(v); } return r; }
function fail(n, msg) { const l = n && n.loc ? `${n.loc.start.line}:${n.loc.start.column}` : '?'; throw new Error(`${msg} @${l}: ${n ? src.slice(n.start, Math.min(n.end, n.start + 120)) : ''}`); }
const isFn = n => n.type === 'FunctionDeclaration' || n.type === 'FunctionExpression' || n.type === 'ArrowFunctionExpression';

// Globals provided by the Kotlin runtime: name -> type
const RUNTIME = {
  Math: 'MATH', Number: 'NUMFN', Array: 'ARRAYFN', window: 'WIN', Infinity: 'N', NaN: 'N', undefined: 'D',
  Float32Array: 'TA', Float64Array: 'TA', Uint8Array: 'TA', Int16Array: 'TA', Int32Array: 'TA', Map: 'TA', Set: 'TA',
  mouseX: 'N', mouseY: 'N', freqData: 'NARR', timeData: 'NARR', performance: 'PERF', document: 'DOC', getComputedStyle: 'GCS', console: 'CONSOLE', setTimeout: 'TIMER', clearTimeout: 'TIMER', requestAnimationFrame: 'TIMER', localStorage: 'LS', navigator: 'NAV', innerWidth: 'N', innerHeight: 'N', Date: 'DATE', JSON: 'JSONO', Object: 'OBJECT', parseInt: 'PARSEINT', parseFloat: 'PARSEFLOAT', isFinite: 'ISFINITE', String: 'STRFN', Boolean: 'BOOLFN',
};

// ------------------------------------------------------------------ scopes / bindings
let bid = 0;
const allBindings = [];
const fnInfos = [];
class Scope { constructor(parent, fn) { this.parent = parent; this.fn = fn; this.vars = new Map(); } lookup(n) { let s = this; while (s) { if (s.vars.has(n)) return s.vars.get(n); s = s.parent; } return null; } }
function sanitize(n) { return n.replace(/\$/g, 'S_').replace(/^_$/, 'U_'); }
function mkBinding(name, kind, scope, node) {
  const b = { id: bid++, name, kind, scope, node, types: new Set(), assigned: [], kname: null, fn: null, top: !scope.parent };
  b.kname = b.top ? (kind === 'fn' ? sanitize(name) : 'g_' + sanitize(name)) : 'v' + b.id + '_' + sanitize(name);
  allBindings.push(b); scope.vars.set(name, b); return b;
}
const globalScope = new Scope(null, null);

function declarePattern(p, kind, scope, cb) {
  if (p.type === 'Identifier') { const b = mkBinding(p.name, kind, scope, p); p._b = b; cb && cb(b); }
  else if (p.type === 'ArrayPattern') p.elements.forEach(e => e && declarePattern(e, kind, scope, cb));
  else if (p.type === 'ObjectPattern') p.properties.forEach(pr => declarePattern(pr.value, kind, scope, cb));
  else if (p.type === 'AssignmentPattern') declarePattern(p.left, kind, scope, cb);
  else fail(p, 'pattern');
}
// collect var declarations + function declarations of a function body (not nested fns)
function hoist(body, scope) {
  (function rec(n) {
    if (!n) return;
    if (n.type === 'VariableDeclaration' && n.kind === 'var') n.declarations.forEach(d => { if (!scope.vars.has(d.id.name)) declarePattern(d.id, 'var', scope); else d.id._b = scope.vars.get(d.id.name); });
    if (n.type === 'FunctionDeclaration') { const b = mkBinding(n.id.name, 'fn', scope, n); n.id._b = b; b.fn = n; return; }
    if (isFn(n)) return;
    kids(n).forEach(rec);
  })(body);
}
function predeclare(stmts, scope) {
  for (const st of stmts) if (st.type === 'VariableDeclaration' && st.kind !== 'var') { st._pre = true; st.declarations.forEach(d => { declarePattern(d.id, st.kind, scope); if (d.id.type === 'Identifier') d.id._b.declEnd = d.end; }); }
}
function resolve(n, scope) {
  if (!n || typeof n.type !== 'string') return;
  switch (n.type) {
    case 'Program': hoist(n, scope); predeclare(n.body, scope); n.body.forEach(s => resolve(s, scope)); return;
    case 'FunctionDeclaration': case 'FunctionExpression': case 'ArrowFunctionExpression': {
      const fs = new Scope(scope, n); n._scope = fs; fnInfos.push(n);
      n._params = [];
      n.params.forEach((p, i) => { declarePattern(p.type === 'AssignmentPattern' ? p.left : p, 'param', fs, b => { if (p.type === 'Identifier' || (p.type === 'AssignmentPattern' && p.left.type === 'Identifier')) n._params[i] = b; }); if (p.type === 'AssignmentPattern') resolve(p.right, fs); });
      if (n.body.type === 'BlockStatement') { hoist(n.body, fs); predeclare(n.body.body, fs); n.body.body.forEach(s => resolve(s, fs)); }
      else resolve(n.body, fs);
      return;
    }
    case 'BlockStatement': { const bs = new Scope(scope, scope.fn); n._scope = bs; predeclare(n.body, bs); n.body.forEach(s => resolve(s, bs)); return; }
    case 'ForStatement': case 'ForInStatement': case 'ForOfStatement': { const fs = new Scope(scope, scope.fn); n._scope = fs; kids(n).forEach(k => resolve(k, fs)); return; }
    case 'VariableDeclaration':
      n.declarations.forEach(d => {
        if (d.init) resolve(d.init, scope);
        if (n.kind !== 'var' && !n._pre) declarePattern(d.id, n.kind, scope);
        else if (n.kind !== 'var') {}
        else (function mark(p) { if (p.type === 'Identifier') { p._b = scope.lookup(p.name); } else if (p.type === 'ArrayPattern') p.elements.forEach(e => e && mark(e)); else if (p.type === 'ObjectPattern') p.properties.forEach(pp => mark(pp.value)); })(d.id);
      });
      return;
    case 'Identifier': { const b = scope.lookup(n.name); if (b && b.declEnd && n.start < b.declEnd && n !== b.node) b.fwd = true; if (b) n._b = b; else if (!(n.name in RUNTIME) && !EXT_FN.has(n.name) && !EXT_VAR.has(n.name)) fail(n, 'unknown global ' + n.name); return; }
    case 'MemberExpression': resolve(n.object, scope); if (n.computed) resolve(n.property, scope); return;
    case 'Property': if (n.computed) resolve(n.key, scope); resolve(n.value, scope); return;
    case 'LabeledStatement': case 'BreakStatement': case 'ContinueStatement': if (n.body) resolve(n.body, scope); return;
    default: kids(n).forEach(k => resolve(k, scope));
  }
}
(function unchain(n) { if (!n || typeof n.type !== 'string') return; for (const k in n) { const v = n[k]; if (Array.isArray(v)) v.forEach((c, i) => { if (c && c.type === 'ChainExpression') v[i] = c.expression; unchain(v[i]); }); else if (v && typeof v.type === 'string') { if (v.type === 'ChainExpression') n[k] = v.expression; unchain(n[k]); } } })(ast);
resolve(ast, globalScope);

// ------------------------------------------------------------------ type inference
// types: N S B C G D F(function) ; 'U' unknown
const CTX_PROPS = { fillStyle: 'D', strokeStyle: 'D', lineWidth: 'N', globalAlpha: 'N', globalCompositeOperation: 'S', font: 'S', textAlign: 'S', textBaseline: 'S', lineCap: 'S', lineJoin: 'S', shadowColor: 'D', shadowBlur: 'N', shadowOffsetX: 'N', shadowOffsetY: 'N', filter: 'S', imageSmoothingEnabled: 'B', miterLimit: 'N', imageSmoothingQuality: 'S', lineDashOffset: 'N' };
const CTX_METHODS = { createLinearGradient: 'G', createRadialGradient: 'G', measureText: 'D', createPattern: 'D', createImageData: 'D', getImageData: 'D' };
function joinVar(set) {
  const t = [...set].filter(x => x !== 'U');
  if (!t.length) return 'U';
  const s = new Set(t);
  if (s.size === 1) return t[0];
  if ([...s].every(x => x === 'N' || x === 'D' || x === 'B')) return s.has('N') ? 'N' : 'D';
  if ([...s].every(x => x === 'S' || x === 'D')) return 'D';
  return 'D';
}
function joinExpr(a, b) { if (a === 'U') return b; if (b === 'U') return a; if (a === b) return a; return 'D'; }
const bType = b => b.fixed || joinVar(b.types);

function T(n) {
  switch (n.type) {
    case 'Literal': return typeof n.value === 'number' ? 'N' : typeof n.value === 'string' ? 'S' : typeof n.value === 'boolean' ? 'B' : 'D';
    case 'TemplateLiteral': return 'S';
    case 'Identifier': if (n._b) { if (n._b.kind === 'fn') return 'F'; return bType(n._b); } { const r = RUNTIME[n.name]; return r === 'N' ? 'N' : 'D'; }
    case 'ThisExpression': return 'D';
    case 'ArrayExpression': case 'ObjectExpression': return 'D';
    case 'FunctionExpression': case 'ArrowFunctionExpression': return 'F';
    case 'UnaryExpression': return n.operator === '!' ? 'B' : n.operator === 'typeof' ? 'S' : n.operator === 'void' ? 'D' : 'N';
    case 'UpdateExpression': return 'N';
    case 'BinaryExpression': {
      if (['==', '===', '!=', '!==', '<', '>', '<=', '>=', 'in', 'instanceof'].includes(n.operator)) return 'B';
      if (n.operator === '+') { const a = T(n.left), b = T(n.right); if (a === 'S' || b === 'S') return 'S'; return 'N'; }
      return 'N';
    }
    case 'LogicalExpression': {
      const a = T(n.left), b = T(n.right);
      if (a === 'B' && b === 'B') return 'B';
      if (n.operator === '&&') return b === 'B' ? 'B' : (a === b ? a : (a === 'B' ? (b === 'N' ? 'D' : 'D') : 'D'));
      // ||, ??
      if (a === b) return a;
      if (a === 'D' || a === 'U') return b === 'U' ? 'D' : b; // (x.y || "#fff") -> S
      return 'D';
    }
    case 'ConditionalExpression': { const a = T(n.consequent), b = T(n.alternate); if (a === b) return a; if (a === 'U') return b; if (b === 'U') return a; if ((a === 'N' && b === 'D') || (a === 'D' && b === 'N')) return 'D'; return 'D'; }
    case 'AssignmentExpression': return n.operator === '=' ? T(n.right) : (n.operator === '+=' ? (T(n.left) === 'S' ? 'S' : 'N') : 'N');
    case 'SequenceExpression': return T(n.expressions[n.expressions.length - 1]);
    case 'MemberExpression': {
      const o = n.object;
      if (o.type === 'Identifier' && !o._b && o.name === 'Math') return 'N';
      if (o.type === 'Identifier' && !o._b && o.name === 'window') return 'D';
      if (!n.computed && n.property.name === 'length') return 'N';
      const ot = T(o);
      if (ot === 'C') { const p = n.property.name; if (p === 'canvas') return 'D'; return CTX_PROPS[p] || fail(n, 'ctx prop ' + p); }
      if (o.type === 'Identifier' && !o._b && RUNTIME[o.name] === 'NARR' && n.computed) return 'N';
      if (ot === 'S' && n.computed) return 'S';
      return 'D';
    }
    case 'CallExpression': {
      const c = n.callee;
      if (c.type === 'Identifier') {
        if (c._b && c._b.kind === 'fn') return c._b.fn._ret || 'U';
        if (c._b) { const f = c._b.fnNode; return f ? (f._ret || 'U') : 'D'; }
        if (c.name === 'Number' || c.name === 'parseInt' || c.name === 'parseFloat') return 'N';
        if (c.name === 'isFinite' || c.name === 'Boolean') return 'B';
        if (c.name === 'String') return 'S';
        return 'D';
      }
      if (c.type === 'MemberExpression' && !c.computed) {
        const m = c.property.name; const o = c.object;
        if (o.type === 'Identifier' && !o._b && o.name === 'Math') return 'N';
        if (o.type === 'Identifier' && !o._b && o.name === 'Array') return m === 'isArray' ? 'B' : 'D';
        if (o.type === 'Identifier' && !o._b && o.name === 'Number') return m === 'isFinite' || m === 'isNaN' || m === 'isInteger' ? 'B' : 'N';
        if (o.type === 'Identifier' && !o._b && o.name === 'performance') return 'N';
        if (m === 'getContext') return 'C';
        if (o.type === 'Identifier' && !o._b && o.name === 'Date') return 'N';
        if (o.type === 'Identifier' && !o._b && (o.name === 'console' || o.name === 'localStorage' && m === 'setItem')) return 'U0';
        const ot = T(o);
        if (ot === 'C') return CTX_METHODS[m] || 'U0';
        if (ot === 'G') return 'U0';
        if (m === 'toFixed' || m === 'toString' || m === 'join') return 'S';
        if (ot === 'S' && (m === 'toUpperCase' || m === 'toLowerCase' || m === 'padStart' || m === 'charAt' || m === 'repeat' || m === 'slice' || m === 'substring')) return 'S';
        if (m === 'indexOf' || m === 'push' || m === 'findIndex' || (m === 'charCodeAt' && ot === 'S')) return 'N';
        if (m === 'includes' || m === 'some' || m === 'every') return 'B';
        if (m === 'reduce') return n.arguments.length > 1 ? T(n.arguments[1]) : 'D';
        return 'D';
      }
      return 'D';
    }
    case 'NewExpression': return 'D';
    default: fail(n, 'type of ' + n.type);
  }
}

// gather assignments
const calls = []; // [fnNode, argsNodes]
function gather(n) {
  if (!n || typeof n.type !== 'string') return;
  if (n.type === 'VariableDeclarator') {
    if (n.id.type === 'Identifier') {
      const b = n.id._b;
      if (n.init) { b.assigned.push(n.init); if (isFn(n.init) && b.kind === 'const' && !b.fwd) { b.fnNode = n.init; n.init._name = b; } }
      else if (b.kind !== 'var') b.assigned.push({ type: 'Identifier', name: 'undefined' });
    } else { // destructuring -> D
      (function mark(p) { if (p.type === 'Identifier') p._b.types.add('D'); else if (p.type === 'ArrayPattern') p.elements.forEach(e => e && mark(e)); else if (p.type === 'ObjectPattern') p.properties.forEach(pp => mark(pp.value)); else if (p.type === 'AssignmentPattern') mark(p.left); })(n.id);
    }
  }
  if (n.type === 'AssignmentExpression' && n.left.type === 'Identifier' && n.left._b) {
    if (n.operator === '=') n.left._b.assigned.push(n.right); else n.left._b.assignedOp = (n.left._b.assignedOp || []).concat([n]);
  }
  if (n.type === 'CallExpression') calls.push(n);
  kids(n).forEach(gather);
}
gather(ast);

// callbacks of array methods / Array.from have fixed param types
function markCallbackParams() {
  for (const c of calls) {
    const cal = c.callee;
    if (cal.type !== 'MemberExpression' || cal.computed) continue;
    const m = cal.property.name;
    const cb = c.arguments.find(isFn);
    if (!cb) continue;
    const isArrFrom = cal.object.type === 'Identifier' && cal.object.name === 'Array' && m === 'from';
    if (!['forEach', 'map', 'filter', 'some', 'every', 'find', 'findIndex', 'sort', 'reduce', 'from'].includes(m)) continue;
    cb._cbKind = isArrFrom ? 'from' : m;
    const ps = cb._params || [];
    const types = m === 'sort' ? ['D', 'D'] : m === 'reduce' ? ['D', 'D', 'N'] : ['D', 'N', 'D'];
    cb.params.forEach((p, i) => { if (p.type === 'Identifier') p._b.fixed = types[i] || 'D'; else { (function mark(q) { if (q.type === 'Identifier') q._b.fixed = 'D'; else if (q.type === 'ArrayPattern') q.elements.forEach(e => e && mark(e)); else if (q.type === 'ObjectPattern') q.properties.forEach(pp => mark(pp.value)); else if (q.type === 'AssignmentPattern') mark(q.left); })(p); } });
  }
}
markCallbackParams();

// fixpoint
const seedTypes = JSON.parse(process.argv[6] || '{}'); // fnName -> [param types]
for (let iter = 0; iter < 40; iter++) {
  let changed = false;
  const snap = allBindings.map(b => bType(b)).join();
  for (const b of allBindings) {
    if (b.fixed) continue;
    const s = new Set();
    for (const a of b.assigned) s.add(T(a));
    if (b.assignedOp) for (const a of b.assignedOp) s.add(a.operator === '+=' && joinVar(b.types) === 'S' ? 'S' : 'N');
    if (b.kind === 'param') for (const t of (b.callTypes || [])) s.add(t);
    if (b.kind === 'fn' || (b.fnNode)) s.add('F');
    b.types = s;
  }
  // params from call sites
  for (const b of allBindings) if (b.kind === 'param') b.callTypes = [];
  for (const c of calls) {
    let fn = null;
    if (c.callee.type === 'Identifier' && c.callee._b) fn = c.callee._b.fn || c.callee._b.fnNode;
    if (!fn) continue;
    fn._params.forEach((pb, i) => { if (!pb) return; const a = c.arguments[i]; pb.callTypes.push(a ? T(a) : 'U'); });
  }
  for (const [name, tys] of Object.entries(seedTypes)) {
    const b = globalScope.vars.get(name); if (!b || !b.fn) continue;
    b.fn._params.forEach((pb, i) => pb && pb.callTypes.push(tys[i] || 'D'));
  }
  // return types
  for (const f of fnInfos) {
    let r = 'U';
    if (f.body.type !== 'BlockStatement') r = T(f.body);
    else (function rec(n) { if (!n || typeof n.type !== 'string') return; if (n !== f && isFn(n)) return; if (n.type === 'ReturnStatement') r = n.argument ? joinExpr(r, T(n.argument)) : joinExpr(r, 'V'); kids(n).forEach(rec); })(f.body);
    if (r === 'V') r = 'U0';
    if (r !== 'U' && r !== 'U0' && f.body.type === 'BlockStatement') { let hasVoid = false; (function rec(n) { if (!n || typeof n.type !== 'string') return; if (n !== f && isFn(n)) return; if (n.type === 'ReturnStatement' && !n.argument) hasVoid = true; kids(n).forEach(rec); })(f.body); if (hasVoid) r = 'D'; }
    if (f._ret !== r) { f._ret = r; changed = true; }
  }
  if (snap !== allBindings.map(b => bType(b)).join()) changed = true;
  if (!changed) break;
}
for (const b of allBindings) { let t = bType(b); if (t === 'U') t = 'D'; b.t = t; }
for (const b of allBindings) if (b.kind === 'param' && b.t === 'N') {
  // ctx params: methods called on them
}
// detect ctx-typed params: any param on which a canvas method is called
const CTX_SIGNS = new Set(['beginPath', 'fillRect', 'moveTo', 'lineTo', 'arc', 'createLinearGradient', 'createRadialGradient', 'save', 'restore', 'fill', 'stroke', 'strokeRect', 'ellipse', 'translate', 'rotate']);
let ctxChanged = true;
while (ctxChanged) {
  ctxChanged = false;
  for (const c of calls) {
    const cal = c.callee;
    if (cal.type === 'MemberExpression' && !cal.computed && cal.object.type === 'Identifier' && cal.object._b && CTX_SIGNS.has(cal.property.name) && cal.object._b.t !== 'C') { cal.object._b.t = 'C'; cal.object._b.fixed = 'C'; ctxChanged = true; }
  }
  // propagate ctx to params receiving ctx args
  for (const c of calls) {
    let fn = null;
    if (c.callee.type === 'Identifier' && c.callee._b) fn = c.callee._b.fn || c.callee._b.fnNode;
    if (!fn) continue;
    fn._params.forEach((pb, i) => { const a = c.arguments[i]; if (pb && a && a.type === 'Identifier' && a._b && a._b.t === 'C' && pb.t !== 'C') { pb.t = 'C'; pb.fixed = 'C'; ctxChanged = true; } });
  }
}
// re-run inference with ctx fixed (return types depending on ctx methods)
for (let iter = 0; iter < 20; iter++) {
  let changed = false;
  for (const b of allBindings) {
    if (b.fixed) { b.t = b.fixed; continue; }
    const s = new Set();
    for (const a of b.assigned) s.add(T(a));
    if (b.assignedOp) for (const a of b.assignedOp) s.add(a.operator === '+=' && joinVar(b.types) === 'S' ? 'S' : 'N');
    if (b.kind === 'param') for (const t of (b.callTypes || [])) s.add(t);
    if (b.kind === 'fn' || b.fnNode) s.add('F');
    let t = joinVar(s); if (t === 'U') t = 'D';
    b.types = s;
    if (t !== b.t) { b.t = t; changed = true; }
  }
  for (const b of allBindings) if (b.kind === 'param' && !b.fixed) b.callTypes = [];
  for (const c of calls) {
    let fn = null;
    if (c.callee.type === 'Identifier' && c.callee._b) fn = c.callee._b.fn || c.callee._b.fnNode;
    if (!fn) continue;
    fn._params.forEach((pb, i) => { if (!pb || pb.fixed) return; const a = c.arguments[i]; pb.callTypes.push(a ? T(a) : 'U'); });
  }
  for (const [name, tys] of Object.entries(seedTypes)) {
    const b = globalScope.vars.get(name); if (!b || !b.fn) continue;
    b.fn._params.forEach((pb, i) => pb && !pb.fixed && pb.callTypes.push(tys[i] || 'D'));
  }
  for (const f of fnInfos) {
    let r = 'U';
    if (f.body.type !== 'BlockStatement') r = T(f.body);
    else (function rec(n) { if (!n || typeof n.type !== 'string') return; if (n !== f && isFn(n)) return; if (n.type === 'ReturnStatement') r = n.argument ? joinExpr(r, T(n.argument)) : joinExpr(r, 'V'); kids(n).forEach(rec); })(f.body);
    if (r === 'V') r = 'U0';
    if (r !== 'U' && r !== 'U0' && f.body.type === 'BlockStatement') { let hasVoid = false; (function rec(n) { if (!n || typeof n.type !== 'string') return; if (n !== f && isFn(n)) return; if (n.type === 'ReturnStatement' && !n.argument) hasVoid = true; kids(n).forEach(rec); })(f.body); if (hasVoid) r = 'D'; }
    if (f._ret !== r) { f._ret = r; changed = true; }
  }
  if (!changed) break;
}
for (const b of allBindings) { if (b.fixed) b.t = b.fixed; if (b.t === 'U' || b.t === 'U0') b.t = 'D'; if (b.t === 'F' && !b.fn && !b.fnNode) b.t = 'D'; }
// parameters never called with info: number by default when only used arithmetically is unknowable -> keep D
// ------------------------------------------------------------------ emit
const KT = { N: 'Double', S: 'String', B: 'Boolean', C: 'Ctx', G: 'Grad', D: 'Any?' };
const DEF = { N: '0.0', S: '""', B: 'false', C: 'null', G: 'null', D: 'null' };
const UNDEF = { N: 'Double.NaN', S: '"undefined"', B: 'false', D: 'null' };
function retKt(f) { const r = f._ret; if (!r || r === 'U' || r === 'U0') return 'Unit'; return KT[r] || 'Any?'; }
function numLit(v) { if (!isFinite(v)) return v > 0 ? 'Double.POSITIVE_INFINITY' : 'Double.NEGATIVE_INFINITY'; let s = String(v); if (/e/.test(s)) { s = v.toExponential(); s = s.replace('e+', 'E').replace('e-', 'E-'); if (!/\./.test(s.split('E')[0])) s = s.replace('E', '.0E'); return s; } return /\./.test(s) ? s : s + '.0'; }
function strLit(s) { return '"' + s.replace(/\\/g, '\\\\').replace(/"/g, '\\"').replace(/\$/g, '\\$').replace(/\n/g, '\\n') + '"'; }
function co(code, from, to) {
  if (to === 'ANY' || to === 'D') return code;
  if (from === to) return code;
  if (to === 'N') return from === 'B' ? `(if (${code}) 1.0 else 0.0)` : `num(${code})`;
  if (to === 'S') return from === 'N' ? `jsStr(${code})` : `str(${code})`;
  if (to === 'B') return `truthy(${code})`;
  if (to === 'C') return `(${code} as Ctx)`;
  if (to === 'G') return `(${code} as Grad)`;
  return code;
}
let lbl = 0;
const loopStack = []; // {brk, cont}
let curFn = null;

function E(n, want) { // returns code coerced to `want` (N,S,B,D,C,G or undefined= natural)
  const t = T(n);
  const c = E0(n);
  if (!want) return c;
  const tt = (t === 'U' || t === 'U0') ? 'D' : t;
  if (tt === 'F') return c;
  if (tt === 'G' && want === 'D') return c;
  if (tt === 'C' && want === 'D') return c;
  return co(c, tt, want);
}
function nat(n) { const t = T(n); return (t === 'U' || t === 'U0' || t === 'F') ? 'D' : t; }
function bindRef(b) { if (b.t === 'C' && b.hoistedNullable) return b.kname + '!!'; if (b.t === 'G' && b.hoistedNullable) return b.kname + '!!'; return b.kname; }

function E0(n) {
  switch (n.type) {
    case 'Literal':
      if (typeof n.value === 'number') return numLit(n.value);
      if (typeof n.value === 'string') return strLit(n.value);
      if (typeof n.value === 'boolean') return String(n.value);
      if (n.value === null) return 'null';
      fail(n, 'literal');
    case 'TemplateLiteral': {
      const parts = [];
      n.quasis.forEach((q, i) => { if (q.value.cooked) parts.push(strLit(q.value.cooked)); if (i < n.expressions.length) parts.push(E(n.expressions[i], 'S')); });
      return '(' + (parts.length ? parts.join(' + ') : '""') + ')';
    }
    case 'Identifier':
      if (n._b) { if (n._b.kind === 'fn') return `::${n._b.kname}`; return bindRef(n._b); }
      if (n.name === 'undefined') return 'null';
      if (n.name === 'Infinity') return 'Double.POSITIVE_INFINITY';
      if (n.name === 'NaN') return 'Double.NaN';
      if (n.name === 'mouseX' || n.name === 'mouseY') return 'Rt.' + n.name;
      if (n.name === 'freqData' || n.name === 'timeData') return 'Rt.' + n.name + 'Arr';
      if (EXT_VAR.has(n.name)) return `Ext.v(${strLit(n.name)})`;
      if (EXT_FN.has(n.name)) return `Ext.fnRef(${strLit(n.name)})`;
      if (n.name === 'innerWidth' || n.name === 'innerHeight') return `Dom.${n.name}()`;
      fail(n, 'bare global');
    case 'ThisExpression': fail(n, 'this');
    case 'ArrayExpression': return 'jsArr(' + n.elements.map(e => E(e, 'D')).join(', ') + ')';
    case 'ObjectExpression': return 'jsObj(' + n.properties.map(p => { const k = p.key.type === 'Identifier' ? p.key.name : String(p.key.value); return strLit(k) + ' to ' + E(p.value, 'D'); }).join(', ') + ')';
    case 'UnaryExpression': {
      const op = n.operator;
      if (op === '!') return '!' + par(E(n.argument, 'B'));
      if (op === '-') return '-' + par(E(n.argument, 'N'));
      if (op === '+') return E(n.argument, 'N');
      if (op === '~') return `(jsInt(${E(n.argument, 'N')}).inv().toDouble())`;
      if (op === 'typeof') { const a = n.argument; if (a.type === 'Identifier' && !a._b && (EXT_FN.has(a.name) || EXT_VAR.has(a.name))) return `Ext.typeOf(${strLit(a.name)})`; if (a.type === 'MemberExpression' && a.object.type === 'Identifier' && a.object.name === 'window' && !globalScope.vars.has(a.property.name)) return `jsTypeOf(Rt.win(${strLit(a.property.name)}))`; return strLit(typeOfStatic(a)); }
      if (op === 'void') return 'null';
      fail(n, 'unary ' + op);
    }
    case 'UpdateExpression': {
      const tgt = n.argument;
      if (tgt.type === 'Identifier' && !tgt._b) return `Ext.postAdd(${strLit(tgt.name)}, ${n.operator === '++' ? '1.0' : '-1.0'})`;
      if (tgt.type === 'Identifier') { const k = tgt._b.kname; const b = tgt._b; if (b.t !== 'N') fail(n, 'update non-number'); return n.prefix ? `${n.operator}${k}` : `${k}${n.operator}`; }
      // member: o.x++ -> postInc helper
      const d = n.operator === '++' ? '1.0' : '-1.0';
      const [o, key] = memberParts(tgt);
      return n.prefix ? `jsPreAdd(${o}, ${key}, ${d})` : `jsPostAdd(${o}, ${key}, ${d})`;
    }
    case 'BinaryExpression': return binary(n);
    case 'LogicalExpression': {
      const t = T(n);
      if (t === 'B') return `(${E(n.left, 'B')} ${n.operator === '&&' ? '&&' : '||'} ${E(n.right, 'B')})`;
      if (n.operator === '&&') return `jsAnd(${E(n.left, 'D')}) { ${E(n.right, 'D')} }`;
      // || / ??
      const a = nat(n.left);
      if (t === 'D') return n.operator === '??' ? `jsNullish(${E(n.left, 'D')}) { ${E(n.right, 'D')} }` : `jsOr(${E(n.left, 'D')}) { ${E(n.right, 'D')} }`;
      if (a === t) return n.operator === '??' ? `(${E(n.left)})` : `jsOr${t}(${E(n.left)}) { ${E(n.right, t)} }`;
      return n.operator === '??' ? `(jsNullish(${E(n.left, 'D')}) { ${E(n.right, 'D')} }).let { ${co('it', 'D', t)} }` : `jsOr${t}D(${E(n.left, 'D')}) { ${E(n.right, t)} }`;
    }
    case 'ConditionalExpression': {
      const t = nat(n);
      return `(if (${E(n.test, 'B')}) ${E(n.consequent, t)} else ${E(n.alternate, t)})`;
    }
    case 'AssignmentExpression': return `(${assignExpr(n)})`;
    case 'SequenceExpression': return `run { ${n.expressions.slice(0, -1).map(e => stmtExpr(e)).join('; ')}; ${E(n.expressions[n.expressions.length - 1])} }`;
    case 'MemberExpression': return member(n);
    case 'CallExpression': return call(n);
    case 'ArrowFunctionExpression': case 'FunctionExpression': return lambda(n, 'D');
    case 'NewExpression': {
      const cn = n.callee.name;
      if (/^(Float32|Float64|Uint8|Int16|Int32|Uint16|Uint8Clamped)Array$/.test(cn)) return n.arguments[0] && nat(n.arguments[0]) === 'N' ? `jsZeros(${E(n.arguments[0], 'N')})` : `jsArrayFromArr(${n.arguments[0] ? E(n.arguments[0], 'D') : 'null'})`;
      if (cn === 'Array') return `jsZeros(${E(n.arguments[0], 'N')}, null)`;
      if (cn === 'Map') return 'JsObj()';
      if (cn === 'Set') return 'JsArr()';
      if (cn === 'Date') return `Rt.newDate(${n.arguments.map(a => E(a, 'D')).join(', ')})`;
      fail(n, 'new ' + cn);
    }
    default: fail(n, 'expr ' + n.type);
  }
}
function par(c) { return /^[\w.!]+$/.test(c) || /^\(.*\)$/.test(c) && balanced(c) ? c : '(' + c + ')'; }
function balanced(c) { let d = 0; for (let i = 0; i < c.length; i++) { if (c[i] === '(') d++; else if (c[i] === ')') { d--; if (d === 0 && i < c.length - 1) return false; } } return true; }
function typeOfStatic(a) {
  if (a.type === 'Identifier') {
    if (a._b) return a._b.kind === 'fn' || a._b.fnNode ? 'function' : ({ N: 'number', S: 'string', B: 'boolean' }[a._b.t] || 'object');
    if (a.name === 'mouseX' || a.name === 'mouseY') return 'number';
    if (a.name === 'freqData' || a.name === 'timeData') return 'object';
    return 'undefined';
  }
  if (a.type === 'MemberExpression' && a.object.type === 'Identifier' && a.object.name === 'window') return globalScope.vars.has(a.property.name) ? 'object' : 'undefined';
  return 'object';
}
function binary(n) {
  const op = n.operator;
  const lt = nat(n.left), rt = nat(n.right);
  if (op === '+') {
    if (T(n) === 'S') return `(${E(n.left, 'S')} + ${E(n.right, 'S')})`;
    return `(${E(n.left, 'N')} + ${E(n.right, 'N')})`;
  }
  if (['-', '*', '/', '%'].includes(op)) return `(${E(n.left, 'N')} ${op} ${E(n.right, 'N')})`;
  if (op === '**') return `Math.pow(${E(n.left, 'N')}, ${E(n.right, 'N')})`;
  const bit = { '&': 'and', '|': 'or', '^': 'xor', '<<': 'shl', '>>': 'shr', '>>>': 'ushr' }[op];
  if (op === '>>>') return `((jsInt(${E(n.left, 'N')}) ushr jsInt(${E(n.right, 'N')})).toLong() and 0xFFFFFFFFL).toDouble()`;
  if (bit) return `(jsInt(${E(n.left, 'N')}) ${bit} jsInt(${E(n.right, 'N')})).toDouble()`;
  if (['<', '>', '<=', '>='].includes(op)) {
    if (lt === 'S' && rt === 'S') return `(${E(n.left)} ${op} ${E(n.right)})`;
    return `(${E(n.left, 'N')} ${op} ${E(n.right, 'N')})`;
  }
  if (['===', '==', '!==', '!='].includes(op)) {
    const neg = op[0] === '!';
    let code;
    const isNullish = x => (x.type === 'Literal' && x.value === null) || (x.type === 'Identifier' && x.name === 'undefined' && !x._b);
    // a number that is "undefined" (a missing argument) is NaN here: x == null / x === undefined test that
    if (lt === 'N' && (isNullish(n.right) && (op === '==' || op === '!=' || n.right.type === 'Identifier'))) code = `(${E(n.left)}).isNaN()`;
    else if (rt === 'N' && (isNullish(n.left) && (op === '==' || op === '!=' || n.left.type === 'Identifier'))) code = `(${E(n.right)}).isNaN()`;
    else if (lt === 'N' && rt === 'N') code = `(${E(n.left)} == ${E(n.right)})`;
    else if (lt === 'S' && rt === 'S') code = `(${E(n.left)} == ${E(n.right)})`;
    else if (lt === 'B' && rt === 'B') code = `(${E(n.left)} == ${E(n.right)})`;
    else code = `jsEq(${E(n.left, 'D')}, ${E(n.right, 'D')})`;
    return neg ? `!${code}` : code;
  }
  if (op === 'in') return `jsHas(${E(n.right, 'D')}, ${E(n.left, 'D')})`;
  fail(n, 'binop ' + op);
}
function memberParts(m) { // for dynamic objects
  const o = E(m.object, 'D');
  const key = m.computed ? E(m.property, 'D') : strLit(m.property.name);
  return [o, key];
}
function member(n) {
  const o = n.object;
  if (o.type === 'Identifier' && !o._b && o.name === 'Math') {
    const p = n.property.name;
    return { PI: 'Math.PI', E: 'Math.E', LN2: 'Math.log(2.0)', SQRT2: 'Math.sqrt(2.0)', LN10: 'Math.log(10.0)' }[p] || fail(n, 'Math.' + p);
  }
  if (o.type === 'Identifier' && !o._b && o.name === 'document') { const p = n.property.name; if (p === 'hidden') return 'false'; return `Dom.${p}`; }
  if (o.type === 'Identifier' && !o._b && o.name === 'window' && globalScope.vars.has(n.property.name)) return globalScope.vars.get(n.property.name).kname;
  if (o.type === 'Identifier' && !o._b && o.name === 'window') return `Rt.win(${strLit(n.property.name)})`;
  if (o.type === 'Identifier' && !o._b && o.name === 'Number') return { MAX_VALUE: 'Double.MAX_VALUE', EPSILON: 'Math.ulp(1.0)' }[n.property.name] || fail(n, 'Number.');
  if (o.type === 'Identifier' && !o._b && (o.name === 'freqData' || o.name === 'timeData')) {
    if (!n.computed) { if (n.property.name === 'length') return `Rt.${o.name}Arr.size.toDouble()`; fail(n, 'arr prop'); }
    return `Rt.${o.name}Arr[jsIdx(${E(n.property, 'N')})]`;
  }
  const ot = nat(o);
  if (!n.computed && n.property.name === 'length') {
    if (ot === 'S') return `${par(E(o))}.length.toDouble()`;
    return `jsLen(${E(o, 'D')})`;
  }
  if (ot === 'C') { if (n.property.name === 'canvas') return `${E(o)}.canvasInfo`; return `${E(o)}.${n.property.name}`; }
  if (ot === 'S' && n.computed) return `jsCharAt(${E(o)}, ${E(n.property, 'N')})`;
  const [oo, key] = memberParts(n);
  return `jsGet(${oo}, ${key})`;
}
function argsOf(n, types) { return n.arguments.map((a, i) => E(a, types ? (types[i] || 'D') : 'D')); }
function call(n) {
  const c = n.callee;
  if (c.type === 'Identifier') {
    if (c._b && (c._b.kind === 'fn' || c._b.fnNode)) {
      const fn = c._b.fn || c._b.fnNode;
      const ps = fn._params;
      const args = fn.params.map((p, i) => { const pb = ps[i]; const a = n.arguments[i]; if (!a) return null; return E(a, pb ? pb.t : 'D'); });
      while (args.length && args[args.length - 1] === null) args.pop();
      if (args.includes(null)) fail(n, 'hole args');
      return `${c._b.kname}(${args.join(', ')})`;
    }
    if (c._b) return `jsCall(${bindRef(c._b)}${n.arguments.map(a => ', ' + E(a, 'D')).join('')})`;
    if (EXT_FN.has(c.name)) return `Ext.call(${strLit(c.name)}${n.arguments.map(a => ', ' + E(a, 'D')).join('')})`;
    if (c.name === 'getComputedStyle') return `Dom.computedStyle(${E(n.arguments[0], 'D')})`;
    if (c.name === 'setTimeout') return `Rt.setTimeout(${E(n.arguments[0], 'D')}, ${n.arguments[1] ? E(n.arguments[1], 'N') : '0.0'})`;
    if (c.name === 'clearTimeout') return `Rt.clearTimeout(${E(n.arguments[0], 'D')})`;
    if (c.name === 'requestAnimationFrame') return `Rt.setTimeout(${E(n.arguments[0], 'D')}, 16.0)`;
    if (c.name === 'Number') return n.arguments.length ? E(n.arguments[0], 'N') : '0.0';
    if (c.name === 'parseInt') return n.arguments.length > 1 ? `jsParseInt(${E(n.arguments[0], 'D')}, ${E(n.arguments[1], 'N')})` : `jsParseInt(${E(n.arguments[0], 'D')})`;
    if (c.name === 'parseFloat') return `jsParseFloat(${E(n.arguments[0], 'D')})`;
    if (c.name === 'isFinite') return `${par(E(n.arguments[0], 'N'))}.isFinite()`;
    if (c.name === 'String') return E(n.arguments[0], 'S');
    if (c.name === 'Boolean') return E(n.arguments[0], 'B');
    fail(n, 'call global ' + c.name);
  }
  if (c.type !== 'MemberExpression') fail(n, 'callee');
  const o = c.object; const m = c.computed ? null : c.property.name;
  if (!m) fail(n, 'computed call');
  if (o.type === 'Identifier' && !o._b && o.name === 'Math') {
    const a = argsOf(n, n.arguments.map(() => 'N'));
    const map = { sin: 'sin', cos: 'cos', tan: 'tan', atan2: 'atan2', atan: 'atan', asin: 'asin', acos: 'acos', abs: 'abs', sqrt: 'sqrt', floor: 'floor', ceil: 'ceil', exp: 'exp', log: 'ln', pow: 'pow', cbrt: 'cbrt', log2: 'log2', log10: 'log10', sign: 'sign', trunc: 'truncate', sinh: 'sinh', cosh: 'cosh', tanh: 'tanh' };
    if (m === 'random') return 'Rt.random()';
    if (m === 'round') return `jsRound(${a[0]})`;
    if (m === 'max' || m === 'min') return a.length === 2 ? `jsM${m === 'max' ? 'ax' : 'in'}(${a.join(', ')})` : `jsM${m === 'max' ? 'ax' : 'in'}N(${a.join(', ')})`;
    if (m === 'hypot') return a.length === 2 ? `Math.hypot(${a.join(', ')})` : `jsHypot(${a.join(', ')})`;
    if (m === 'pow') return `Math.pow(${a.join(', ')})`;
    if (map[m]) return `kotlin.math.${map[m]}(${a.join(', ')})`;
    fail(n, 'Math.' + m);
  }
  if (o.type === 'Identifier' && !o._b && o.name === 'Array' && m === 'from') {
    const src0 = n.arguments[0];
    const cb = n.arguments[1];
    let len;
    if (src0.type === 'ObjectExpression' && src0.properties.length === 1 && src0.properties[0].key.name === 'length') len = E(src0.properties[0].value, 'N');
    else len = `jsLen(${E(src0, 'D')})`;
    if (!cb) return `jsArrayFromArr(${E(src0, 'D')})`;
    return `jsArrayFrom(${len}) ${lambda(cb, 'D', true)}`;
  }
  if (o.type === 'Identifier' && !o._b && o.name === 'Array' && m === 'isArray') return `jsIsArray(${E(n.arguments[0], 'D')})`;
  if (o.type === 'Identifier' && !o._b && o.name === 'Number') return `jsNumberFn_${m}(${argsOf(n).join(', ')})`;
  if (o.type === 'Identifier' && !o._b && o.name === 'performance' && m === 'now') return 'Rt.nowMs()';
  if (o.type === 'Identifier' && !o._b && o.name === 'Date' && m === 'now') return 'Rt.dateNow()';
  if (o.type === 'Identifier' && !o._b && o.name === 'console') return 'Unit';
  if (o.type === 'Identifier' && !o._b && o.name === 'localStorage') return `Rt.ls_${m}(${argsOf(n).join(', ')})`;
  if (o.type === 'Identifier' && !o._b && o.name === 'JSON') return `Rt.json_${m}(${argsOf(n).join(', ')})`;
  if (o.type === 'Identifier' && !o._b && o.name === 'Object') return `Rt.object_${m}(${argsOf(n).join(', ')})`;
  if (o.type === 'Identifier' && !o._b && o.name === 'document') return `Dom.${m}(${argsOf(n).join(', ')})`;
  if (o.type === 'Identifier' && !o._b && o.name === 'window') return `Ext.call(${strLit('window.' + m)}${n.arguments.map(a => ', ' + E(a, 'D')).join('')})`;
  if (m === 'getContext') return `jsGetContext(${E(o, 'D')})`;
  const ot = nat(o);
  if (ot === 'C') {
    const sig = CTX_SIG[m]; if (!sig) fail(n, 'ctx method ' + m);
    return `${E(o)}.${m}(${argsOf(n, sig).join(', ')})`;
  }
  if (ot === 'G') { if (m !== 'addColorStop') fail(n, 'grad method'); return `${E(o)}.addColorStop(${argsOf(n, ['N', 'D']).join(', ')})`; }
  // number / string methods
  if (m === 'toFixed') return `jsToFixed(${E(o, 'N')}, ${n.arguments.length ? E(n.arguments[0], 'N') : '0.0'})`;
  if (m === 'toString' && ot === 'N') return `jsStr(${E(o)})`;
  if (m === 'toString') return `str(${E(o, 'D')})`;
  if (ot === 'S') {
    const s = E(o);
    if (m === 'toUpperCase') return `${par(s)}.uppercase()`;
    if (m === 'toLowerCase') return `${par(s)}.lowercase()`;
    if (m === 'charAt') return `jsCharAt(${s}, ${E(n.arguments[0], 'N')})`;
    if (m === 'charCodeAt') return `jsCharCode(${s}, ${n.arguments.length ? E(n.arguments[0], 'N') : '0.0'})`;
    if (m === 'padStart') return `${par(s)}.padStart(jsIdx(${E(n.arguments[0], 'N')}), ${n.arguments[1] ? E(n.arguments[1], 'S') : '" "'}[0])`;
    if (m === 'repeat') return `${par(s)}.repeat(jsIdx(${E(n.arguments[0], 'N')}))`;
    if (m === 'includes') return `${par(s)}.contains(${E(n.arguments[0], 'S')})`;
    if (m === 'indexOf') return `${par(s)}.indexOf(${E(n.arguments[0], 'S')}).toDouble()`;
    if (m === 'slice' || m === 'substring') return `jsSlice(${s}, ${argsOf(n, ['N', 'N']).join(', ')})`;
    if (m === 'split') return `jsSplit(${s}, ${E(n.arguments[0], 'S')})`;
  }
  // array methods on dynamic
  const recv = E(o, 'D');
  const cbArg = n.arguments.find(isFn);
  switch (m) {
    case 'forEach': case 'map': case 'filter': case 'some': case 'every': case 'find': case 'findIndex': {
      const cb = n.arguments[0];
      if (isFn(cb)) return `js_${m}(${recv}) ${lambda(cb, 'D', true)}`;
      if (cb.type === 'Identifier' && cb._b && (cb._b.fn || cb._b.fnNode)) {
        const fnode = cb._b.fn || cb._b.fnNode;
        const call = `${cb._b.kname}(${fnode._params.map((p, i) => i === 0 ? co('a0', 'D', p.t) : i === 1 ? co('a1', 'N', p.t) : 'null').join(', ')})`;
        // map / filter / some / every / find use the callback's value; forEach ignores it
        const usesValue = m !== 'forEach';
        return usesValue ? `js_${m}(${recv}) { a0, a1, _ -> ${co(call, fnode._ret && fnode._ret !== 'U' && fnode._ret !== 'U0' ? fnode._ret : 'D', 'D')} }` : `js_${m}(${recv}) { a0, a1, _ -> ${call}; null }`;
      }
      fail(n, 'callback');
    }
    case 'sort': return n.arguments.length ? `js_sort(${recv}) ${lambda(n.arguments[0], 'D', true)}` : `js_sortDefault(${recv})`;
    case 'reduce': return `js_reduce(${recv}, ${E(n.arguments[1], 'D')}) ${lambda(n.arguments[0], 'D', true)}`;
    case 'push': return `jsPush(${recv}, ${argsOf(n).join(', ')})`;
    case 'pop': return `jsPop(${recv})`;
    case 'shift': return `jsShift(${recv})`;
    case 'unshift': return `jsUnshift(${recv}, ${argsOf(n).join(', ')})`;
    case 'splice': return `jsSplice(${recv}, ${argsOf(n, ['N', 'N']).join(', ')})`;
    case 'slice': return `jsSliceArr(${recv}${n.arguments.length ? ', ' + argsOf(n, ['N', 'N']).join(', ') : ''})`;
    case 'concat': return `jsConcat(${recv}, ${argsOf(n).join(', ')})`;
    case 'reverse': return `jsReverse(${recv})`;
    case 'join': return `jsJoin(${recv}, ${n.arguments.length ? E(n.arguments[0], 'S') : '","'})`;
    case 'includes': return `jsIncludes(${recv}, ${E(n.arguments[0], 'D')})`;
    case 'indexOf': return `jsIndexOf(${recv}, ${E(n.arguments[0], 'D')})`;
    case 'fill': return `jsFill(${recv}, ${E(n.arguments[0], 'D')})`;
  }
  return `jsInvoke(${recv}, ${strLit(m)}${n.arguments.map(a => ', ' + (isFn(a) ? lambda(a, 'D') : E(a, 'D'))).join('')})`;
}
const CTX_SIG = {
  save: [], restore: [], beginPath: [], closePath: [], fill: ['S'], stroke: [], clip: ['S'],
  moveTo: ['N', 'N'], lineTo: ['N', 'N'], quadraticCurveTo: ['N', 'N', 'N', 'N'], bezierCurveTo: ['N', 'N', 'N', 'N', 'N', 'N'],
  arc: ['N', 'N', 'N', 'N', 'N', 'B'], arcTo: ['N', 'N', 'N', 'N', 'N'], ellipse: ['N', 'N', 'N', 'N', 'N', 'N', 'N', 'B'], rect: ['N', 'N', 'N', 'N'],
  fillRect: ['N', 'N', 'N', 'N'], strokeRect: ['N', 'N', 'N', 'N'], clearRect: ['N', 'N', 'N', 'N'],
  translate: ['N', 'N'], rotate: ['N'], scale: ['N', 'N'], setTransform: ['N', 'N', 'N', 'N', 'N', 'N'], transform: ['N', 'N', 'N', 'N', 'N', 'N'], resetTransform: [],
  createLinearGradient: ['N', 'N', 'N', 'N'], createRadialGradient: ['N', 'N', 'N', 'N', 'N', 'N'],
  fillText: ['S', 'N', 'N', 'N'], strokeText: ['S', 'N', 'N', 'N'], measureText: ['S'], setLineDash: ['D'], drawImage: ['D', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N'], shiftBand: ['N', 'N', 'N', 'N', 'N'],
  roundRect: ['N', 'N', 'N', 'N', 'D'],
  createPattern: ['D', 'S'], createImageData: ['N', 'N'], putImageData: ['D', 'N', 'N'], getImageData: ['N', 'N', 'N', 'N'],
};

function lambda(f, retWant, trailing) {
  // trailing lambda for js_* helpers: params all typed by callback kind
  const kind = f._cbKind;
  const ps = f.params;
  if (!kind) return genericFn(f);
  const label = 'L' + (lbl++);
  const prevFn = curFn; curFn = { node: f, label, lambda: true };
  const pdecl = []; const pre = [];
  const ptypes = kind === 'sort' ? ['D', 'D'] : kind === 'reduce' ? ['D', 'D', 'N', 'D'] : ['D', 'N', 'D'];
  const kparams = [];
  const maxP = kind === 'sort' ? 2 : kind === 'reduce' ? 4 : 3;
  for (let i = 0; i < maxP; i++) {
    const p = ps[i];
    const pt = ptypes[i];
    if (!p) { kparams.push('_'); continue; }
    if (p.type === 'Identifier') { kparams.push(`${p._b.kname}: ${KT[pt]}`); }
    else { const tmp = 'p' + (lbl++); kparams.push(`${tmp}: ${KT[pt]}`); pre.push(...destructure(p, tmp)); }
  }
  let body;
  if (f.body.type === 'BlockStatement') body = pre.join('\n') + '\n' + hoistDecls(f) + block(f.body.body) + '\nnull';
  else body = pre.join('\n') + '\n' + E(f.body, 'D');
  curFn = prevFn;
  return `${label}@{ ${kparams.join(', ')} ->\n${body}\n}`;
}
function genericFn(f) {
  const label = 'L' + (lbl++);
  const prevFn = curFn; curFn = { node: f, label, lambda: true };
  const pre = [];
  f.params.forEach((p, i) => {
    if (p.type === 'Identifier') pre.push(`var ${p._b.kname}: ${KT[p._b.t]} = ${co(`args.getOrNull(${i})`, 'D', p._b.t)}`);
    else { const tmp = 'p' + (lbl++); pre.push(`val ${tmp}: Any? = args.getOrNull(${i})`); pre.push(...destructure(p, tmp)); }
  });
  let body;
  if (f.body.type === 'BlockStatement') body = pre.join('\n') + '\n' + hoistDecls(f) + block(f.body.body) + '\nnull';
  else body = pre.join('\n') + '\n' + E(f.body, 'D');
  curFn = prevFn;
  return `JsFn ${label}@{ args ->\n${body}\n}`;
}
function destructure(p, src) {
  const out = [];
  if (p.type === 'ArrayPattern') p.elements.forEach((e, i) => { if (!e) return; if (e.type === 'Identifier') out.push(`var ${e._b.kname}: Any? = jsGet(${src}, ${i}.0)`); else { const t = 'p' + (lbl++); out.push(`val ${t}: Any? = jsGet(${src}, ${i}.0)`); out.push(...destructure(e, t)); } });
  else if (p.type === 'ObjectPattern') p.properties.forEach(pr => { const k = pr.key.name; const v = pr.value; if (v.type === 'Identifier') out.push(`var ${v._b.kname}: Any? = jsGet(${src}, ${strLit(k)})`); else if (v.type === 'AssignmentPattern') out.push(`var ${v.left._b.kname}: Any? = jsGet(${src}, ${strLit(k)}) ?: ${E(v.right, 'D')}`); else fail(pr, 'objpat'); });
  else if (p.type === 'Identifier') out.push(`var ${p._b.kname}: Any? = ${src}`);
  else fail(p, 'destructure');
  return out;
}
// hoisted `var` bindings of a function
function fwdDecls(scope) {
  const out = [];
  for (const [name, b] of scope.vars) if (b.fwd && b.kind !== 'var' && b.kind !== 'param' && b.kind !== 'fn') { b.hoisted = true; const nl = b.t === 'C' || b.t === 'G'; if (nl) b.hoistedNullable = true; out.push(`var ${b.kname}: ${KT[b.t]}${nl ? '?' : ''} = ${DEF[b.t]}`); }
  return out.length ? out.join('\n') + '\n' : '';
}
function hoistDecls(f) {
  const out = [];
  if (f.body.type === 'BlockStatement') { const fd = fwdDecls(f._scope); if (fd) out.push(fd.trim()); }
  for (const [name, b] of f._scope.vars) {
    if (b.kind !== 'var') continue;
    const nullable = (b.t === 'C' || b.t === 'G');
    if (nullable) b.hoistedNullable = true;
    out.push(`var ${b.kname}: ${KT[b.t]}${nullable ? '?' : ''} = ${DEF[b.t]}`);
  }
  // nested function declarations: emitted at top
  for (const [name, b] of f._scope.vars) if (b.kind === 'fn') out.push(localFun(b.fn, b.kname));
  return out.length ? out.join('\n') + '\n' : '';
}
function localFun(fn, name) {
  const prev = curFn; curFn = { node: fn, label: null };
  const dpre = [];
  const params = fn.params.map((p, i) => {
    const pb = fn._params[i];
    if (!pb) { const tmp = 'p' + (lbl++); dpre.push(...destructure(p, tmp)); return `${tmp}: Any?`; }
    let d = `${pb.kname}: ${KT[pb.t]}`;
    if (p.type === 'AssignmentPattern') d += ` = ${E(p.right, pb.t)}`;
    else if (UNDEF[pb.t]) d += ` = ${UNDEF[pb.t]}`;
    return d;
  });
  // params are vals in Kotlin; JS can reassign them -> shadow with var
  const reassigned = new Set();
  (function rec(n) { if (!n || typeof n.type !== 'string') return; if (n !== fn && isFn(n)) return; if ((n.type === 'AssignmentExpression' && n.left.type === 'Identifier') || (n.type === 'UpdateExpression' && n.argument.type === 'Identifier')) { const b = (n.left || n.argument)._b; if (b && b.kind === 'param' && fn._params.includes(b)) reassigned.add(b); } kids(n).forEach(rec); })(fn.body);
  let body;
  const shadow = [...reassigned].map(b => `var ${b.kname} = ${b.kname}`).concat(dpre).join('\n');
  const rt = retKt(fn);
  if (fn.body.type === 'BlockStatement') body = `{\n${shadow}\n${hoistDecls(fn)}${block(fn.body.body)}\n${rt !== 'Unit' ? (rt === 'Grad' || rt === 'Ctx' ? 'error("undefined")' : 'return ' + (rt === 'Double' ? 'Double.NaN' : rt === 'Boolean' ? 'false' : rt === 'String' ? '"undefined"' : 'null')) + '\n' : ''}}`;
  else body = `{\n${shadow}\nreturn ${E(fn.body, fn._ret === 'U' || fn._ret === 'U0' ? 'D' : fn._ret)}\n}`;
  curFn = prev;
  return `fun ${name}(${params.join(', ')}): ${rt} ${body}`;
}

// ------------------------------------------------------------------ statements
function block(stmts) { return stmts.map(stmt).filter(Boolean).join('\n'); }
function stmtExpr(e) { // expression used as a statement
  if (e.type === 'AssignmentExpression') return assignStmt(e);
  if (e.type === 'UpdateExpression') return E0(e);
  if (e.type === 'SequenceExpression') return e.expressions.map(stmtExpr).join('\n');
  if (e.type === 'LogicalExpression' && e.operator === '&&') return `if (${E(e.left, 'B')}) { ${stmtExpr(e.right)} }`;
  if (e.type === 'LogicalExpression' && e.operator === '||') return `if (!${par(E(e.left, 'B'))}) { ${stmtExpr(e.right)} }`;
  if (e.type === 'ConditionalExpression') return `if (${E(e.test, 'B')}) { ${stmtExpr(e.consequent)} } else { ${stmtExpr(e.alternate)} }`;
  const c = E0(e);
  return c;
}
function assignTarget(left) {
  if (left.type === 'Identifier' && !left._b && EXT_VAR.has(left.name)) return { kind: 'ext', key: strLit(left.name) };
  if (left.type === 'Identifier') return { kind: 'id', b: left._b };
  if (left.type === 'MemberExpression') {
    const ot = nat(left.object);
    if (ot === 'C') return { kind: 'ctx', code: `${E(left.object)}.${left.property.name}`, t: CTX_PROPS[left.property.name] };
    if (left.object.type === 'Identifier' && !left.object._b && (left.object.name === 'freqData' || left.object.name === 'timeData')) return { kind: 'narr', code: `Rt.${left.object.name}Arr[jsIdx(${E(left.property, 'N')})]` };
    if (left.object.type === 'Identifier' && !left.object._b && left.object.name === 'window') return { kind: 'win', key: strLit(left.property.name) };
    const [o, key] = memberParts(left);
    return { kind: 'dyn', o, key };
  }
  fail(left, 'assign target');
}
function assignStmt(n) {
  const tg = assignTarget(n.left);
  const op = n.operator;
  if (tg.kind === 'id') {
    const b = tg.b; if (!b) fail(n, 'assign to global');
    if (op === '=') return `${b.kname} = ${E(n.right, b.t)}`;
    if (op === '+=' && b.t === 'S') return `${b.kname} += ${E(n.right, 'S')}`;
    if (b.t !== 'N') { const bop = op.slice(0, -1); return `${b.kname} = ${opApply(co(b.kname, b.t, 'N'), bop, n.right)}`; }
    if (['+=', '-=', '*=', '/=', '%='].includes(op)) return `${b.kname} ${op} ${E(n.right, 'N')}`;
    return `${b.kname} = ${opApply(b.kname, op.slice(0, -1), n.right)}`;
  }
  if (tg.kind === 'ctx' || tg.kind === 'narr') {
    const t = tg.t || 'N';
    if (op === '=') return `${tg.code} = ${E(n.right, t)}`;
    return `${tg.code} = ${opApply(tg.code, op.slice(0, -1), n.right)}`;
  }
  if (tg.kind === 'win') return `Rt.setWin(${tg.key}, ${E(n.right, 'D')})`;
  if (tg.kind === 'ext') { if (op === '=') return `Ext.setV(${tg.key}, ${E(n.right, 'D')})`; return `Ext.setV(${tg.key}, num(Ext.v(${tg.key})) ${op.slice(0, -1)} ${E(n.right, 'N')})`; }
  if (op === '=') return `jsSet(${tg.o}, ${tg.key}, ${E(n.right, 'D')})`;
  const bop = op.slice(0, -1);
  if (bop === '+') return `jsAddAssign(${tg.o}, ${tg.key}, ${E(n.right, 'D')})`;
  return `jsOpAssign(${tg.o}, ${tg.key}, ${strLit(bop)}, ${E(n.right, 'N')})`;
}
function opApply(lhsNumCode, bop, right) {
  const r = E(right, 'N');
  const bit = { '&': 'and', '|': 'or', '^': 'xor', '<<': 'shl', '>>': 'shr', '>>>': 'ushr' }[bop];
  if (bop === '>>>') return `((jsInt(${lhsNumCode}) ushr jsInt(${r})).toLong() and 0xFFFFFFFFL).toDouble()`;
  if (bit) return `(jsInt(${lhsNumCode}) ${bit} jsInt(${r})).toDouble()`;
  if (bop === '**') return `Math.pow(${lhsNumCode}, ${r})`;
  return `${lhsNumCode} ${bop} ${r}`;
}
function assignExpr(n) { // assignment used as value
  const tg = assignTarget(n.left);
  if (tg.kind === 'id') { const t = tg.b.t; return `${E(n.right, t)}.also { ${tg.b.kname} = it }`.replace(/^(.*)$/, n.operator === '=' ? '$1' : `run { ${assignStmt(n)}; ${tg.b.kname} }`); }
  return `run { ${assignStmt(n)}; ${E(n.left)} }`;
}
function decl(d, kind) {
  const p = d.id;
  if (p.type !== 'Identifier') { const tmp = 'p' + (lbl++); return [`val ${tmp}: Any? = ${E(d.init, 'D')}`, ...destructure(p, tmp)].join('\n'); }
  const b = p._b;
  if (kind === 'var' || b.hoisted) { if (!d.init) return ''; return `${b.kname} = ${E(d.init, b.t)}`; }
  if (b.fnNode && d.init === b.fnNode) return localFun(b.fnNode, b.kname);
  const nullable = (b.t === 'C' || b.t === 'G') && !d.init;
  const reassigned = b.assigned.length > 1 || b.assignedOp || kind === 'let';
  return `${reassigned ? 'var' : 'val'} ${b.kname}: ${KT[b.t]}${nullable ? '?' : ''} = ${d.init ? E(d.init, b.t) : DEF[b.t]}`;
}
function stmt(s) {
  switch (s.type) {
    case 'EmptyStatement': return '';
    case 'ExpressionStatement': return stmtExpr(s.expression);
    case 'VariableDeclaration': return s.declarations.map(d => decl(d, s.kind)).filter(Boolean).join('\n');
    case 'FunctionDeclaration': return ''; // hoisted
    case 'ReturnStatement': {
      if (curFn.lambda) return s.argument ? `return@${curFn.label} ${E(s.argument, 'D')}` : `return@${curFn.label} null`;
      const rt = retKt(curFn.node);
      if (!s.argument) return rt === 'Unit' ? 'return' : 'return null';
      if (rt === 'Unit') return `${stmtExpr(s.argument)}\nreturn`;
      return `return ${E(s.argument, curFn.node._ret)}`;
    }
    case 'IfStatement': return `if (${E(s.test, 'B')}) {\n${stmtBody(s.consequent)}\n}${s.alternate ? ` else {\n${stmtBody(s.alternate)}\n}` : ''}`;
    case 'BlockStatement': return `run {\n${block(s.body)}\n}`.replace(/^run \{/, '').replace(/\}$/, '') ? blockInline(s) : '';
    case 'ForStatement': return forStmt(s);
    case 'WhileStatement': return loop(s, '', s.test, null, s.body);
    case 'DoWhileStatement': { const L = 'W' + (lbl++); loopStack.push({ brk: L, cont: L + 'c' }); const b = stmtBody(s.body); loopStack.pop(); return `${L}@ do {\n${L}c@ do {\n${b}\n} while (false)\n} while (${E(s.test, 'B')})`; }
    case 'BreakStatement': return `break@${loopStack[loopStack.length - 1].brk}`;
    case 'ContinueStatement': { const t = loopStack[loopStack.length - 1]; return t.contUsed = true, `${t.cont ? 'break@' + t.cont : 'continue@' + t.brk}`; }
    case 'SwitchStatement': return switchStmt(s);
    case 'TryStatement': return `try {\n${block(s.block.body)}\n} catch (ex: Exception) {\n${s.handler ? block(s.handler.body.body) : ''}\n}`;
    case 'ThrowStatement': return `throw RuntimeException("js throw")`;
    default: fail(s, 'stmt ' + s.type);
  }
}
function blockInline(s) { return fwdDecls(s._scope) + block(s.body); }
function stmtBody(s) { return s.type === 'BlockStatement' ? blockInline(s) : stmt(s); }
function forStmt(s) {
  let init = '';
  if (s.init) init = s.init.type === 'VariableDeclaration' ? stmt(s.init) : stmtExpr(s.init);
  return `run {\n${init}\n${loop(s, '', s.test, s.update, s.body)}\n}`;
}
function loop(s, _, test, update, body) {
  const L = 'L' + (lbl++);
  const frame = { brk: L, cont: null };
  // need a continue target that still runs `update`
  let hasCont = false;
  (function rec(n, depth) { if (!n || typeof n.type !== 'string') return; if (isFn(n)) return; if (['ForStatement', 'WhileStatement', 'DoWhileStatement', 'SwitchStatement'].includes(n.type) && n !== s) { if (n.type === 'SwitchStatement') kids(n).forEach(k => rec(k, depth)); else return; } if (n.type === 'ContinueStatement') hasCont = true; kids(n).forEach(k => rec(k, depth)); })(body, 0);
  if (hasCont && update) frame.cont = L + 'c';
  loopStack.push(frame);
  const b = stmtBody(body);
  loopStack.pop();
  const upd = update ? stmtExpr(update) : '';
  const t = test ? E(test, 'B') : 'true';
  if (frame.cont) return `${L}@ while (${t}) {\n${L}c@ do {\n${b}\n} while (false)\n${upd}\n}`;
  return `${L}@ while (${t}) {\n${b}\n${upd}\n}`;
}
function switchStmt(s) {
  const L = 'S' + (lbl++);
  const disc = E(s.discriminant, nat(s.discriminant) === 'N' ? 'N' : 'D');
  // supports only cases that end with break/return (no fallthrough except empty cases)
  const groups = []; let cur = null;
  for (const c of s.cases) { if (!cur) { cur = { tests: [], body: [] }; groups.push(cur); } cur.tests.push(c.test); if (c.consequent.length) { cur.body = c.consequent; cur = null; } }
  loopStack.push({ brk: L, cont: null, isSwitch: true });
  const arms = groups.map(g => {
    const body = g.body.filter((st, i) => !(st.type === 'BreakStatement' && i === g.body.length - 1));
    const isDefault = g.tests.some(t => t === null);
    const conds = g.tests.filter(t => t !== null).map(t => E(t, nat(s.discriminant) === 'N' ? 'N' : 'D'));
    return `${isDefault ? 'else' : conds.join(', ')} -> {\n${block(body)}\n}`;
  });
  loopStack.pop();
  return `${L}@ do {\nwhen (${disc}) {\n${arms.join('\n')}\n}\n} while (false)`;
}

// ------------------------------------------------------------------ program
const outTop = []; const outInit = [];
for (const st of ast.body) {
  if (st.type === 'FunctionDeclaration') { outTop.push(localFun(st, st.id._b.kname)); continue; }
  if (st.type === 'VariableDeclaration') {
    for (const d of st.declarations) {
      const b = d.id._b;
      if (b.fnNode) { outTop.push(localFun(b.fnNode, b.kname)); continue; }
      outTop.push(`var ${b.kname}: ${KT[b.t]}${b.t === 'C' || b.t === 'G' ? '?' : ''} = ${DEF[b.t]}`);
      if (d.init) { curFn = { node: { _ret: 'U' }, label: null }; outInit.push(`${b.kname} = ${E(d.init, b.t)}`); }
    }
    continue;
  }
  curFn = { node: { _ret: 'U' }, label: null };
  outInit.push(stmt(st));
}
const header = `// GENERATED from the REVERENDER_95 website source by tools/tx.js — do not edit by hand.
@file:Suppress("NAME_SHADOWING", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UNUSED_VALUE", "VARIABLE_WITH_REDUNDANT_INITIALIZER", "LocalVariableName", "FunctionName", "UNUSED_ANONYMOUS_PARAMETER", "UNNECESSARY_NOT_NULL_ASSERTION", "SENSELESS_COMPARISON", "UNREACHABLE_CODE", "KotlinConstantConditions", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE", "UNCHECKED_CAST", "USELESS_CAST", "RedundantExplicitType", "CanBeVal", "unused", "LiftReturnOrAssignment", "CascadeIf", "ReplaceWithOperatorAssignment", "ControlFlowWithEmptyBody", "ReplaceJavaStaticMethodWithKotlinAnalog", "ReplaceManualRangeWithIndicesCalls", "IntroduceWhenSubject", "RedundantIf", "SimplifyBooleanWithConstants")
package ${pkg}

import com.reverender.r95.ui.viz.js.*

`;
const body = outTop.join('\n\n') + `\n\nfun ${objName}Init() {\n${outInit.join('\n')}\n}\n`;
fs.writeFileSync(outFile, header + body);
console.error('ok', allBindings.length, 'bindings');
