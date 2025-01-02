import { Component, OnInit } from '@angular/core';
import { PaymentService } from '../../services/payment.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-payed',
  standalone: false,
  
  templateUrl: './payed.component.html',
  styleUrl: './payed.component.css'
})
export class PayedComponent  implements OnInit{

  isLoading = true ; 
  constructor(private payementService  : PaymentService ,
    private toatr : ToastrService,
    private router : Router){

  }

  ngOnInit(): void {
    this.isLoading = false;
    this.payementService.pay().subscribe({
      next : (res : any )=> {
        this.isLoading = false ; 
        this.router.navigate(["/profile"])
        window.close();
      },
      error : (err :any )=>{
        this.toatr.error(err.errors)
      }
    })
  }


}
