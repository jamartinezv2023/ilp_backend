import { spawn } from 'node:child_process';
import { mkdtemp, readFile, access } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { resolve, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { createInterface } from 'node:readline';
const manual = process.argv.includes('--manual');
const lab = fileURLToPath(new URL('.', import.meta.url));
const root = resolve(lab, '../..');
const evidence = await mkdtemp(join(tmpdir(), 'ilp-dataset-browser-'));
const ready = join(evidence, 'ready.txt');
const windows = process.platform === 'win32';
const gradle = spawn(windows ? 'cmd.exe' : './gradlew', windows ? ['/d', '/s', '/c', 'gradlew.bat :adaptive-education-service:test --tests *SyntheticDatasetBrowserBridgeTest --no-daemon --console=plain'] : [':adaptive-education-service:test', '--tests', '*SyntheticDatasetBrowserBridgeTest', '--no-daemon', '--console=plain'], { cwd: root, env: { ...process.env, ILP_DATASET_READY: ready, ILP_DATASET_MANUAL: manual ? 'true' : 'false' }, stdio: 'inherit' });
let gradleEnded = false;
const gradleExit = new Promise((done) => { gradle.on('error', (error) => { console.error(error); gradleEnded = true; done(1); }); gradle.on('exit', (code) => { gradleEnded = true; done(code ?? 1); }); });
let url;
let browserCode = 1;
try {
  const deadline = Date.now() + 300_000;
  while (Date.now() < deadline) {
    try { await access(ready); url = (await readFile(ready, 'utf8')).trim(); break; } catch { /* Wait for H2 fixture and loopback HTTP adapter. */ }
    if (gradleEnded) throw new Error('Gradle exited before readiness');
    await new Promise((done) => setTimeout(done, 250));
  }
  if (!url || !/^http:\/\/127\.0\.0\.1:\d+$/.test(url)) throw new Error('Isolated backend not ready');
  if (manual) {
    if (!process.stdin.isTTY) throw new Error('Manual mode requires an interactive terminal');
    console.log(`LAB_URL=${url}\nSYNTHETIC_H2_ONLY=True\nSESSION_LIMIT_MINUTES=10`);
    if (windows) {
      const opener = spawn('cmd.exe', ['/d', '/s', '/c', `start "" "${url}"`], { stdio: 'ignore' });
      opener.on('error', () => console.log('Open LAB_URL in your browser.'));
    }
    console.log('Descargue CSV y manifiesto. Pulse ENTER al terminar / Download both files, then press ENTER.');
    const input = createInterface({ input: process.stdin, output: process.stdout });
    await new Promise((done) => {
      const timer = setTimeout(() => { console.log('Session ended after 9 minutes.'); input.close(); }, 540_000);
      input.once('line', () => input.close());
      input.once('SIGINT', () => input.close());
      input.once('close', () => { clearTimeout(timer); done(); });
    });
    browserCode = 0;
  } else {
    const child = spawn(process.execPath, [join(lab, 'node_modules/@playwright/test/cli.js'), 'test', '--config', join(lab, 'playwright.config.mjs')], { cwd: lab, env: { ...process.env, ILP_DATASET_URL: url }, stdio: 'inherit' });
    browserCode = await new Promise((done) => { child.on('error', () => done(1)); child.on('exit', (code) => done(code ?? 1)); });
  }
} catch (error) {
  console.error(error);
} finally {
  if (url) await fetch(`${url}/release`).catch(() => {});
  else if (!gradleEnded) gradle.kill();
}
const gradleCode = await gradleExit;
console.log(`${manual ? 'MANUAL_SESSION_EXIT_CODE' : 'BROWSER_EXIT_CODE'}=${browserCode}\nGRADLE_EXIT_CODE=${gradleCode}`);
process.exitCode = browserCode === 0 && gradleCode === 0 ? 0 : 1;
