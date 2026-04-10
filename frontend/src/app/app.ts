import { Component } from '@angular/core';
import { PageShellComponent } from './shared/components/page-shell/page-shell';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [PageShellComponent],
  template: `<app-page-shell></app-page-shell>`,
})
export class App {}
