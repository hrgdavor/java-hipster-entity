package hr.hrg.hipster.entity.person;

import java.util.List;
import java.util.Map;

public enum PersonSummaryField {
    id(Long.class),
    firstName(String.class),
    lastName(String.class),
    age(Integer.class),
    departmentName(String.class),
    metadata(Map.class);

    private final Class<?> javaType;
    private final String methodName;

    PersonSummaryField(Class<?> javaType) {
        this.javaType = javaType;
        this.methodName = name();
    }

    public Class<?> javaType() {
        return javaType;
    }

    public String methodName() {
        return methodName;
    }

    public static PersonSummaryField forMethodName(String methodName) {
        if (methodName == null) {
            return null;
        }
        return switch (methodName) {
            case "id" -> id;
            case "firstName" -> firstName;
            case "lastName" -> lastName;
            case "age" -> age;
            case "departmentName" -> departmentName;
            case "metadata" -> metadata;
            default -> null;
        };
    }

    @SuppressWarnings("unchecked")
    public static Map<String, List<Long>> castMetadata(Object value) {
        return (Map<String, List<Long>>) value;
    }
}
