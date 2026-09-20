import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { ReuniaoDetalheComponent } from './reuniao-detalhe.component';

describe('ReuniaoDetalheComponent', () => {
  let component: ReuniaoDetalheComponent;
  let fixture: ComponentFixture<ReuniaoDetalheComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReuniaoDetalheComponent],
      providers: [provideRouter([]), provideHttpClient()]
    }).compileComponents();

    fixture = TestBed.createComponent(ReuniaoDetalheComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
