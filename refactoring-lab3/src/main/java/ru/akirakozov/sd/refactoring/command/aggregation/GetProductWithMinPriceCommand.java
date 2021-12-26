package ru.akirakozov.sd.refactoring.command.aggregation;

public class GetProductWithMinPriceCommand extends SingleProductCommand {
    public GetProductWithMinPriceCommand() {
        super("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1");
    }
}
