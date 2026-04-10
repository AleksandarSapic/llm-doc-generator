import { Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CdkTextareaAutosize } from '@angular/cdk/text-field';

export interface TemplateValues {
  fileExplanationTemplate: string;
  projectSummaryTemplate: string;
}

@Component({
  selector: 'app-template-editor',
  standalone: true,
  imports: [FormsModule, MatFormFieldModule, MatInputModule, CdkTextareaAutosize],
  templateUrl: './template-editor.html',
  styleUrl: './template-editor.css',
})
export class TemplateEditorComponent {
  readonly values = input.required<TemplateValues>();
  readonly valuesChange = output<TemplateValues>();

  onFileTemplateChange(event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    this.valuesChange.emit({
      ...this.values(),
      fileExplanationTemplate: target.value,
    });
  }

  onSummaryTemplateChange(event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    this.valuesChange.emit({
      ...this.values(),
      projectSummaryTemplate: target.value,
    });
  }
}
