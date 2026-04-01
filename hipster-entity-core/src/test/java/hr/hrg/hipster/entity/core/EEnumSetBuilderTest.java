package hr.hrg.hipster.entity.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EEnumSetBuilderTest {

    @Test
    void uses64BuilderFor64ValueEnum() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        assertTrue(builder instanceof EEnumSetBuilder64);

        assertTrue(builder.mark(EnumTestUtil.Enum64.E00));
        assertTrue(builder.mark(EnumTestUtil.Enum64.E63));
        assertFalse(builder.mark(EnumTestUtil.Enum64.E63));

        EEnumSet<EnumTestUtil.Enum64> immutable = builder.toImmutable();
        assertTrue(immutable.has(EnumTestUtil.Enum64.E00));
        assertTrue(immutable.has(EnumTestUtil.Enum64.E63));
        assertEquals(2, immutable.size());
    }

    @Test
    void usesLargeBuilderFor65ValueEnum() {
        EEnumSetBuilder<EnumTestUtil.Enum65> builder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);
        assertTrue(builder instanceof EEnumSetBuilderLarge);

        assertTrue(builder.mark(EnumTestUtil.Enum65.E64));
        EEnumSet<EnumTestUtil.Enum65> immutable = builder.toImmutable();
        assertTrue(immutable.has(EnumTestUtil.Enum65.E64));
        assertEquals(1, immutable.size());
    }
}
