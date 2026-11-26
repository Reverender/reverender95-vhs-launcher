const fs=require('fs');
const calls=[];
global.host={ call:(n,a)=>{calls.push(n+' '+a); return n==='revyOpenWebsiteInBrowser'?'1':'';}, get:(k)=>{ if(k.startsWith('has:')) return '1'; if(k==='volume') return '0.8'; if(k==='track') return '3'; if(k==='track-name') return 'TRK 04: Test'; if(k.startsWith('ls:')) return '\u0000'; if(k==='style') return 'longhorn'; if(k==='lang') return 'en-US'; return '0'; } };
eval(fs.readFileSync('bundle.js','utf8')+';global.revyHandle=revyHandle;');
for (const q of ['hello','help','open the music player','next wallpaper','set volume to 30%','what is 12*7','convert 5 km to miles','what time is it','tell me a joke','who is the president of france','my name is Rev','switch to windows 95','play','skip song','blah blah zzz']) {
  calls.length=0; let r; try { r=JSON.parse(revyHandle(q,'[]')); } catch(e){ r={err:e.message}; }
  process.stdout.write(['>',q,'\n ',(r.kind||'')+': '+(r.reply||r.err||'').slice(0,110).replace(/\n/g,' '),'|',calls.join('; ')].join(' ')+'\n');
}
