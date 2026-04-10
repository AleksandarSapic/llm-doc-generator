import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { JobStatusResponse, TERMINAL_STATUSES } from '../models/job.model';

@Injectable({ providedIn: 'root' })
export class SseService {
  streamJobStatus(jobId: string): Observable<JobStatusResponse> {
    return new Observable<JobStatusResponse>(observer => {
      const url = `${environment.apiBaseUrl}/api/v1/jobs/${jobId}/stream`;
      const es = new EventSource(url);

      es.addEventListener('status', (event: MessageEvent) => {
        try {
          const data = JSON.parse(event.data) as JobStatusResponse;
          observer.next(data);
          if (TERMINAL_STATUSES.includes(data.status)) {
            observer.complete();
            es.close();
          }
        } catch {
          observer.error(new Error('Failed to parse SSE event data'));
          es.close();
        }
      });

      es.onerror = () => {
        observer.error(new Error('SSE connection failed'));
        es.close();
      };

      return () => es.close();
    });
  }
}
