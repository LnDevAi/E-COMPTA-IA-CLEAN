import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PayrollCalculations } from './payroll-calculations';

describe('PayrollCalculations', () => {
  let component: PayrollCalculations;
  let fixture: ComponentFixture<PayrollCalculations>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PayrollCalculations]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PayrollCalculations);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
