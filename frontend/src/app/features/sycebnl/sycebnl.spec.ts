import { TestBed } from '@angular/core/testing';

import { Sycebnl } from './sycebnl';

describe('Sycebnl', () => {
  let service: Sycebnl;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Sycebnl);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
