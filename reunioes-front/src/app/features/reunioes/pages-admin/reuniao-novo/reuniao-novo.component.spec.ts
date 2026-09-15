import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReuniaoNovoComponent } from './reuniao-novo.component';

describe('ReuniaoNovoComponent', () => {
  let component: ReuniaoNovoComponent;
  let fixture: ComponentFixture<ReuniaoNovoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReuniaoNovoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReuniaoNovoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
