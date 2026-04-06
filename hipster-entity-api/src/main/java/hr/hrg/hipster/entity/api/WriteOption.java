package hr.hrg.hipster.entity.api;

public enum WriteOption {
    /**
     * Default write option, which is determined by the code generator based on the presence of other options and the type of view.
     */
    DEFAULT,
    /**
     * Do not generate any code for writing operations.
     */
    NONE,
    /**
     * Generate an interface for writing operations on DTOs and Forms witohut need to materialize them as records or builders. 
     * This genrates less classes to compile, but also the least performant, as it relies on dynamic proxies and reflection for writing values.
     * It is in most cases compltely sufficient for DTOs and Forms, which are not used in performance critical code, but it is not recommended for entity views, which are often used in performance critical code.
     */
    INTERFACE,
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
     * Generate both regular builder and tracking builder classes for writing operations. This is the most flexible option, as it allows to choose between 
     * regular builder and tracking builder at runtime. But it creates two builder classes, so use where erally needed and using tracking builder
     * in tracking scenario and non-tracking scenario and there is strong performance requirement (very rarely). 
     */
    BUILDER_BOTH,
}
