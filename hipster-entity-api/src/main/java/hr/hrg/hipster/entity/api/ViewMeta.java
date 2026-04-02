package hr.hrg.hipster.entity.api;

/**
 * Metadata and factory contract for a single array-backed entity view.
 *
 * <p>Each view interface (e.g. {@code PersonSummary}) is paired with one concrete
 * implementation of this interface (e.g. {@code PersonSummaryMeta}), typically a singleton.
 * It captures everything needed to register, serialize, deserialize, and construct the view:</p>
 * <ul>
 *   <li>{@link #viewType()} — the view interface class</li>
 *   <li>{@link #fieldType()} — the companion field enum class (implements {@link FieldDef})</li>
 *   <li>{@link #forName(String)} — reverse lookup from field name to enum constant</li>
 *   <li>{@link #create(Object[])} — constructs the view from a positional values array</li>
 * </ul>
 *
 * <h3>Naming contract (inherited from {@link FieldDef})</h3>
 * <p>Field names are {@code enum.name()} — identical to the accessor method name on the view
 * interface. No mapping exists. {@link #forName(String)} is the inverse of {@code enum.name()}.</p>
 *
 * @param <V> the view interface type
 * @param <F> the companion field enum type, must implement {@link FieldDef}
 */
public interface ViewMeta<V, F extends Enum<F> & FieldDef> {

    /** The view interface class. */
    Class<V> viewType();

    /** The companion field enum class; {@code fieldType().getEnumConstants()[i].ordinal() == i}. */
    Class<F> fieldType();

    /** Number of fields in the view. */
    int fieldCount();

    /** Field value enum constants in ordinal order. */
    F[] fieldValues();

    /** Field name for given ordinal (same as enum.name()). */
    String fieldNameAt(int ordinal);

    /** Field Java type for given ordinal. */
    Class<?> fieldTypeAt(int ordinal);

    /**
     * Reverse lookup: field name → enum constant, or {@code null} if not found.
     * Equivalent to {@code Enum.valueOf} but null-safe and without throwing on unknown names.
     *
     * @param name the field name (== {@code enum.name()})
     * @return the matching constant, or {@code null}
     */
    F forName(String name);

    /**
     * Constructs the view from a positional values array where {@code values[f.ordinal()]}
     * holds the value for field {@code f}.
     *
     * @param values positional array indexed by field ordinal
     * @return the constructed view instance
     */
    V create(Object[] values);
}
