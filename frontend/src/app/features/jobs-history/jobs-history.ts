import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { JobHistoryRowComponent } from './job-history-row/job-history-row';
import { ErrorBannerComponent } from '../../shared/components/error-banner/error-banner';
import { JobsService } from '../../core/services/jobs.service';
import { JobStatusResponse } from '../../core/models/job.model';
import { AppError } from '../../core/interceptors/error.interceptor';

@Component({
  selector: 'app-jobs-history',
  standalone: true,
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatProgressSpinnerModule,
    JobHistoryRowComponent,
    ErrorBannerComponent,
  ],
  templateUrl: './jobs-history.html',
  styleUrl: './jobs-history.css',
})
export class JobsHistoryComponent implements OnInit {
  private readonly jobsService = inject(JobsService);

  readonly jobs = signal<JobStatusResponse[]>([]);
  readonly isLoading = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.jobsService.getAll().subscribe({
      next: jobs => {
        this.jobs.set(jobs.sort((a, b) => a.jobId > b.jobId ? -1 : 1));
        this.isLoading.set(false);
      },
      error: (err: AppError) => {
        this.error.set(err?.message ?? 'Failed to load jobs.');
        this.isLoading.set(false);
      },
    });
  }
}
