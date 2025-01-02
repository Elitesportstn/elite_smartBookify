import { Component } from '@angular/core';
import { PaymentService } from '../../services/payment.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: false,
  
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  user : any;
  isLoading = false ;

  constructor(
    private router : Router,
    private authService : AuthService,
    private paymentService : PaymentService , private toastr : ToastrService){}



    onSubscribe() {
    this.isLoading = true ;
    this.paymentService.initPayURL().subscribe({
      next : (res : any )=>{
        console.log(res);
         this.paymentService.initPay(res.paymentRef).subscribe({
          next : ()=>{
            this.isLoading =false ;

            window.open(res.payUrl , '_blank')
          },
          error : (err : any )=> {
            this.isLoading =false ;
            this.toastr.error(err.errors)}
        })
      },
      error : (err : any)=>{
        this.toastr.error(err);
        console.log(err);
        this.isLoading =false ;
      }
    })
  }

  getCurrentUser(){
    this.user = this.authService.getUser()
  }

  
}
