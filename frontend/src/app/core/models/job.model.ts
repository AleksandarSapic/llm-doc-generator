export type DocJobStatus =
  | 'PENDING'
  | 'CLONING'
  | 'TRAVERSING'
  | 'PROCESSING'
  | 'AGGREGATING'
  | 'COMPLETED'
  | 'FAILED';

export const TERMINAL_STATUSES: readonly DocJobStatus[] = ['COMPLETED', 'FAILED'];

export interface JobSubmittedResponse {
  jobId: string;
  statusUrl: string;
  message: string;
}

export interface JobStatusResponse {
  jobId: string;
  status: DocJobStatus;
  statusMessage: string;
  totalFiles: number;
  processedFiles: number;
  progressPercent: number;
  errorMessage?: string;
  llmProvider: string;
  llmModel: string;
}
