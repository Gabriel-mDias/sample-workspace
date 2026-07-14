package br.com.gems.sample_project.security.dto;

import org.springframework.http.HttpMethod;

public record PublicRoute(HttpMethod method, String pattern) {}
