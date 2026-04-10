import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private requestCount = 0;
  readonly isLoading = signal(false);

  increment(): void {
    this.requestCount++;
    this.isLoading.set(true);
  }

  decrement(): void {
    this.requestCount = Math.max(0, this.requestCount - 1);
    if (this.requestCount === 0) {
      this.isLoading.set(false);
    }
  }
}
