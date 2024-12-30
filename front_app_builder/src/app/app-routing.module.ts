import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './components/login/login.component';
import { ListComponent } from './components/list/list.component';
import { CreateComponent } from './components/create/create.component';
import { RegisterComponent } from './components/register/register.component';
import { ForgetpasswordComponent } from './components/forgetpassword/forgetpassword.component';
import { ConfirmEmailComponent } from './components/confirm-email/confirm-email.component';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  // Redirect empty path to 'login'
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // Main routes
  { path: 'login', component: LoginComponent },
  { path: 'home', component: ListComponent, canActivate: [AuthGuard] },
  { path: 'create', component: CreateComponent, canActivate: [AuthGuard] },
  { path: 'register', component: RegisterComponent },
  { path: 'forget-password', component: ForgetpasswordComponent },
  { path: 'confirm-email', component: ConfirmEmailComponent },

  // Wildcard (catch-all) route. Redirect any unknown path to 'login'
  { path: '**', redirectTo: 'login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
