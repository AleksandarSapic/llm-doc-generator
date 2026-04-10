import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'submit', pathMatch: 'full' },
  {
    path: 'submit',
    loadComponent: () =>
      import('./features/job-submission/job-submission').then(m => m.JobSubmissionComponent),
  },
  {
    path: 'jobs',
    loadComponent: () =>
      import('./features/jobs-history/jobs-history').then(m => m.JobsHistoryComponent),
  },
  {
    path: 'jobs/:jobId/status',
    loadComponent: () =>
      import('./features/job-status/job-status').then(m => m.JobStatusComponent),
  },
  {
    path: 'jobs/:jobId/result',
    loadComponent: () =>
      import('./features/results/documentation-result').then(m => m.DocumentationResultComponent),
  },
  { path: '**', redirectTo: 'submit' },
];
