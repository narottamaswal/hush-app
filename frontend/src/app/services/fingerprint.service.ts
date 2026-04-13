import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class FingerPrintService {

    getIdentity() {
        const cachedFingerprint = localStorage.getItem('fingerprint');
        if (cachedFingerprint) {
            return cachedFingerprint;
        }
        const navigatorInfo = window.navigator.userAgent;
        const screenInfo = `${window.screen.width}x${window.screen.height}`;
        const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone;
        const fingerprint = btoa(`${navigatorInfo}|${screenInfo}|${timeZone}`);
        localStorage.setItem('fingerprint', fingerprint);
        return fingerprint;
    }
}
