import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ProdutoStore } from '../services/produto.store';
import { ProdutoModel } from '../models/produto.model';

import {
  GemsSummaryCardComponent,
  GemsButtonComponent,
  GemsAlertService,
  GemsNavigationService
} from '@gabriel-mdias/angular-gems-sdk';

@Component({
  selector: 'app-produto-view',
  standalone: true,
  imports: [
    CommonModule,
    GemsSummaryCardComponent,
    GemsButtonComponent
  ],
  templateUrl: './produto-view.component.html',
  styleUrls: ['./produto-view.component.css']
})
export class ProdutoViewComponent implements OnInit {

  private store = inject(ProdutoStore);
  private route = inject(ActivatedRoute);
  private alertService = inject(GemsAlertService);
  private nav = inject(GemsNavigationService);

  // Signals de estado
  isLoading = signal(false);
  produto = signal<ProdutoModel | null>(null);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadProduto(id);
    }
  }

  loadProduto(id: string): void {
    this.isLoading.set(true);
    this.store.getById(id).subscribe({
      next: (data: ProdutoModel) => {
        this.produto.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.alertService.errorFromApi(err);
        this.isLoading.set(false);
      }
    });
  }

  goBack(): void {
    this.nav.back();
  }

  edit(): void {
    const id = this.produto()?.id;
    if (id) {
      this.nav.navigate(['/produtos', id, 'editar']);
    }
  }
}
