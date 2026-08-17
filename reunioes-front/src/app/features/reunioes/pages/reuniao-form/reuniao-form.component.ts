import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormArray, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ReuniaoStoreService } from '../../../../core/mock/reuniao-store.service'; 

@Component({
  selector: 'app-reuniao-form',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './reuniao-form.component.html',
  styleUrl: './reuniao-form.component.scss',
})
export class ReuniaoFormComponent implements OnInit {
  form!: FormGroup;
  idEditando: number | null = null; 
  idNaoEncontrado = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private store: ReuniaoStoreService, 
    private router: Router
  ) {
    this.form = this.fb.group({
      tituloReuniao: ['', [Validators.required, Validators.minLength(5)]],
      dataProcessamento: ['', Validators.required],
      resumoExecutivo: [''],
      status: ['', Validators.required],
      areas: [''],
      participantes: [''],
      acoes: this.fb.array([])
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    
    if (idParam) {
      this.idEditando = Number(idParam);
      const reuniao = this.store.buscarPorId(this.idEditando); 

      if (reuniao) {
        this.form.patchValue(reuniao);
      } else {
        this.idNaoEncontrado = true;
      }
    }
  }

  get acoes(): FormArray {
    return this.form.get('acoes') as FormArray;
  }

  adicionarAcao(): void {
    const novaAcao = this.fb.group({
      descricao: ['', Validators.required],
      responsavel: ['', Validators.required]
    });
    this.acoes.push(novaAcao);
  }

  salvar(): void {
    if (this.form.invalid) {
      return;
    }

    const dados = this.form.value;

    if (this.idEditando !== null) {
      this.store.atualizar(this.idEditando, dados); 
    } else {
      this.store.criar(dados); 
    }

    this.router.navigate(['/reunioes']);
  }

  cancelar(): void {
    this.router.navigate(['/reunioes']);
  }

  removerAcao(index: number): void {
    this.acoes.removeAt(index);
  }
}
