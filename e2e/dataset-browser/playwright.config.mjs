import { defineConfig } from '@playwright/test';
export default defineConfig({ testDir: '.', testMatch: '*.spec.mjs', workers: 1, reporter: [['list'], ['html', { open: 'never' }]], use: { baseURL: process.env.ILP_DATASET_URL, trace: 'retain-on-failure' } });
