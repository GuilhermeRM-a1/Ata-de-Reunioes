import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { AcoesComponent } from './acoes.component';

describe('AcoesComponent', () => {
  let component: AcoesComponent;
  let fixture: ComponentFixture<AcoesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AcoesComponent],
      providers: [provideHttpClient(), provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AcoesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
