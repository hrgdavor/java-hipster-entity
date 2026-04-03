package hr.hrg.hipster.entity.tooling;

import java.util.Objects;

public class Property {
    public final String name;
    public final String type;
    public final String fieldKind;
    public final String column;
    public final String relation;
    public final String expression;
    public final int lineNumber;

    public Property(String name, String type, String fieldKind, String column, String relation, String expression, int lineNumber) {
        this.name = name;
        this.type = type;
        this.fieldKind = fieldKind;
        this.column = column;
        this.relation = relation;
        this.expression = expression;
        this.lineNumber = lineNumber;
    }

    public Property(String name, String type) {
        this(name, type, null, null, null, null, -1);
    }

    public Property(String name, String type, int lineNumber) {
        this(name, type, null, null, null, null, lineNumber);
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getFieldKind() { return fieldKind; }
    public String getColumn() { return column; }
    public String getRelation() { return relation; }
    public String getExpression() { return expression; }
    public int getLineNumber() { return lineNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Property)) return false;
        Property property = (Property) o;
        return lineNumber == property.lineNumber && Objects.equals(name, property.name) && Objects.equals(type, property.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, lineNumber);
    }
}
