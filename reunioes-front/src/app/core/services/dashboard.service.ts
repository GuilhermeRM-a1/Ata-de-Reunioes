import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Dashboard } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly apiUrl = 'http://localhost:8080/api/dashboard';
  private readonly http = inject(HttpClient);

  buscar(): Observable<Dashboard> {
    return this.http.get<Dashboard>(this.apiUrl);
  }
}