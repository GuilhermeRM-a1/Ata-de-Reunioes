import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeriadoApiDTO } from '../models';


@Injectable({providedIn: 'root'})
export class FeriadoService{
    private readonly apiUrl = 'http://localhost:8080/api/reunioes/feriados'; 


    constructor(private http: HttpClient) {

  }

  verificarData(data:string): Observable<FeriadoApiDTO>{
    return this.http.get<FeriadoApiDTO>(`${this.apiUrl}`, { params: { data } });
  }

}