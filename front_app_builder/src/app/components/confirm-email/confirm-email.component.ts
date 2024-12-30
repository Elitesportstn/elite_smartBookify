import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-confirm-email',
  standalone: false,
  
  templateUrl: './confirm-email.component.html',
  styleUrl: './confirm-email.component.css'
})
export class ConfirmEmailComponent {
  confirmationCode: string = '';
  isLoading: boolean = false;

  constructor(private authService : AuthService ,
    private toastr : ToastrService,
    private router : Router){}

  onSubmit() {
    this.isLoading = true;
    if(this.confirmationCode.length!= 6){
      this.toastr.error('Invalid Code');
      this.isLoading = false;
      return;
    }

    var body = {email : localStorage.getItem("emailToConfirm") , code : this.confirmationCode};
    this.authService.confirmMail(body).subscribe({ next : (res : any)=>{
      this.isLoading = false;
      this.router.navigate(["login"])
      this.toastr.success("Email verified")
    },
    error : (err : any)=>{
      
      this.isLoading = false;
       this.toastr.error( err.error.message)    }
  }
     );
    
  }

  onResendCode() {
    this.isLoading = true;

    // Simulate resending confirmation code
    setTimeout(() => {
      alert('A new confirmation code has been sent to your email.');
      this.isLoading = false;
    }, 2000);
  }
}