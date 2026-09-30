const messages = {
  es: { label: 'Idioma', title: 'Dataset sintético', scope: 'Laboratorio de prueba con H2. Sólo datos sintéticos.', prepare: 'Preparar descarga', ready: 'Archivos verificados', error: 'No se pudo validar el dataset. Descarga bloqueada.', csv: 'Descargar CSV', manifest: 'Descargar manifiesto' },
  en: { label: 'Language', title: 'Synthetic dataset', scope: 'Test laboratory with H2. Synthetic data only.', prepare: 'Prepare download', ready: 'Files verified', error: 'Dataset validation failed. Download blocked.', csv: 'Download CSV', manifest: 'Download manifest' },
};
const element = (id) => document.getElementById(id);
let state = '';
let urls = [];
let generation = 0;
function clearDownloads() {
  urls.forEach((url) => URL.revokeObjectURL(url));
  urls = [];
  for (const id of ['csv', 'manifest']) {
    element(id).hidden = true;
    element(id).removeAttribute('href');
  }
}
function render() {
  const locale = element('language').value;
  const text = messages[locale];
  document.documentElement.lang = locale;
  document.title = text.title;
  for (const id of ['title', 'scope', 'prepare', 'csv', 'manifest']) element(id).textContent = text[id];
  element('language-label').textContent = text.label;
  element('status').textContent = state === 'ready' ? text.ready : '';
  element('error').textContent = state === 'error' ? text.error : '';
}
element('language').addEventListener('change', render);
element('prepare').addEventListener('click', async () => {
  const current = ++generation;
  clearDownloads();
  state = '';
  element('prepare').disabled = true;
  render();
  try {
    const response = await fetch('/snapshot', { cache: 'no-store' });
    if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) throw new Error('Response rejected');
    const snapshot = await response.json();
    const { csv, manifest } = snapshot;
    if (typeof csv !== 'string' || manifest?.dataClass !== 'SYNTHETIC_ONLY' || manifest.schemaVersion !== '1.0-synthetic' || manifest.acceptedAttempts < 2 || manifest.excludedAttempts !== 0) throw new Error('Invalid snapshot');
    const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(csv));
    const hash = Array.from(new Uint8Array(digest), (value) => value.toString(16).padStart(2, '0')).join('');
    if (hash !== manifest.csvSha256) throw new Error('Hash mismatch');
    if (current !== generation) return;
    urls = [URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' })), URL.createObjectURL(new Blob([JSON.stringify(manifest, null, 2) + '\n'], { type: 'application/json' }))];
    ['csv', 'manifest'].forEach((id, index) => { element(id).href = urls[index]; element(id).hidden = false; });
    state = 'ready';
  } catch {
    clearDownloads();
    state = 'error';
  } finally {
    element('prepare').disabled = false;
    render();
  }
});
render();
