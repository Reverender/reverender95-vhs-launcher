const acorn=require('/opt/node-tools/node_modules/acorn');const fs=require('fs');
const src=fs.readFileSync('/home/claude/pretty/rr-wallpaper-renderer.js','utf8');
const ast=acorn.parse(src,{ecmaVersion:'latest'});
const body=ast.body[0].expression.callee.body.body;
const names=['tl','dl','Y','z','x','ll','ol','al','el','j'];
const out=[];
for(const st of body){ if(st.type==='FunctionDeclaration'&&names.includes(st.id.name)){
  let s=src.slice(st.start,st.end);
  out.push(s.replace(/^function (\w+)/,'function wp_$1'));
}}
let s=out.join('\n');
// rename calls of sibling functions
for(const n of names){ s=s.replace(new RegExp('(?<![\\w.$])'+n+'\\(','g'),'wp_'+n+'('); }
s=s.replace(/function wp_wp_/g,'function wp_');
process.stdout.write(s+'\n');
