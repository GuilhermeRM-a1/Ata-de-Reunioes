import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ReuniaoDetalheComponent } from './reuniao-detalhe.component';
import { ReuniaoStoreService } from '../../../../core/services/reuniao-store.service';

describe('ReuniaoDetalheComponent', () => {
  let component: ReuniaoDetalheComponent;
  let fixture: ComponentFixture<ReuniaoDetalheComponent>;

  // Mock do service para suprir a busca no construtor
  const mockReuniaoStoreService = {
    buscarPorId: jasmine.createSpy('buscarPorId').and.returnValue(undefined),
    remover: jasmine.createSpy('remover')
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReuniaoDetalheComponent],
      providers: [
        provideRouter([]), // Fornece dependências de rotas (ActivatedRoute e Router)
        { provide: ReuniaoStoreService, useValue: mockReuniaoStoreService }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReuniaoDetalheComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});