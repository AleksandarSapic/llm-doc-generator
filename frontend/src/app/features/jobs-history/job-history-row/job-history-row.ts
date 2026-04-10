import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge';
import { JobStatusResponse } from '../../../core/models/job.model';

@Component({
  selector: 'app-job-history-row',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatIconModule, StatusBadgeComponent],
  templateUrl: './job-history-row.html',
  styleUrl: './job-history-row.css',
})
export class JobHistoryRowComponent {
  readonly job = input.required<JobStatusResponse>();

  shortId(): string {
    return this.job().jobId.substring(0, 8) + '...';
  }
}
