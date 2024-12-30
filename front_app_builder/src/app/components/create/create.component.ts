import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ApplicationService } from '../../services/application.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-create',
  standalone: false,
  
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent implements OnInit{
 
  email: string = '';
  name: string = '';
     selectedFile : any   ;
  formData = new FormData();
  isLoading: boolean = false;
  constructor(private applicationService : ApplicationService ,
    private router : Router,
    private authService : AuthService,
    private toastr : ToastrService){}
  ngOnInit(): void {
   this.email =  this.authService.getUserEmail()!;
  }

  onFileSelected(event: any): void {
    this.selectedFile =  event.target.files[0];
    this.formData.append('file' , this.selectedFile , this.selectedFile.name)
    console.log(this.formData);
    const fd = new FormData();
  fd.append('upload_file',this.selectedFile, this.selectedFile.name);
  console.log('fd ' , fd);
  
    
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    if (event.dataTransfer?.files) {
      this.selectedFile =  event.dataTransfer.files[0];
    }
  }


  onSubmit(): void {
    if (!this.email || this.selectedFile === 0) {
      this.toastr.error('Please provide an email and upload at least one file.');
      return;
    }
    
    if (this.selectedFile) {
      this.isLoading = true;
      this.formData.append('email', this.authService.getUserEmail()!);
      if(this.formData.has('appname')){
        this.formData.set('appname' , this.name)
      } else {
        this.formData.append('appname' , this.name)

      }
       
      const upload$ = this.applicationService.create(this.formData)
      console.log(upload$);
      
      upload$.subscribe({
        next: () => {
          this.isLoading = false;
            this.router.navigate(["home"])
            this.toastr.success('Application generated successfully!');
        },
        error: (error: any) => {
                    this.isLoading = false;
           
          this.toastr.error('Upload failed: ' +error.error.message);
         },
      });
    }
  }
  
}
