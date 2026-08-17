import { ComponentFixture, TestBed } from '@angular/core';
import { provideRouter } from '@angular/router';
import { ReunioesComponent } from './reunioes.component';
import { ReuniaoStoreService } from '../../../../core/services/reuniao-store.service';

describe('ReunioesComponent', () => {
  let component: ReunioesComponent;
  let fixture: ComponentFixture<ReunioesComponent>;

  // Mock do service com a propriedade reunioes retornando um array vazio de acordo com o model
  const mockReuniaoStoreService = {
    reunioes: () => [],
    remover: jasmine.createSpy('remover')
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReunioesComponent],
      providers: [
        provideRouter([]), // Para suprir as rotas (Router)
        { provide: ReuniaoStoreService, useValue: mockReuniaoStoreService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReunioesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});