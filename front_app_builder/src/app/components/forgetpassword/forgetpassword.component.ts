import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-forgetpassword',
  standalone: false,
  
  templateUrl: './forgetpassword.component.html',
  styleUrl: './forgetpassword.component.css'
})
export class ForgetpasswordComponent {
  email: string = '';
  isLoading: boolean = false;


  constructor(private authService : AuthService  , private router : Router , 
    private toastr : ToastrService
  ){}

  onSubmit() {
    this.isLoading = true;
    var body = {"email" : this.email};
    this.authService.forgetPassword(body).subscribe({
      next : (res : any )=>{
        this.isLoading = false;
        this.toastr.success("email sent ")
        this.router.navigate(["login"])
      },
      error : (err : any )=>{
        this.isLoading = false;
        this.toastr.error(err.error.message);
      }
    })
  }
}
