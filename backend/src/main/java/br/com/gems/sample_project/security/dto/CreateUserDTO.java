package br.com.gems.sample_project.security.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record CreateUserDTO(String username, String email, String firstName, String lastName, String password, Map<String, List<String>> attributes) {
}
