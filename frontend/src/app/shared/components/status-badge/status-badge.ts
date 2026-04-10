import { Component, input } from '@angular/core';
import { NgClass } from '@angular/common';
import { DocJobStatus } from '../../../core/models/job.model';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [NgClass],
  templateUrl: './status-badge.html',
  styleUrl: './status-badge.css',
})
export class StatusBadgeComponent {
  readonly status = input.required<DocJobStatus>();

  badgeClass(): string {
    const s = this.status().toLowerCase();
    if (s === 'completed') return 'badge-completed';
    if (s === 'failed') return 'badge-failed';
    if (s === 'processing' || s === 'aggregating') return 'badge-processing';
    return 'badge-pending';
  }

  statusLabel(): string {
    return this.status().charAt(0) + this.status().slice(1).toLowerCase();
  }
}
