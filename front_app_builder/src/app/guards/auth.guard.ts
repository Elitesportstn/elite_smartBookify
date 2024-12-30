import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router , private toastr : ToastrService) {}

  canActivate(): boolean {
    const isAuthenticated = !!localStorage.getItem('token');  

    if (isAuthenticated) {
      return true;  
    } else {
      this.toastr.error("session expired please login")
      this.router.navigate(['/login']);  
      return false; 
    }
  }
}
