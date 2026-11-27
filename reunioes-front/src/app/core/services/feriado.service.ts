import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeriadoApiDTO } from '../models';
import { API } from '../config/api.config';


@Injectable({providedIn: 'root'})
export class FeriadoService{
    private readonly apiUrl = API.feriados; 


    constructor(private http: HttpClient) {

  }

  verificarData(data:string): Observable<FeriadoApiDTO>{
    return this.http.get<FeriadoApiDTO>(`${this.apiUrl}`, { params: { data } });
  }

}