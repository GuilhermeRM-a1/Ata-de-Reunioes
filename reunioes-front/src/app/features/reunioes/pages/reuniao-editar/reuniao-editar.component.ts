import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-reuniao-editar',
  imports: [],
  templateUrl: './reuniao-editar.component.html',
  styleUrl: './reuniao-editar.component.scss'
})
export class ReuniaoEditarComponent implements OnInit {
  id: string | null = null;

  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.id = params.get('id');
    });
  }

  navigateBack(): void {
    if (this.id) {
      this.router.navigate([`/reunioes/${this.id}`], { 
        queryParams: { sucesso: 'true' } 
      });
    }
  }

}
