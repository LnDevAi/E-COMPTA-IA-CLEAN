import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatTableModule, MatButtonModule, MatSnackBarModule],
  template: `<div></div>`
})
export class EmployeesComponent implements OnInit {
  constructor(private snackBar: MatSnackBar, private api: ApiService) {}
  ngOnInit(): void {
    // placeholder to avoid build errors
  }
}


