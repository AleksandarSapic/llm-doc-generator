export interface GenerateDocumentationRequest {
  repositoryUrl: string;
  provider?: 'openai' | 'anthropic' | 'ollama';
  model?: string;
  fileExplanationTemplate?: string;
  projectSummaryTemplate?: string;
}

export interface DocumentationResponse {
  jobId: string;
  repositoryUrl: string;
  markdownContent: string;
  generatedAt: string;
  totalFilesDocumented: number;
}
