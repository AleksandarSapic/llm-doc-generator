import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment.development';

export interface AppError {
  message: string;
  statusCode?: number;
  timestamp: Date;
}

function normalizeError(error: HttpErrorResponse): AppError {
  let message = 'An unexpected error occurred. Please try again.';

  if (error.status === 0) {
    message = 'Unable to reach the server. Please check your connection.';
  } else if (error.status === 404) {
    message = 'The requested resource was not found.';
  } else if (error.status === 409) {
    message = error.error?.message ?? 'A conflict occurred. A job may already be running for this repository.';
  } else if (error.status >= 400 && error.status < 500) {
    message = error.error?.message ?? `Request error (${error.status}).`;
  } else if (error.status >= 500) {
    message = error.error?.message ?? 'A server error occurred. Please try again later.';
  }

  if (!environment.production) {
    console.error('[HTTP Error]', error);
  }

  return { message, statusCode: error.status, timestamp: new Date() };
}

export const errorInterceptor: HttpInterceptorFn = (req, next) =>
  next(req).pipe(
    catchError((error: HttpErrorResponse) =>
      throwError(() => normalizeError(error))
    )
  );
