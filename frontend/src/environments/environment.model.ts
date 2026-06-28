export interface RuntimeEnvironment {
  production: boolean;
  apiBaseUrl: string;
  tenantHeaderName: string;
  defaultTenant: string;
  oidc: {
    issuer: string;
    clientId: string;
    redirectUri: string;
    postLogoutRedirectUri: string;
    scope: string;
    organizationClaim: string;
  };
}

export type RuntimeEnvironmentOverrides = Partial<
  Omit<RuntimeEnvironment, 'oidc'> & {
    oidc: Partial<RuntimeEnvironment['oidc']>;
  }
>;

export function buildEnvironment(
  defaults: RuntimeEnvironment,
  runtimeOverrides: RuntimeEnvironmentOverrides = {}
): RuntimeEnvironment {
  return {
    ...defaults,
    ...runtimeOverrides,
    oidc: {
      ...defaults.oidc,
      ...runtimeOverrides.oidc
    }
  };
}

