import { Component } from '@angular/core';
import { PaymentService } from '../../services/payment.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: false,
  
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  user = {
    name: 'John Doe',
    email: 'john.doe@example.com',
    subscriptionActive: false, // Initial subscription status
  };
  isLoading = false ;

  constructor(
    private router : Router,
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

  onUnsubscribe() {
    // Simulate an unsubscription action
    this.user.subscriptionActive = false;
    alert('Your subscription has been canceled.');
  }
}
