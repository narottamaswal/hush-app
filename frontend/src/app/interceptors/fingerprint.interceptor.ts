import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import {FingerPrintService} from "../services/fingerprint.service";

export const fingerprintInterceptor: HttpInterceptorFn = (req, next) => {
    const fingerPrintService = inject(FingerPrintService);
    const fingerprint = fingerPrintService.getIdentity();
    const isApiRequest = req.url.includes('api');
    if (isApiRequest && fingerprint) {
        const authReq = req.clone({
            setHeaders: {
                'X-Device-Fingerprint': fingerprint
            }
        });
        return next(authReq);
    }
    return next(req);
};