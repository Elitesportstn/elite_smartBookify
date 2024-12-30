import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-login',
  standalone: false,
  
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  isLoading: boolean = false;

  constructor(private authService : AuthService , 
    private router : Router,
    private toastr: ToastrService
  ){}

  onLogin() {
    this.isLoading = true;
    var body = { 
      'email' : this.email,
      'password': this.password
    }
    this.authService.login(body).subscribe({
      next: (res : any )=>{
        this.authService.setToken(res.token);
        console.log(res.token);
        
        this.router.navigate(['/home'])
      },
      error: (err : any )=>{
        this.isLoading = false;
        this.toastr.error(err.error.message);
      }
    })
  }
}