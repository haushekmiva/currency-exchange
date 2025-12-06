package models;

public record Currency(
        int id,
        String fullName,
        String code,
        String sign
) {}
