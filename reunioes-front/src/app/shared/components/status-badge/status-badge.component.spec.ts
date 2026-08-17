import { ComponentFixture, TestBed } from '@angular/core/testing';

import { STATUS_REUNIAO, StatusReuniao } from '../../../core/models';
import { StatusBadgeComponent } from './status-badge.component';

describe('StatusBadgeComponent', () => {
  let component: StatusBadgeComponent;
  let fixture: ComponentFixture<StatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusBadgeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatusBadgeComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('status', 'CONCLUIDA' as StatusReuniao);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('mostra o rótulo legível em vez da constante crua', () => {
    const texto = fixture.nativeElement.textContent.trim();

    expect(texto).toBe('Concluída');
  });

  it('aplica uma classe distinta para cada um dos 5 status', () => {
    const classes = STATUS_REUNIAO.map((status) => {
      fixture.componentRef.setInput('status', status);
      fixture.detectChanges();

      return fixture.nativeElement.querySelector('.status-badge').className;
    });

    expect(new Set(classes).size).toBe(5);
  });
});
