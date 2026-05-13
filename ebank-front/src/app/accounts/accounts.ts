import { Component, inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AsyncPipe, DatePipe} from '@angular/common';
import {catchError, map, Observable, of} from 'rxjs';
import {Account, AccountListState, RequestStatus} from '../model/account.model';
import {LoadingService} from '../services/loading';

@Component({
  selector: 'app-accounts',
  imports: [
    AsyncPipe,
    DatePipe
  ],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css',
})
export class Accounts {
  private http = inject(HttpClient);
  public loadingService = inject(LoadingService);
  accounts$ : Observable<AccountListState>  = this.http.get<Account[]>
  ('http://localhost:8888/EBANK-SERVICE/accounts')
    .pipe(
      map(resp => {
        return {accounts: resp, status: RequestStatus.SUCCESS};
      }),
      catchError((error,caught) => {
        return of({status: RequestStatus.ERROR, errorMessage:error.status.text})
      })
    )

  protected readonly RequestStatus = RequestStatus;
}
