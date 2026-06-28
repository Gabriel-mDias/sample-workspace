import { buildEnvironment, RuntimeEnvironmentOverrides } from './environment.model';

const runtimeEnv = (globalThis as typeof globalThis & {
  __env?: RuntimeEnvironmentOverrides;
}).__env ?? {};

export const environment = buildEnvironment({
  production: true,
  apiBaseUrl: 'https://api.sample.example.com',
  tenantHeaderName: 'X-Tenant-Id',
  defaultTenant: 'sample',
  oidc: {
    issuer: 'https://auth.sample.example.com/realms/sample-realm',
    clientId: 'sample-frontend',
    redirectUri: `${window.location.origin}/callback`,
    postLogoutRedirectUri: window.location.origin,
    scope: 'openid profile email organization',
    organizationClaim: 'organization'
  }
}, runtimeEnv);

