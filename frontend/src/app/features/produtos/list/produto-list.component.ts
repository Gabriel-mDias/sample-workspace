import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProdutoStore } from '../services/produto.store';
import { ProdutoFilterModel } from '../models/produto-filter.model';
import { ProdutoResponseModel } from '../models/produto-response.model';
import { GEMS_TABLE_LABELS_PT_BR } from '../../../commons/gems-table-labels';
import { AuthService } from '../../../core/auth/auth.service';
import { Roles } from '../../../core/auth/roles';

import {
  GemsTableComponent,
  GemsFormCardComponent,
  GemsInputTextComponent,
  GemsButtonComponent,
  GemsTableColumn,
  GemsTableAction,
  GemsAlertService,
  GemsNavigationService,
  GemsPageable
} from '@gabriel-mdias/angular-gems-sdk';

@Component({
  selector: 'app-produto-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    GemsTableComponent,
    GemsFormCardComponent,
    GemsInputTextComponent,
    GemsButtonComponent
  ],
  templateUrl: './produto-list.component.html',
  styleUrls: ['./produto-list.component.css']
})
export class ProdutoListComponent implements OnInit {

  private store = inject(ProdutoStore);
  private alertService = inject(GemsAlertService);
  private nav = inject(GemsNavigationService);
  private auth = inject(AuthService);

  // Estado de dados (signals)
  produtos = signal<any[]>([]);
  isLoading = signal(false);
  totalElements = signal(0);

  // Filtro e paginação
  filter: ProdutoFilterModel = {};
  pageable: GemsPageable = { page: 0, size: 10, sort: ['dataCriacao,desc'] };

  readonly tableLabels = GEMS_TABLE_LABELS_PT_BR;

  // Visibilidade da ação de exclusão — apenas ADMIN (DELETE é ADMIN-only no backend)
  isAdmin = signal(this.auth.hasRole(Roles.ADMIN));

  columns: GemsTableColumn[] = [
    { field: 'nome', header: 'Nome' },
    { field: 'precoFormatado', header: 'Preço' }
  ];

  actions: GemsTableAction[] = [
    { actionName: 'view', icon: 'fa-solid fa-eye', tooltip: 'Visualizar', colorClass: 'btn-view' },
    { actionName: 'edit', icon: 'fa-solid fa-pen', tooltip: 'Editar', colorClass: 'btn-edit' },
    {
      actionName: 'delete',
      icon: 'fa-solid fa-trash',
      tooltip: 'Excluir',
      colorClass: 'btn-delete',
      visible: () => this.isAdmin()
    }
  ];

  ngOnInit(): void {
    this.search();
  }

  search(): void {
    this.pageable.page = 0;
    this.loadProdutos();
  }

  clearFilters(): void {
    this.filter = {};
    this.search();
  }

  loadProdutos(): void {
    this.isLoading.set(true);

    this.store.search(this.filter, this.pageable).subscribe({
      next: (res) => {
        const mapped = res.content.map((x: ProdutoResponseModel) => ({
          ...x,
          precoFormatado: (x.preco ?? 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
        }));
        this.produtos.set(mapped);
        this.totalElements.set(res.totalElements);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.alertService.errorFromApi(err);
        this.isLoading.set(false);
      }
    });
  }

  onPageChange(newPageable: GemsPageable): void {
    this.pageable = newPageable;
    this.loadProdutos();
  }

  onAction(event: { action: string; row: any }): void {
    if (event.action === 'view') {
      this.nav.navigate(['/produtos', event.row.id]);
    } else if (event.action === 'edit') {
      this.nav.navigate(['/produtos', event.row.id, 'editar']);
    } else if (event.action === 'delete') {
      this.deleteProduto(event.row);
    }
  }

  async deleteProduto(item: any): Promise<void> {
    const confirmado = await this.alertService.confirm(
      'Atenção',
      `Deseja realmente excluir o produto "${item.nome}"? Esta ação é irreversível.`,
      'Excluir',
      'Cancelar'
    );

    // O SweetAlertResult do confirm() tem a prop .isConfirmed
    if (!confirmado.isConfirmed) {
      return;
    }

    this.isLoading.set(true);

    this.store.deleteProduto(item.id).subscribe({
      next: () => {
        this.alertService.success('Sucesso', 'Produto excluído com sucesso.');
        this.loadProdutos();
      },
      error: (err) => {
        this.alertService.errorFromApi(err);
        this.isLoading.set(false);
      }
    });
  }
}
