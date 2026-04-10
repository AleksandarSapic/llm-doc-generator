import { Component, input } from '@angular/core';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDividerModule } from '@angular/material/divider';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge';
import { JobStatusResponse } from '../../../core/models/job.model';

@Component({
  selector: 'app-progress-tracker',
  standalone: true,
  imports: [MatProgressBarModule, MatDividerModule, StatusBadgeComponent],
  templateUrl: './progress-tracker.html',
  styleUrl: './progress-tracker.css',
})
export class ProgressTrackerComponent {
  readonly status = input.required<JobStatusResponse>();
  readonly isTerminal = input(false);
}
