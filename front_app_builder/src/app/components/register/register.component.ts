import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-register',
  standalone: false,
  
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent { registerForm: FormGroup;
  isLoading = false;

  constructor(private fb: FormBuilder , 
    private toastr : ToastrService,
    private router : Router,
    private authService: AuthService) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    return password === confirmPassword ? null : { mismatch: true };
  }

  onRegister() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      var body ={
        "username" : this.registerForm.get('name')?.value,
        "email" : this.registerForm.get('email')?.value,
        "password" :this.registerForm.get('password')?.value,
        "phone" : this.registerForm.get('phone')?.value,
        "role" : "USER"
    }
    localStorage.setItem("emailToConfirm" , this.registerForm.get("email")?.value);
    this.authService.register(body).subscribe({
      next:(res : any)=>{
        console.log(res);
        this.isLoading = false;
        this.toastr.success("please check your email")
        this.router.navigate(["/confirm-email"])
      }, 
      error : (err : any)=>{
        this.isLoading = false;
        this.toastr.error(err.error.message);
      }
    })
    
    
    } 
  }
}