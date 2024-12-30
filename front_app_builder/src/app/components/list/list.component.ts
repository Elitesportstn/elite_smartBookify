import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ApplicationService } from '../../services/application.service';
import { Application } from '../../models/models';
import { Observable } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-list',
  standalone: false,
  templateUrl: './list.component.html',
  styleUrl: './list.component.css'
})
export class ListComponent implements OnInit{

 
  applications :  Application[] =[];


  constructor(private applicationService : ApplicationService ,
    private router : Router,
    private authService : AuthService,
    private toastr: ToastrService){}


  ngOnInit(): void {
      this.getUserApps();
      
   }
  getUserApps() {  
    
   this.applicationService.getUserApps(this.authService.getUserEmail()).subscribe({
    next : (res : any )=> {
      this.applications = res ;
    },
    error : (err : any )=> {
      console.log(err);
      this.toastr.error(err.error.message);
    }
   })
  }


  

  onViewApplication(app: any) {
    this.toastr.info(app.name + "(" + app.version+ ")")
  }

  onDeleteApplication(index: number) {
    this.toastr.error(this.applications[index].name + "(" + this.applications[index].version+ ")")
  }

  onAddApplication() {
    this.router.navigate(["create"])
  }
}
