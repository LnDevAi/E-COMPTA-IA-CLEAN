import { TestBed } from '@angular/core/testing';

import { Crm } from './crm';

describe('Crm', () => {
  let service: Crm;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Crm);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
