import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  Api_Key = "67767c953a850325dc1cac4a:QdIciZavFOTjZeoo1hcUOgRpAE";
  wallet_Id = "67767c953a850325dc1cac52";
  localUrl = "https://smartbookify.savooria.com/api/auth/"
  constructor(private http: HttpClient , private authService : AuthService) { }

  initPayURL() {
    var url = "https://api.konnect.network/api/v2/payments/init-payment";
    const body = {
      "receiverWalletId": this.wallet_Id,
      "token": "TND",
      "amount": 10000,
      "successUrl": "https://smartbookify.savooria.com/payed",
      "failUrl": "https://smartbookify.savooria.com/profile",
      "theme": "dark"
    }
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'x-api-key': this.Api_Key
    });
    return this.http.post(url, body, {
      headers: headers
    })
  }
  initPay( ref : any ){
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authService.getToken()}`
    });
    return this.http.post(this.localUrl+"initPay/" + this.authService.getUserEmail() +"/"+ ref ,
     {} , 
      {
      headers: headers
    });
  }


 pay(){
    const headers = new HttpHeaders({
      Authorization: `Bearer ${this.authService.getToken()}`,
      'Content-Type': 'application/json',
    });
    return this.http.post( this.localUrl+"pay/" + this.authService.getUserEmail(),
     {} , 
      {
      headers: headers
    });
  }
  getPaymentDetails(paymentRef: any) {
    var url = "https://api.konnect.network/api/v2/payments/" + paymentRef;
    return this.http.get(url)
  }
}
