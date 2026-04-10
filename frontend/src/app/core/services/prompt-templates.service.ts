import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { PromptTemplatesResponse } from '../models/prompt-templates.model';

@Injectable({ providedIn: 'root' })
export class PromptTemplatesService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/api/v1/documentation`;

  getDefaults(): Observable<PromptTemplatesResponse> {
    return this.http.get<PromptTemplatesResponse>(`${this.base}/prompt-templates`);
  }
}
