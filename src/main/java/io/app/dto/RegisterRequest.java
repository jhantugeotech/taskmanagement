package io.app.dto;

import jakarta.validation.constraints.Email;

public record RegisterRequest(@Email String email, String password) {
}
