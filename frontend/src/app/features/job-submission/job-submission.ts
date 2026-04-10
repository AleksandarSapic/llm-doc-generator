import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { TemplateEditorComponent, TemplateValues } from './template-editor/template-editor';
import { ErrorBannerComponent } from '../../shared/components/error-banner/error-banner';
import { DocumentationService } from '../../core/services/documentation.service';
import { PromptTemplatesService } from '../../core/services/prompt-templates.service';
import { AppError } from '../../core/interceptors/error.interceptor';

const PROVIDER_DEFAULTS: Record<string, string> = {
  openai: 'gpt-4o-mini',
  anthropic: 'claude-sonnet-4-6',
  ollama: 'gemma3',
};

@Component({
  selector: 'app-job-submission',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatExpansionModule,
    MatProgressSpinnerModule,
    MatIconModule,
    TemplateEditorComponent,
    ErrorBannerComponent,
  ],
  templateUrl: './job-submission.html',
  styleUrl: './job-submission.css',
})
export class JobSubmissionComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly documentationService = inject(DocumentationService);
  private readonly promptTemplatesService = inject(PromptTemplatesService);

  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);
  readonly templatesLoading = signal(false);
  readonly templateValues = signal<TemplateValues>({
    fileExplanationTemplate: '',
    projectSummaryTemplate: '',
  });

  readonly form = this.fb.group({
    repositoryUrl: ['', [Validators.required, Validators.pattern(/^https?:\/\/.+/)]],
    provider: [''],
    model: [''],
  });

  modelPlaceholder(): string {
    const provider = this.form.get('provider')?.value ?? '';
    return provider ? `e.g. ${PROVIDER_DEFAULTS[provider] ?? 'default model'}` : 'Provider default';
  }

  ngOnInit(): void {
    this.templatesLoading.set(true);
    this.promptTemplatesService.getDefaults().subscribe({
      next: templates => {
        this.templateValues.set({
          fileExplanationTemplate: templates.fileExplanationTemplate,
          projectSummaryTemplate: templates.projectSummaryTemplate,
        });
        this.templatesLoading.set(false);
      },
      error: () => {
        this.templatesLoading.set(false);
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const { repositoryUrl, provider, model } = this.form.getRawValue();
    const templates = this.templateValues();

    this.documentationService.generate({
      repositoryUrl: repositoryUrl!,
      ...(provider ? { provider: provider as 'openai' | 'anthropic' | 'ollama' } : {}),
      ...(model ? { model } : {}),
      ...(templates.fileExplanationTemplate ? { fileExplanationTemplate: templates.fileExplanationTemplate } : {}),
      ...(templates.projectSummaryTemplate ? { projectSummaryTemplate: templates.projectSummaryTemplate } : {}),
    }).subscribe({
      next: response => {
        this.router.navigate(['/jobs', response.jobId, 'status']);
      },
      error: (err: AppError) => {
        this.error.set(err.message);
        this.submitting.set(false);
      },
    });
  }
}
