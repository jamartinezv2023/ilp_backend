import { spawn } from 'node:child_process';
import { mkdtemp, readFile, access } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { resolve, join } from 'node:path';
import { fileURLToPath } from 'node:url';
const lab = fileURLToPath(new URL('.', import.meta.url));
const root = resolve(lab, '../..');
const evidence = await mkdtemp(join(tmpdir(), 'ilp-dataset-browser-'));
const ready = join(evidence, 'ready.txt');
const windows = process.platform === 'win32';
const gradle = spawn(windows ? 'cmd.exe' : './gradlew', windows ? ['/d', '/s', '/c', 'gradlew.bat :adaptive-education-service:test --tests *SyntheticDatasetBrowserBridgeTest --no-daemon --console=plain'] : [':adaptive-education-service:test', '--tests', '*SyntheticDatasetBrowserBridgeTest', '--no-daemon', '--console=plain'], { cwd: root, env: { ...process.env, ILP_DATASET_READY: ready }, stdio: 'inherit' });
let gradleEnded = false;
const gradleExit = new Promise((done) => { gradle.on('error', (error) => { console.error(error); gradleEnded = true; done(1); }); gradle.on('exit', (code) => { gradleEnded = true; done(code ?? 1); }); });
let url;
let browserCode = 1;
try {
  const deadline = Date.now() + 180_000;
  while (Date.now() < deadline) {
    try { await access(ready); url = (await readFile(ready, 'utf8')).trim(); break; } catch { /* Wait for H2 fixture and loopback HTTP adapter. */ }
    if (gradleEnded) throw new Error('Gradle exited before readiness');
    await new Promise((done) => setTimeout(done, 250));
  }
  if (!url || !/^http:\/\/127\.0\.0\.1:\d+$/.test(url)) throw new Error('Isolated backend not ready');
  const child = spawn(process.execPath, [join(lab, 'node_modules/@playwright/test/cli.js'), 'test', '--config', join(lab, 'playwright.config.mjs')], { cwd: lab, env: { ...process.env, ILP_DATASET_URL: url }, stdio: 'inherit' });
  browserCode = await new Promise((done) => { child.on('error', () => done(1)); child.on('exit', (code) => done(code ?? 1)); });
} catch (error) {
  console.error(error);
} finally {
  if (url) await fetch(`${url}/release`).catch(() => {});
  else if (!gradleEnded) gradle.kill();
}
const gradleCode = await gradleExit;
console.log(`BROWSER_EXIT_CODE=${browserCode}\nGRADLE_EXIT_CODE=${gradleCode}`);
process.exitCode = browserCode === 0 && gradleCode === 0 ? 0 : 1;
