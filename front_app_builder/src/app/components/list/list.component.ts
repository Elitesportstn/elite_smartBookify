import { HttpClient, HttpResponse } from '@angular/common/http';
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


  

  download(app: any) {
    this.applicationService.download(app.id).subscribe({
      next: (response: HttpResponse<Blob>) => {
        const blob = response.body as Blob;
        const url = window.URL.createObjectURL(blob);
  
        const a: HTMLAnchorElement = document.createElement('a');
        a.href = url;
  
        // Determine filename from response headers or fallback to default
        const contentDisposition = response.headers.get('Content-Disposition');
        let filename = `${app.name}.apk`;
  
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
      error: (error) => {
        console.error('Download error:', error);
        this.toastr.error('Failed to download the file. Please try again later.');
      },
    });
  }
  
  

  onDeleteApplication(index: number) {
    this.toastr.error(this.applications[index].name + "(" + this.applications[index].version+ ")")
  }

  onAddApplication() {
    this.router.navigate(["create"])
  }
}
