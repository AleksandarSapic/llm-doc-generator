import { Component, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-error-banner',
  standalone: true,
  imports: [MatIconModule, MatButtonModule],
  templateUrl: './error-banner.html',
  styleUrl: './error-banner.css',
})
export class ErrorBannerComponent {
  readonly message = input<string | null>(null);
  readonly dismissed = output<void>();
}
