import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode, JwtPayload } from "jwt-decode";

@Injectable({
  providedIn: 'root'
})
export class AuthService {


  url= 'https://apismartbookify.savooria.com/api/auth/'

  constructor(private http: HttpClient, private router: Router) { }

  login(body: any) {
    return this.http.post(this.url + 'login', body);
  }
  forgetPassword(body: any) {
    return this.http.post(this.url + 'forget-password', body);
  }

  confirmMail(body: any) {
    return this.http.post(this.url + 'confirm-email', body);
  }
  register(body: any) {
    return this.http.post(this.url + 'register', body);
  }

  setToken(token: any) {
    localStorage.setItem("token", token)
  }
  getToken() {
    var token = localStorage.getItem("token") ?? null;
     if (token == null) {
      this.router.navigate(["/login"])
    }
    return token;
  }

  getUserEmail() {
    var token = this.getToken()
    if (token) {
      var decoded = jwtDecode(token)
       
       return decoded.sub
    }
    return null;
  }
  
  isUserSubsribed() {
    var token = this.getToken()
    if (token) {
      var decoded = jwtDecode<CustomJwtPayload>(token);      
       return decoded.user.subscribed
    }
    return null;
  }

   
  getUser() {
    var token = this.getToken()
    if (token) {
      var decoded = jwtDecode<CustomJwtPayload>(token);
       return decoded.user
    }
    return null;
  }


}

interface CustomJwtPayload extends JwtPayload {
  user: {
    subscribed: boolean;
    
  };
}
