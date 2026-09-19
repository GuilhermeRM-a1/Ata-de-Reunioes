import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReuniaoEditarComponent } from './reuniao-editar.component';

describe('ReuniaoEditarComponent', () => {
  let component: ReuniaoEditarComponent;
  let fixture: ComponentFixture<ReuniaoEditarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReuniaoEditarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReuniaoEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
