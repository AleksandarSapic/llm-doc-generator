import { Component, OnInit, inject, input, signal } from '@angular/core';
import { Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ErrorBannerComponent } from '../../shared/components/error-banner/error-banner';
import { MarkdownPipe } from '../../core/pipes/markdown.pipe';
import { DocumentationService } from '../../core/services/documentation.service';
import { DocumentationResponse } from '../../core/models/documentation.model';
import { AppError } from '../../core/interceptors/error.interceptor';

@Component({
  selector: 'app-documentation-result',
  standalone: true,
  imports: [
    DatePipe,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    ErrorBannerComponent,
    MarkdownPipe,
  ],
  templateUrl: './documentation-result.html',
  styleUrl: './documentation-result.css',
})
export class DocumentationResultComponent implements OnInit {
  readonly jobId = input.required<string>();

  readonly router = inject(Router);
  private readonly documentationService = inject(DocumentationService);

  readonly doc = signal<DocumentationResponse | null>(null);
  readonly isLoading = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.documentationService.getResult(this.jobId()).subscribe({
      next: result => {
        this.doc.set(result);
        this.isLoading.set(false);
      },
      error: (err: AppError) => {
        this.error.set(err?.message ?? 'Failed to load documentation.');
        this.isLoading.set(false);
      },
    });
  }

  downloadMarkdown(): void {
    this.documentationService.getRawMarkdown(this.jobId()).subscribe({
      next: content => {
        const blob = new Blob([content], { type: 'text/markdown;charset=utf-8' });
        const url = URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = `documentation-${this.jobId()}.md`;
        anchor.click();
        URL.revokeObjectURL(url);
      },
      error: (err: AppError) => {
        this.error.set(err?.message ?? 'Download failed.');
      },
    });
  }
}
