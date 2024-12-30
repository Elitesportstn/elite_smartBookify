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

     this.applicationService.download(app.id).subscribe({
      next: (response: any) => {
        const blob = response.body as Blob;
        const url = window.URL.createObjectURL(blob);
        const a: HTMLAnchorElement = document.createElement('a') as HTMLAnchorElement;
        a.href = url;
        const contentDisposition = response.headers.get('Content-Disposition');
        let filename = app.name+'.apk';

        if (contentDisposition) {
          const matches = /filename="([^"]+)"/.exec(contentDisposition);
          if (matches != null && matches[1]) {
            filename = matches[1];
          }
        }

        a.download = filename;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);

        this.toastr.success('Download successful');
      },
      error: (err: any) => {
        console.error('Download error:', err);
        this.toastr.error(err.error?.message || 'Download failed');
      }
    }); 
  }

  onDeleteApplication(index: number) {
    this.toastr.error(this.applications[index].name + "(" + this.applications[index].version+ ")")
  }

  onAddApplication() {
    this.router.navigate(["create"])
  }
}
