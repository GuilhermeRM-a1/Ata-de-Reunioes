import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReuniaoFormComponent } from './reuniao-form.component';

describe('ReuniaoFormComponent', () => {
  let component: ReuniaoFormComponent;
  let fixture: ComponentFixture<ReuniaoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReuniaoFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReuniaoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
