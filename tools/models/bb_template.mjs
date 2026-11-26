// Builds one block model in the running Blockbench through its MCP server (plugin "Blockbench MCP", /bb-mcp), then
// saves the exported Java block model, the .bbmodel project and preview images to OUT, and closes the project.
// Filled in and run by tools/models/blockbench.py (desktop-commander node:local); SPEC is inserted there.
import fs from 'fs';
import path from 'path';
import crypto from 'crypto';
const SPEC = __SPEC__;
const U = 'http://127.0.0.1:3000/bb-mcp';
async function rpc(body, sid) {
  const h = { 'content-type': 'application/json', accept: 'application/json, text/event-stream' };
  if (sid) h['mcp-session-id'] = sid;
  const r = await fetch(U, { method: 'POST', headers: h, body: JSON.stringify(body) });
  const t = await r.text();
  const m = t.match(/^data: (.*)$/m);
  const x = m ? m[1] : t;
  return { sid: r.headers.get('mcp-session-id'), data: x ? JSON.parse(x) : null };
}
const init = await rpc({ jsonrpc: '2.0', id: 1, method: 'initialize', params: { protocolVersion: '2025-03-26', capabilities: {}, clientInfo: { name: 'foodindustrial-models', version: '1' } } });
const sid = init.sid;
await rpc({ jsonrpc: '2.0', method: 'notifications/initialized' }, sid);
let id = 2;
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));
async function call(name, args) {
  // the plugin turns project tools off while no project is selected and back on a moment after one is, so a call
  // right after create_project or a close can find its tool "disabled" for a short while
  for (let attempt = 0; ; attempt++) {
    const d = (await rpc({ jsonrpc: '2.0', id: id++, method: 'tools/call', params: { name, arguments: args } }, sid)).data;
    if (d.error) throw new Error(name + ': ' + JSON.stringify(d.error));
    if (d.result && d.result.isError) {
      const msg = JSON.stringify(d.result.content);
      if (msg.includes('disabled') && attempt < 40) { await sleep(250); continue; }
      throw new Error(name + ': ' + msg.slice(0, 800));
    }
    return d.result;
  }
}
const text = (res) => res.content.filter((c) => c.type === 'text').map((c) => c.text).join('\n');
const log = [];
fs.mkdirSync(SPEC.out, { recursive: true });
for (const [name, src] of Object.entries(SPEC.textures)) {
  if (src.startsWith('data:') && crypto.createHash('sha256').update(src).digest('hex').slice(0, 10) !== SPEC.check[name]) throw new Error(SPEC.id + ' ' + name + ': texture garbled in the copy');
}
// a project of this name left by an earlier run that stopped half way
await call('risky_eval', { code: "ModelProject.all.filter((p) => p.name === 'fi_" + SPEC.id + "').map((p) => (p.close(true), p.name))" });
await call('create_project', { name: 'fi_' + SPEC.id, format: 'java_block' });
for (const [name, src] of Object.entries(SPEC.textures)) {
  const data = src.startsWith('data:') ? src : 'data:image/png;base64,' + fs.readFileSync(src).toString('base64');
  await call('create_texture', { name, data });
}
const placed = await call('place_cube', { elements: SPEC.elements.map((e) => ({ name: e.name, from: e.from, to: e.to, origin: e.origin || [8, 8, 8], rotation: e.rotation || [0, 0, 0] })), faces: false });
log.push(text(placed).slice(0, 300));
for (const e of SPEC.elements) {
  const faces = {};
  for (const f of ['north', 'east', 'south', 'west', 'up', 'down']) {
    const face = e.faces[f];
    faces[f] = face ? { uv: face.uv, texture: face.texture, rotation: face.rotation || 0 } : { texture: null };
  }
  await call('set_cube_uv', { id: e.name, faces });
}
await call('create_offscreen_view', { id: 'v_' + SPEC.id, width: 512, height: 512, antialias: false, copy_view: 'none' });
for (const [i, cam] of SPEC.cameras.entries()) {
  // Blockbench's scene puts the block's bottom centre at the origin
  const shot = await call('set_camera_angle', { view: 'v_' + SPEC.id, position: cam.position, target: [0, 8, 0], projection: 'orthographic', zoom: cam.zoom || 0.5, format: 'png' });
  const img = shot.content.find((c) => c.type === 'image');
  if (img) fs.writeFileSync(path.join(SPEC.out, `${SPEC.id}_preview${i}.png`), Buffer.from(img.data, 'base64'));
}
try { await call('delete_offscreen_view', { view: 'v_' + SPEC.id }); } catch (e) { log.push('view left to the session end: ' + e.message.slice(0, 80)); }
const java = await call('export_model', { codec_id: 'java_block', max_content_length: 500000 });
const jt = JSON.parse(text(java));
fs.writeFileSync(path.join(SPEC.out, `${SPEC.id}.json`), typeof jt.content === 'string' ? jt.content : JSON.stringify(jt.content ?? jt, null, 2));
const proj = await call('export_model', { codec_id: 'project', max_content_length: 2000000 });
const pt = JSON.parse(text(proj));
fs.writeFileSync(path.join(SPEC.out, `${SPEC.id}.bbmodel`), typeof pt.content === 'string' ? pt.content : JSON.stringify(pt.content ?? pt));
if (SPEC.close) await call('risky_eval', { code: "ModelProject.all.filter((p) => p.name === 'fi_" + SPEC.id + "').map((p) => (p.close(true), p.name))" });
console.log(JSON.stringify({ id: SPEC.id, log, java_keys: Object.keys(jt) }));
