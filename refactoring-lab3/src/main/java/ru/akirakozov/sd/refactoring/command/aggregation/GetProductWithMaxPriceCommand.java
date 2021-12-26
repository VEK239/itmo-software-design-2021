package ru.akirakozov.sd.refactoring.command.aggregation;

public class GetProductWithMaxPriceCommand extends SingleProductCommand {
    public GetProductWithMaxPriceCommand() {
        super("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1");
    }
}
