import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Dashboard } from '../models/dashboard.model';
import { API } from '../config/api.config';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly apiUrl = API.dashboard;
  private readonly http = inject(HttpClient);

  buscar(): Observable<Dashboard> {
    return this.http.get<Dashboard>(this.apiUrl);
  }
}