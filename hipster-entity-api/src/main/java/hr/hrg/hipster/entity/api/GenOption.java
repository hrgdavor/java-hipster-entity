package hr.hrg.hipster.entity.api;

public enum GenOption {
    /**
     * Default write option, which is determined by the code generator based on the presence of other options and the type of view.
     */
    DEFAULT,
    /**
     * Do not generate any code for write/read operations.
     */
    MINIMAL,
    /**
     * Generate read materialization record class. This is the most concise option, and also more performant than proxy via interface.
     */
    READ_RECORD,
    /**
     * Generate an interface for writing operations on DTOs and Forms without need to materialize them as records or builders. 
     * This generates less classes to compile, but also the least performant, as it relies on dynamic proxies and reflection for writing values.
     * It is in most cases completely sufficient for DTOs and Forms, which are not used in performance critical code, but it is not recommended for entity views, which are often used in performance critical code.
     */
    WRITE_INTERFACE,
    /**
     * Generate a builder class for writing operations. This is the most performant option, as it generates a concrete class 
     * with mutable fields and setter methods for writing values. It is recommended for use in performance critical code, 
     * but it can be overkill for simple DTOs and Forms.
     */
    BUILDER,
    /**
     * Generate a builder class for writing operations with tracking capabilities. This is the most feature-rich option, as it generates a concrete class with mutable fields, 
     * setter methods for writing values, and tracking of modified fields. It is used for implementing partial updates and patch operations, where it is important to know which fields were modified by the user. 
     * 
     */
    BUILDER_TRACKING,
    /**
     * Rarely needed, only in cases of strong performance requirement (very rarely). Generate both regular builder and tracking builder classes for writing operations. 
     * This option is available as it is simple to support, but it is not recommended for use in most cases, as it generates more classes to compile and maintain, 
     * and it can be confusing to have both options available at the same time.
     */
    BUILDER_BOTH,
}
