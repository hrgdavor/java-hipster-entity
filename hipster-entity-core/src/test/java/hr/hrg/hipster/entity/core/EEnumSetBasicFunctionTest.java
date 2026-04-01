package hr.hrg.hipster.entity.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EEnumSetBasicFunctionTest {

    @Test
    void builder64MarkUnmarkAndClearUpdateState() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);

        assertTrue(builder.isEmpty());
        assertEquals(0, builder.size());

        assertTrue(builder.mark(EnumTestUtil.Enum64.E00));
        assertTrue(builder.mark(EnumTestUtil.Enum64.E63));
        assertFalse(builder.mark(EnumTestUtil.Enum64.E63));

        assertTrue(builder.has(EnumTestUtil.Enum64.E00));
        assertTrue(builder.has(EnumTestUtil.Enum64.E63));
        assertEquals(2, builder.size());

        assertTrue(builder.unmark(EnumTestUtil.Enum64.E00));
        assertFalse(builder.unmark(EnumTestUtil.Enum64.E00));
        assertEquals(1, builder.size());

        builder.clear();
        assertTrue(builder.isEmpty());
        assertEquals(0, builder.size());
    }

    @Test
    void builderLargeMarksAcrossTwoSegments() {
        EEnumSetBuilder<EnumTestUtil.Enum65> builder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);

        assertTrue(builder.mark(EnumTestUtil.Enum65.E00));
        assertTrue(builder.mark(EnumTestUtil.Enum65.E64));

        assertTrue(builder.has(EnumTestUtil.Enum65.E00));
        assertTrue(builder.has(EnumTestUtil.Enum65.E64));
        assertEquals(2, builder.size());

        assertTrue(builder.unmark(EnumTestUtil.Enum65.E64));
        assertFalse(builder.has(EnumTestUtil.Enum65.E64));
        assertEquals(1, builder.size());
    }

    @Test
    void toImmutableReturnsEmptySetWhenNothingMarked() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);

        EEnumSet<EnumTestUtil.Enum64> immutable = builder.toImmutable();

        assertInstanceOf(EEnumSetEmpty.class, immutable);
        assertTrue(immutable.isEmpty());
    }

    @Test
    void toImmutableAndBackToBuilderPreservesBitsFor64() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        builder.mark(EnumTestUtil.Enum64.E01);
        builder.mark(EnumTestUtil.Enum64.E63);

        EEnumSet<EnumTestUtil.Enum64> immutable = builder.toImmutable();
        EEnumSetBuilder<EnumTestUtil.Enum64> roundTrip = immutable.toBuilder();

        assertTrue(roundTrip.has(EnumTestUtil.Enum64.E01));
        assertTrue(roundTrip.has(EnumTestUtil.Enum64.E63));
        assertEquals(2, roundTrip.size());
    }

    @Test
    void toImmutableAndBackToBuilderPreservesBitsForLarge() {
        EEnumSetBuilder<EnumTestUtil.Enum65> builder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);
        builder.mark(EnumTestUtil.Enum65.E02);
        builder.mark(EnumTestUtil.Enum65.E64);

        EEnumSet<EnumTestUtil.Enum65> immutable = builder.toImmutable();
        EEnumSetBuilder<EnumTestUtil.Enum65> roundTrip = immutable.toBuilder();

        assertTrue(roundTrip.has(EnumTestUtil.Enum65.E02));
        assertTrue(roundTrip.has(EnumTestUtil.Enum65.E64));
        assertEquals(2, roundTrip.size());
    }

    @Test
    void hasAnyAndHasAllWorkForOverlappingSets() {
        EEnumSetBuilder<EnumTestUtil.Enum64> left = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        left.mark(EnumTestUtil.Enum64.E01);
        left.mark(EnumTestUtil.Enum64.E02);

        EEnumSetBuilder<EnumTestUtil.Enum64> overlap = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        overlap.mark(EnumTestUtil.Enum64.E02);

        EEnumSetBuilder<EnumTestUtil.Enum64> disjoint = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        disjoint.mark(EnumTestUtil.Enum64.E10);

        assertTrue(left.hasAny(overlap));
        assertTrue(left.hasAll(overlap));

        assertFalse(left.hasAny(disjoint));
        assertFalse(left.hasAll(disjoint));
    }

    @Test
    void forEachEnumeratesMarkedValuesInOrdinalOrderForBuilder64() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        builder.mark(EnumTestUtil.Enum64.E10);
        builder.mark(EnumTestUtil.Enum64.E00);
        builder.mark(EnumTestUtil.Enum64.E03);

        List<EnumTestUtil.Enum64> visited = new ArrayList<>();
        builder.forEach((value, index) -> visited.add(value));

        assertEquals(List.of(EnumTestUtil.Enum64.E00, EnumTestUtil.Enum64.E03, EnumTestUtil.Enum64.E10), visited);
        assertEquals(visited.size(), builder.size());
    }

    @Test
    void hasReturnsFalseForNegativeAndOutOfRangeOnLarge() {
        EEnumSetBuilder<EnumTestUtil.Enum65> builder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);

        assertFalse(builder.has(-1));
        assertFalse(builder.has(65));
        assertFalse(builder.has(999));
    }

    @Test
    void setOrdinalUsesCalculatedBooleanValue() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);

        boolean shouldSet = 5 > 3;
        assertTrue(builder.set(EnumTestUtil.Enum64.E05.ordinal(), shouldSet));
        assertTrue(builder.has(EnumTestUtil.Enum64.E05));

        boolean shouldUnset = 2 > 7;
        assertTrue(builder.set(EnumTestUtil.Enum64.E05.ordinal(), shouldUnset));
        assertFalse(builder.has(EnumTestUtil.Enum64.E05));
    }

    @Test
    void setWithEnumOverloadSupportsNoOpsAndNull() {
        EEnumSetBuilder<EnumTestUtil.Enum65> builder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);

        assertTrue(builder.set(EnumTestUtil.Enum65.E64, true));
        assertFalse(builder.set(EnumTestUtil.Enum65.E64, true));

        assertTrue(builder.set(EnumTestUtil.Enum65.E64, false));
        assertFalse(builder.set(EnumTestUtil.Enum65.E64, false));

        assertFalse(builder.set((EnumTestUtil.Enum65) null, true));
        assertFalse(builder.set((EnumTestUtil.Enum65) null, false));
    }
}
