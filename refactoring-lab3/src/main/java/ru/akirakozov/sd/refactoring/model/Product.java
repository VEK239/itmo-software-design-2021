package ru.akirakozov.sd.refactoring.model;

public class Product {
    public int id;
    public String name;
    public int price;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
