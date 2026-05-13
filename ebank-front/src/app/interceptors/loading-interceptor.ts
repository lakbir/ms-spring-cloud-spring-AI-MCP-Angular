import { HttpInterceptorFn } from '@angular/common/http';
import {LoadingService} from '../services/loading';
import {inject} from '@angular/core';
import {finalize} from 'rxjs';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {

  const loadingService = inject(LoadingService);
  loadingService.setLoakding(true);
  return next(req).pipe(finalize(() => {
    loadingService.setLoakding(false);
  }));
};
