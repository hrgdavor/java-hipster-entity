package hr.hrg.hipster.entity.tooling.meta;

import java.util.List;
import java.util.Objects;

public class ViewMeta {
    public final String name;
    public final List<String> extendsTypes;
    public final Boolean read;
    public final Boolean write;
    public final List<Property> properties;
    public final int lineNumber;

    public ViewMeta(String name, List<String> extendsTypes, Boolean read, Boolean write, List<Property> properties, int lineNumber) {
        this.name = name;
        this.extendsTypes = extendsTypes;
        this.read = read;
        this.write = write;
        this.properties = properties;
        this.lineNumber = lineNumber;
    }

    public String getName() { return name; }
    public List<String> getExtendsTypes() { return extendsTypes; }
    public Boolean getRead() { return read; }
    public Boolean getWrite() { return write; }
    public List<Property> getProperties() { return properties; }
    public int getLineNumber() { return lineNumber; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViewMeta)) return false;
        ViewMeta viewMeta = (ViewMeta) o;
        return lineNumber == viewMeta.lineNumber && Objects.equals(name, viewMeta.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, lineNumber);
    }
}
