import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { GenerateDocumentationRequest, DocumentationResponse } from '../models/documentation.model';
import { JobSubmittedResponse } from '../models/job.model';

@Injectable({ providedIn: 'root' })
export class DocumentationService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/api/v1/documentation`;

  generate(request: GenerateDocumentationRequest): Observable<JobSubmittedResponse> {
    return this.http.post<JobSubmittedResponse>(`${this.base}/generate`, request);
  }

  getResult(jobId: string): Observable<DocumentationResponse> {
    return this.http.get<DocumentationResponse>(`${this.base}/${jobId}`);
  }

  getRawMarkdown(jobId: string): Observable<string> {
    return this.http.get(`${this.base}/${jobId}/raw`, { responseType: 'text' });
  }
}
