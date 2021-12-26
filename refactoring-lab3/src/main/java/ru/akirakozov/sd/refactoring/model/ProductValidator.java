package ru.akirakozov.sd.refactoring.model;

import ru.akirakozov.sd.refactoring.exceptions.BadModelException;

public class ProductValidator {
    public static void validate(Product p) throws BadModelException {
        if (p == null) {
            throw new BadModelException("Product cannot be null");
        }
        if (p.price <= 0) {
            throw new BadModelException("Product price should be positive");
        }
        if (p.name == null || p.name.equals("")) {
            throw new BadModelException("Product name should be not empty string");
        }
    }
}
