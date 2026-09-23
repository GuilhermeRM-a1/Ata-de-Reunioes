import { ComponentFixture, TestBed } from '@angular/core/testing';

import { STATUS_REUNIAO } from '../../../core/models';
import { StatusBadgeComponent } from './status-badge.component';

describe('StatusBadgeComponent', () => {
  let component: StatusBadgeComponent;
  let fixture: ComponentFixture<StatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusBadgeComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(StatusBadgeComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.componentRef.setInput('status', 'PENDENTE');
    fixture.componentRef.setInput('tipo', 'reuniao');
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('mostra o rotulo legivel em vez da constante crua', () => {
    fixture.componentRef.setInput('status', 'CONCLUIDA');
    fixture.componentRef.setInput('tipo', 'transcricao');
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent.trim()).toBe('Concluída');
  });

  it('aplica uma classe distinta para cada status de reuniao', () => {
    fixture.componentRef.setInput('tipo', 'reuniao');

    const classes = STATUS_REUNIAO.map((status) => {
      fixture.componentRef.setInput('status', status);
      fixture.detectChanges();

      return fixture.nativeElement.querySelector('span').className;
    });

    expect(new Set(classes).size).toBe(STATUS_REUNIAO.length);
  });

  it('nao pinta PENDENTE com a mesma cor de ERRO', () => {
    fixture.componentRef.setInput('tipo', 'reuniao');
    fixture.componentRef.setInput('status', 'PENDENTE');
    fixture.detectChanges();
    const pendente = fixture.nativeElement.querySelector('span').className;

    fixture.componentRef.setInput('tipo', 'transcricao');
    fixture.componentRef.setInput('status', 'ERRO');
    fixture.detectChanges();
    const erro = fixture.nativeElement.querySelector('span').className;

    expect(pendente).not.toBe(erro);
  });
});
