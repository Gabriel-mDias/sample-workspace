import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withFetch, withInterceptorsFromDi } from '@angular/common/http';
import { provideGemsHttp, provideGemsTheme } from '@gabriel-mdias/angular-gems-sdk';
import { provideGemsKeycloak } from '@gabriel-mdias/angular-gems-sdk/auth';

import { routes } from './app.routes';
import { environment } from '../environments/environment';
import { keycloakOptions } from './core/auth/keycloak.init';

export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        provideRouter(routes),
        // Necessário para os componentes da SDK que usam a API de animações clássica (ex.: o
        // [@expandCollapse] do gems-summary-card). provideAnimationsAsync está deprecado na v20.2
        // (remoção prevista v23), mas é requisito enquanto a SDK depender dessa API. Carrega o
        // módulo de animações sob demanda (lazy), mantendo o bundle inicial menor.
        provideAnimationsAsync(),
        provideHttpClient(withFetch(), withInterceptorsFromDi()),
        provideGemsKeycloak(keycloakOptions()),
        provideGemsTheme({
            primary: '#12AFA3',    // Verde teal — cor primária (destaque principal)
            secondary: '#49D6C2',  // Verde mentha — cor secundária
            tertiary: '#142236',   // Azul-marinho escuro — texto principal
            background: '#F8FAFC', // Off-white — fundo da aplicação
            danger: '#dc2626',   // opcional
            success: '#16a34a',  // opcional
            warning: '#d97706',  // opcional
            info: '#0284c7',     // opcional
        }),
        provideGemsHttp(environment.apiBaseUrl)
    ]
};
