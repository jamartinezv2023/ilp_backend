import { test, expect } from '@playwright/test';
import { readFile } from 'node:fs/promises';
import { createHash } from 'node:crypto';
for (const locale of ['es', 'en']) {
  test(`persisted snapshot and rejected export in ${locale}`, async ({ page, request }, testInfo) => {
    await page.goto('/');
    await page.locator('#language').selectOption(locale);
    await expect(page.locator('html')).toHaveAttribute('lang', locale);
    await page.locator('#prepare').click();
    await expect(page.locator('#csv')).toBeVisible();
    const csvDownload = page.waitForEvent('download');
    await page.locator('#csv').click();
    const csvFile = await csvDownload;
    const manifestDownload = page.waitForEvent('download');
    await page.locator('#manifest').click();
    const manifestFile = await manifestDownload;
    const bytes = await readFile(await csvFile.path());
    const manifestBytes = await readFile(await manifestFile.path());
    const manifest = JSON.parse(manifestBytes);
    expect(createHash('sha256').update(bytes).digest('hex')).toBe(manifest.csvSha256);
    const lineageResponse = await request.get('/lineage');
    expect(lineageResponse.ok()).toBeTruthy();
    const { history, responses } = await lineageResponse.json();
    const rows = bytes.toString('utf8').trimEnd().split('\n').slice(1).map((line) => line.split(',').map((cell) => cell.slice(1, -1)));
    expect(rows).toHaveLength(manifest.answerRows);
    expect(manifest.acceptedAttempts).toBe(history.totalObservations);
    for (const response of responses) {
      const observation = history.observations.find((item) => item.administrationId === response.id);
      for (const answer of response.answers) {
        expect(rows).toContainEqual([history.participantId, response.id, response.assessmentCode, response.assessmentVersion, response.submittedAt, observation.scoringAlgorithmVersion, answer.questionId, answer.optionId, observation.context.language, observation.context.completeContext.translationVersion, 'SYNTHETIC_HTTP_E2E', 'TEST_ONLY']);
      }
      expect(bytes.toString()).not.toContain(response.studentId);
    }
    await testInfo.attach('observations.csv', { body: bytes, contentType: 'text/csv' });
    await testInfo.attach('manifest.json', { body: manifestBytes, contentType: 'application/json' });
    const downloads = [];
    page.on('download', (file) => downloads.push(file));
    await page.route('**/snapshot', (route) => route.continue({ url: new URL('/snapshot?invalid=1', page.url()).href }));
    const rejected = page.waitForResponse((response) => response.url().includes('/snapshot'));
    await page.locator('#prepare').click();
    expect((await rejected).status()).toBe(422);
    await expect(page.getByRole('alert')).toHaveText(locale === 'es' ? 'No se pudo validar el dataset. Descarga bloqueada.' : 'Dataset validation failed. Download blocked.');
    await expect(page.locator('#csv')).toBeHidden();
    await expect(page.locator('#manifest')).toBeHidden();
    expect(await page.locator('#csv').getAttribute('href')).toBeNull();
    expect(downloads).toEqual([]);
    await page.locator('#language').selectOption(locale === 'es' ? 'en' : 'es');
    await expect(page.getByRole('alert')).toContainText(locale === 'es' ? 'Download blocked.' : 'Descarga bloqueada.');
  });
}
test('a corrupted checksum exposes no download', async ({ page }) => {
  await page.route('**/snapshot', async (route) => {
    const response = await route.fetch();
    const snapshot = await response.json();
    snapshot.csv += 'corrupted';
    await route.fulfill({ response, json: snapshot });
  });
  await page.goto('/');
  await page.locator('#prepare').click();
  await expect(page.getByRole('alert')).toContainText('Descarga bloqueada.');
  await expect(page.locator('#csv')).toBeHidden();
});
