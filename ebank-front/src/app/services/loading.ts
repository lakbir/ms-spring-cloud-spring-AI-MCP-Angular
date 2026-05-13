import {Injectable, signal} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  loading = signal<boolean>(false);

  public isLoading = this.loading.asReadonly();

  public setLoakding(value: boolean) {
    this.loading.set(value);
  }
}
