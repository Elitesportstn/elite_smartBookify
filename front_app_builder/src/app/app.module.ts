import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
 import { ListComponent } from './components/list/list.component';
import { CreateComponent } from './components/create/create.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms'
import {  provideHttpClient } from '@angular/common/http';
import { NavComponent } from './components/nav/nav.component';
import { RegisterComponent } from './components/register/register.component';
import { ForgetpasswordComponent } from './components/forgetpassword/forgetpassword.component';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ListComponent,
    CreateComponent,
    NavComponent,
    RegisterComponent,
    ForgetpasswordComponent,
    ConfirmEmailComponent,
    ],
  imports: [
    BrowserModule,
    AppRoutingModule,
      FormsModule,
     ReactiveFormsModule,
     ToastrModule.forRoot(),  
     BrowserAnimationsModule,
     
   ],
  providers: [
    provideHttpClient(),
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
