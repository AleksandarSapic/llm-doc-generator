import { Component, OnInit, inject, input, computed, signal, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { interval, catchError, switchMap, takeWhile } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProgressTrackerComponent } from './progress-tracker/progress-tracker';
import { ErrorBannerComponent } from '../../shared/components/error-banner/error-banner';
import { SseService } from '../../core/services/sse.service';
import { JobsService } from '../../core/services/jobs.service';
import { JobStatusResponse, TERMINAL_STATUSES } from '../../core/models/job.model';
import { AppError } from '../../core/interceptors/error.interceptor';

@Component({
  selector: 'app-job-status',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    ProgressTrackerComponent,
    ErrorBannerComponent,
  ],
  templateUrl: './job-status.html',
  styleUrl: './job-status.css',
})
export class JobStatusComponent implements OnInit {
  readonly jobId = input.required<string>();

  readonly router = inject(Router);
  private readonly sseService = inject(SseService);
  private readonly jobsService = inject(JobsService);
  private readonly destroyRef = inject(DestroyRef);

  readonly jobStatus = signal<JobStatusResponse | null>(null);
  readonly isLoading = signal(true);
  readonly error = signal<string | null>(null);

  readonly isTerminal = computed(() =>
    TERMINAL_STATUSES.includes(this.jobStatus()?.status ?? 'PENDING')
  );
  readonly isCompleted = computed(() => this.jobStatus()?.status === 'COMPLETED');
  readonly isFailed = computed(() => this.jobStatus()?.status === 'FAILED');

  ngOnInit(): void {
    const fallbackPolling$ = interval(3000).pipe(
      switchMap(() => this.jobsService.getStatus(this.jobId())),
      takeWhile(s => !TERMINAL_STATUSES.includes(s.status), true)
    );

    this.sseService.streamJobStatus(this.jobId()).pipe(
      catchError(() => fallbackPolling$),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: status => {
        this.jobStatus.set(status);
        this.isLoading.set(false);
      },
      error: (err: AppError) => {
        this.error.set(err?.message ?? 'Failed to load job status.');
        this.isLoading.set(false);
      },
    });
  }

  viewResults(): void {
    this.router.navigate(['/jobs', this.jobId(), 'result'], {
      state: { jobStatus: this.jobStatus() },
    });
  }
}
