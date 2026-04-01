package hr.hrg.hipster.entity.person;

public enum PersonUpdateFormField {
    id(Long.class),
    firstName(String.class),
    lastName(String.class),
    email(String.class),
    phoneNumber(String.class);

    private final Class<?> javaType;
    private final String methodName;

    PersonUpdateFormField(Class<?> javaType) {
        this.javaType = javaType;
        this.methodName = name();
    }

    public Class<?> javaType() {
        return javaType;
    }

    public String methodName() {
        return methodName;
    }

    public static PersonUpdateFormField forMethodName(String methodName) {
        if (methodName == null) {
            return null;
        }
        return switch (methodName) {
            case "id" -> id;
            case "firstName" -> firstName;
            case "lastName" -> lastName;
            case "email" -> email;
            case "phoneNumber" -> phoneNumber;
            default -> null;
        };
    }
}
