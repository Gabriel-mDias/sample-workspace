import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProdutoStore } from '../services/produto.store';
import { ProdutoModel } from '../models/produto.model';

import {
  GemsFormCardComponent,
  GemsInputTextComponent,
  GemsInputMaskComponent,
  GemsFieldErrorComponent,
  GemsButtonComponent,
  GemsAlertService,
  GemsNavigationService
} from '@gabriel-mdias/angular-gems-sdk';

@Component({
  selector: 'app-produto-form',
  templateUrl: './produto-form.component.html',
  styleUrls: ['./produto-form.component.css'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    GemsFormCardComponent,
    GemsInputTextComponent,
    GemsInputMaskComponent,
    GemsFieldErrorComponent,
    GemsButtonComponent
  ]
})
export class ProdutoFormComponent implements OnInit {

  private store = inject(ProdutoStore);
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  private alertService = inject(GemsAlertService);
  private nav = inject(GemsNavigationService);

  // Signals de estado
  isLoading = signal(false);
  isEdit = signal(false);

  // Computed
  pageTitle = computed(() => this.isEdit() ? 'Editar Produto' : 'Novo Produto');
  pageSubtitle = computed(() => this.isEdit()
    ? 'Atualize os dados do produto.'
    : 'Preencha os dados abaixo para cadastrar um novo produto.');

  produtoForm: FormGroup;

  constructor() {
    this.produtoForm = this.fb.group({
      nome: ['', [Validators.required]],
      descricao: [''],
      preco: [null, [Validators.required]]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit.set(true);
      this.loadProduto(id);
    }
  }

  loadProduto(id: string): void {
    this.isLoading.set(true);
    this.store.getById(id).subscribe({
      next: (data: ProdutoModel) => {
        this.produtoForm.patchValue({
          nome: data.nome,
          descricao: data.descricao ?? '',
          preco: data.preco
        });
        this.isLoading.set(false);
      },
      error: (err) => {
        this.alertService.errorFromApi(err);
        this.isLoading.set(false);
      }
    });
  }

  save(): void {
    if (this.produtoForm.invalid) {
      this.produtoForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);

    const v = this.produtoForm.getRawValue();
    const dto: ProdutoModel = {
      nome: v['nome'],
      preco: this.parseMoeda(v['preco']),
      ...(v['descricao'] ? { descricao: v['descricao'] } : {})
    };

    const id = this.route.snapshot.paramMap.get('id');
    const call$ = this.isEdit() && id
      ? this.store.update(id, dto)
      : this.store.create(dto);

    call$.subscribe({
      next: () => {
        this.isLoading.set(false);
        this.alertService.success(
          'Sucesso!',
          `Produto ${this.isEdit() ? 'atualizado' : 'cadastrado'} com sucesso.`
        );
        this.nav.navigate(['/produtos']);
      },
      error: (err) => {
        this.alertService.errorFromApi(err);
        this.isLoading.set(false);
      }
    });
  }

  cancel(): void {
    this.nav.back();
  }

  /** Converte o valor do `gems-input-mask` (maskType="moeda", ex.: "R$ 1.234,56") em number. */
  private parseMoeda(value: unknown): number {
    if (typeof value === 'number') {
      return value;
    }
    const raw = (value ?? '').toString().trim();
    if (!raw) {
      return 0;
    }
    const normalized = raw.replace(/[^\d,-]/g, '').replace(',', '.');
    const parsed = parseFloat(normalized);
    return Number.isNaN(parsed) ? 0 : parsed;
  }
}
