import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GemsBaseStore, GemsPageable } from '@gabriel-mdias/angular-gems-sdk';
import { ProdutoModel } from '../models/produto.model';
import { ProdutoFilterModel } from '../models/produto-filter.model';
import { ProdutoResponseModel } from '../models/produto-response.model';

@Injectable({
  providedIn: 'root'
})
export class ProdutoStore extends GemsBaseStore {

  constructor() {
    super('api/produtos');
  }

  getById(id: string): Observable<ProdutoModel> {
    return this.get<ProdutoModel>(`/${id}`);
  }

  search(
    filter: ProdutoFilterModel,
    pageable: GemsPageable
  ): Observable<{ content: ProdutoResponseModel[]; totalElements: number }> {
    return this.post<{ content: ProdutoResponseModel[]; totalElements: number }, ProdutoFilterModel>(
      '/search',
      filter,
      { pageable }
    );
  }

  create(dto: ProdutoModel): Observable<ProdutoModel> {
    return this.post<ProdutoModel, ProdutoModel>('', dto);
  }

  update(id: string, dto: ProdutoModel): Observable<ProdutoModel> {
    return this.put<ProdutoModel, ProdutoModel>(`/${id}`, dto);
  }

  deleteProduto(id: string): Observable<void> {
    return this.delete<void>(`/${id}`);
  }
}
