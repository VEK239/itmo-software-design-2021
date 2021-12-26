package ru.akirakozov.sd.refactoring.servlet.html;

public class HtmlBuilder {

    private final StringBuilder SB = new StringBuilder();

    public HtmlBuilder() {
        SB.append("<html>")
                .append("<head>")
                .append("<meta charset=\"UTF-8\" />")
                .append("</head>")
                .append("<body>");
    }

    @Override
    public String toString() {
        return SB.toString() + "</body></html>";
    }

    public HtmlBuilder addHeader(String header) {
        SB.append("<h2>").append(header).append("</h2>");
        return this;
    }

    public HtmlBuilder addLine(String value) {
        SB.append("<div>").append(value).append("</div>");
        return this;
    }

    public HtmlBuilder addTag(String value) {
        SB.append(value);
        return this;
    }
}