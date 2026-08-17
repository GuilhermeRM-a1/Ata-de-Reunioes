import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReuniaoListaComponent } from './reuniao-lista.component';

describe('ReuniaoListaComponent', () => {
  let component: ReuniaoListaComponent;
  let fixture: ComponentFixture<ReuniaoListaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReuniaoListaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReuniaoListaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
