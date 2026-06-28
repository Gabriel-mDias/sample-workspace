import { buildEnvironment, RuntimeEnvironmentOverrides } from './environment.model';

const runtimeEnv = (globalThis as typeof globalThis & {
  __env?: RuntimeEnvironmentOverrides;
}).__env ?? {};

export const environment = buildEnvironment({
  production: false,
  apiBaseUrl: 'http://localhost:8080',
  tenantHeaderName: 'X-Tenant-Id',
  defaultTenant: 'sample',
  oidc: {
    issuer: 'http://localhost:8443/realms/sample-realm',
    clientId: 'sample-frontend',
    redirectUri: `${window.location.origin}/callback`,
    postLogoutRedirectUri: window.location.origin,
    scope: 'openid profile email organization',
    organizationClaim: 'organization'
  }
}, runtimeEnv);

