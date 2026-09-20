import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ColaboradorService } from './colaborador.service';

describe('ColaboradorService', () => {
  let service: ColaboradorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ColaboradorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
