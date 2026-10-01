const messages = {
  es: { label: 'Idioma', title: 'Dataset sintético', scope: 'Laboratorio de prueba con H2. Sólo datos sintéticos.', prepare: 'Preparar descarga', ready: 'Archivos verificados', error: 'No se pudo validar el dataset. Descarga bloqueada.', csv: 'Descargar CSV', manifest: 'Descargar manifiesto', question: 'Pregunta ficticia: elija A o B', 'option-a': 'Opción A', 'option-b': 'Opción B', submit: 'Guardar respuesta sintética', refresh: 'Actualizar historial', 'history-title': 'Historial persistido de esta sesión', saved: 'Respuesta recuperada del historial', readFailed: 'Envío aceptado; no se confirmó la lectura del historial. Actualice el historial sin reenviar.', submitFailed: 'No se confirmó el envío. Revise el historial antes de volver a intentarlo.', required: 'Seleccione una opción.', attempts: 'Intentos incluidos', snapshot: 'Identificador de instantánea', retry: 'Reintentar el mismo envío', pending: 'Envío sin confirmar. Actualice el historial o reintente el mismo envío; no cree otro.', historyFailed: 'No se pudo leer el historial. Actualice para volver a consultar.' },
  en: { label: 'Language', title: 'Synthetic dataset', scope: 'Test laboratory with H2. Synthetic data only.', prepare: 'Prepare download', ready: 'Files verified', error: 'Dataset validation failed. Download blocked.', csv: 'Download CSV', manifest: 'Download manifest', question: 'Fictional question: choose A or B', 'option-a': 'Option A', 'option-b': 'Option B', submit: 'Save synthetic answer', refresh: 'Refresh history', 'history-title': 'Persisted history for this session', saved: 'Answer recovered from history', readFailed: 'Submission accepted; history read not confirmed. Refresh history without resubmitting.', submitFailed: 'Submission not confirmed. Check history before trying again.', required: 'Select an option.', attempts: 'Included attempts', snapshot: 'Snapshot identifier', retry: 'Retry the same submission', pending: 'Submission unconfirmed. Refresh history or retry the same submission; do not create another.', historyFailed: 'History could not be read. Refresh to query again.' },
};
const element = (id) => document.getElementById(id);
let state = '';
let verifiedSnapshot = null;
let submissionState = '';
let historyRows = [];
let sending = false;
let preparing = false;
let pending = JSON.parse(sessionStorage.getItem('synthetic-dataset-pending') || 'null');
const session = sessionStorage.getItem('synthetic-dataset-session') || crypto.randomUUID();
sessionStorage.setItem('synthetic-dataset-session', session);
element('language').value = sessionStorage.getItem('synthetic-dataset-locale') || 'es';
let urls = [];
let generation = 0;
function clearDownloads() {
  generation++;
  verifiedSnapshot = null;
  urls.forEach((url) => URL.revokeObjectURL(url));
  urls = [];
  for (const id of ['csv', 'manifest']) {
    element(id).hidden = true;
    element(id).removeAttribute('href');
    element(id).removeAttribute('download');
  }
}
function render() {
  const locale = element('language').value;
  const text = messages[locale];
  document.documentElement.lang = locale;
  document.title = text.title;
  for (const id of ['title', 'scope', 'prepare', 'csv', 'manifest', 'question', 'option-a', 'option-b', 'submit', 'refresh', 'history-title', 'retry']) element(id).textContent = text[id];
  element('language-label').textContent = text.label;
  element('status').textContent = state === 'ready' ? text.ready : '';
  element('snapshot-details').textContent = verifiedSnapshot
    ? `${text.attempts}: ${verifiedSnapshot.attempts} · ${text.snapshot}: ${verifiedSnapshot.hash}` : '';
  element('error').textContent = state === 'error' ? text.error : '';
  element('submission-status').textContent = text[submissionState] || '';
  element('retry').hidden = !pending;
  element('retry').disabled = sending || preparing;
  element('submit').disabled = Boolean(pending) || sending || preparing;
  element('prepare').disabled = Boolean(pending) || sending || preparing;
  for (const radio of document.querySelectorAll('input[name=option]')) radio.disabled = Boolean(pending) || sending || preparing;
  element('history').replaceChildren(...historyRows.map((row) => { const li = document.createElement('li'); li.textContent = `${row.id} · ${row.answers[0].optionId}`; return li; }));
}
element('language').addEventListener('change', () => { sessionStorage.setItem('synthetic-dataset-locale', element('language').value); render(); });
async function refreshHistory() {
  const response = await fetch(`/lineage?session=${session}`, { cache: 'no-store' });
  if (!response.ok) throw new Error('History failed');
  const data = await response.json();
  if (!Array.isArray(data.responses) || data.history?.totalObservations !== data.responses.length) throw new Error('History invalid');
  historyRows = data.responses;
  if (pending && historyRows.some((row) => row.id === `SYNTHETIC-${pending.requestId}`
      && row.answers[0].optionId === pending.option)) {
    pending = null;
    sessionStorage.removeItem('synthetic-dataset-pending');
    submissionState = 'saved';
  }
  render();
}
element('refresh').addEventListener('click', async () => {
  try { await refreshHistory(); submissionState = historyRows.length ? 'saved' : ''; } catch { submissionState = pending ? 'pending' : 'historyFailed'; }
  render();
});
async function sendPending() {
  if (!pending || sending || preparing) return;
  const attempt = pending;
  sending = true;
  clearDownloads();
  state = '';
  render();
  let accepted = false;
  try {
    const response = await fetch('/submit', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(attempt) });
    if (![200, 201].includes(response.status)) throw new Error('Submission rejected');
    const receipt = await response.json();
    if (receipt.administrationId !== `SYNTHETIC-${attempt.requestId}`) throw new Error('Receipt mismatch');
    accepted = true;
    await refreshHistory();
    if (!historyRows.some((row) => row.id === receipt.administrationId && row.answers[0].optionId === attempt.option)) throw new Error('History mismatch');
    submissionState = 'saved';
    element('answer-form').reset();
  } catch { submissionState = accepted ? 'readFailed' : 'pending'; }
  finally { sending = false; render(); }
}
element('retry').addEventListener('click', sendPending);
element('answer-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  if (pending || sending || preparing) return;
  const option = new FormData(event.currentTarget).get('option');
  if (!option) { submissionState = 'required'; render(); return; }
  pending = { session, requestId: crypto.randomUUID(), option, language: element('language').value };
  sessionStorage.setItem('synthetic-dataset-pending', JSON.stringify(pending));
  await sendPending();
});
element('prepare').addEventListener('click', async () => {
  if (pending || sending || preparing) return;
  preparing = true;
  clearDownloads();
  const current = generation;
  state = '';
  element('prepare').disabled = true;
  element('submit').disabled = true;
  render();
  try {
    const response = await fetch(`/snapshot?session=${session}`, { cache: 'no-store' });
    if (!response.ok || !response.headers.get('content-type')?.includes('application/json')) throw new Error('Response rejected');
    const snapshot = await response.json();
    const { csv, manifest } = snapshot;
    if (typeof csv !== 'string' || manifest?.dataClass !== 'SYNTHETIC_ONLY' || manifest.schemaVersion !== '1.0-synthetic' || manifest.acceptedAttempts < 2 || manifest.excludedAttempts !== 0) throw new Error('Invalid snapshot');
    const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(csv));
    const hash = Array.from(new Uint8Array(digest), (value) => value.toString(16).padStart(2, '0')).join('');
    if (hash !== manifest.csvSha256) throw new Error('Hash mismatch');
    if (current !== generation) return;
    urls = [URL.createObjectURL(new Blob([csv], { type: 'text/csv;charset=utf-8' })), URL.createObjectURL(new Blob([JSON.stringify(manifest, null, 2) + '\n'], { type: 'application/json' }))];
    verifiedSnapshot = { hash, attempts: manifest.acceptedAttempts };
    ['csv', 'manifest'].forEach((id, index) => {
      element(id).href = urls[index];
      element(id).download = `${id === 'csv' ? 'observations' : 'manifest'}-${hash}.${id === 'csv' ? 'csv' : 'json'}`;
      element(id).hidden = false;
    });
    state = 'ready';
  } catch {
    clearDownloads();
    state = 'error';
  } finally {
    preparing = false;
    element('submit').disabled = false;
    element('prepare').disabled = false;
    render();
  }
});
if (pending) submissionState = 'pending';
render();

refreshHistory().catch(() => { submissionState = pending ? 'pending' : 'historyFailed'; render(); });
