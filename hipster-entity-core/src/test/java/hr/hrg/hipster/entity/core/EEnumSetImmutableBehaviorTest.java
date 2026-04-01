package hr.hrg.hipster.entity.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EEnumSetImmutableBehaviorTest {

    @Test
    void immutable64HasAnyAndHasAllAgainstBuilderAndImmutable() {
        EEnumSetBuilder<EnumTestUtil.Enum64> leftBuilder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        leftBuilder.mark(EnumTestUtil.Enum64.E01);
        leftBuilder.mark(EnumTestUtil.Enum64.E10);
        EEnumSet<EnumTestUtil.Enum64> left = leftBuilder.toImmutable();

        EEnumSetBuilder<EnumTestUtil.Enum64> subsetBuilder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        subsetBuilder.mark(EnumTestUtil.Enum64.E10);
        EEnumSet<EnumTestUtil.Enum64> subset = subsetBuilder.toImmutable();

        EEnumSetBuilder<EnumTestUtil.Enum64> disjointBuilder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        disjointBuilder.mark(EnumTestUtil.Enum64.E63);
        EEnumSet<EnumTestUtil.Enum64> disjoint = disjointBuilder.toImmutable();

        assertTrue(left.hasAny(subsetBuilder));
        assertTrue(left.hasAny(subset));
        assertTrue(left.hasAll(subsetBuilder));
        assertTrue(left.hasAll(subset));

        assertFalse(left.hasAny(disjointBuilder));
        assertFalse(left.hasAny(disjoint));
        assertFalse(left.hasAll(disjointBuilder));
        assertFalse(left.hasAll(disjoint));
    }

    @Test
    void immutableLargeHasAnyAndHasAllAcrossSegments() {
        EEnumSetBuilder<EnumTestUtil.Enum65> leftBuilder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);
        leftBuilder.mark(EnumTestUtil.Enum65.E00);
        leftBuilder.mark(EnumTestUtil.Enum65.E64);
        EEnumSet<EnumTestUtil.Enum65> left = leftBuilder.toImmutable();

        EEnumSetBuilder<EnumTestUtil.Enum65> subsetBuilder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);
        subsetBuilder.mark(EnumTestUtil.Enum65.E64);
        EEnumSet<EnumTestUtil.Enum65> subset = subsetBuilder.toImmutable();

        EEnumSetBuilder<EnumTestUtil.Enum65> disjointBuilder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);
        disjointBuilder.mark(EnumTestUtil.Enum65.E63);
        EEnumSet<EnumTestUtil.Enum65> disjoint = disjointBuilder.toImmutable();

        assertTrue(left.hasAny(subsetBuilder));
        assertTrue(left.hasAny(subset));
        assertTrue(left.hasAll(subsetBuilder));
        assertTrue(left.hasAll(subset));

        assertFalse(left.hasAny(disjointBuilder));
        assertFalse(left.hasAny(disjoint));
        assertFalse(left.hasAll(disjointBuilder));
        assertFalse(left.hasAll(disjoint));
    }

    @Test
    void emptyImmutableSetBasicsAndToBuilder() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        EEnumSet<EnumTestUtil.Enum64> empty = builder.toImmutable();

        assertInstanceOf(EEnumSetEmpty.class, empty);
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
        assertFalse(empty.has(EnumTestUtil.Enum64.E00));
        assertFalse(empty.hasAny(EEnumSetBuilder.create(EnumTestUtil.Enum64.class)));

        EEnumSetBuilder<EnumTestUtil.Enum64> fromEmpty = empty.toBuilder();
        assertTrue(fromEmpty.isEmpty());
        assertTrue(fromEmpty.mark(EnumTestUtil.Enum64.E00));
        assertTrue(fromEmpty.has(EnumTestUtil.Enum64.E00));
    }

    @Test
    void immutable64ForEachReturnsMarkedOrdinalsInAscendingOrder() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        builder.mark(EnumTestUtil.Enum64.E63);
        builder.mark(EnumTestUtil.Enum64.E01);
        builder.mark(EnumTestUtil.Enum64.E10);
        EEnumSet<EnumTestUtil.Enum64> immutable = builder.toImmutable();

        List<EnumTestUtil.Enum64> visited = new ArrayList<>();
        immutable.forEach((value, idx) -> visited.add(value));

        assertEquals(List.of(EnumTestUtil.Enum64.E01, EnumTestUtil.Enum64.E10, EnumTestUtil.Enum64.E63), visited);
        assertEquals(3, immutable.size());
    }

    @Test
    void immutableLargeSegmentMetadataAndHasBounds() {
        EEnumSetBuilder<EnumTestUtil.Enum65> builder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);
        builder.mark(EnumTestUtil.Enum65.E64);
        EEnumSet<EnumTestUtil.Enum65> immutable = builder.toImmutable();

        assertEquals(2, immutable.getSegmentCount());
        assertTrue(immutable.has(EnumTestUtil.Enum65.E64));
        assertFalse(immutable.has(-1));
        assertFalse(immutable.has(65));
    }

    @Test
    void immutableToBuilderRoundTripWorksForLargeSet() {
        EEnumSetBuilder<EnumTestUtil.Enum65> builder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);
        builder.mark(EnumTestUtil.Enum65.E00);
        builder.mark(EnumTestUtil.Enum65.E33);
        builder.mark(EnumTestUtil.Enum65.E64);

        EEnumSet<EnumTestUtil.Enum65> immutable = builder.toImmutable();
        EEnumSetBuilder<EnumTestUtil.Enum65> roundTrip = immutable.toBuilder();

        assertTrue(roundTrip.has(EnumTestUtil.Enum65.E00));
        assertTrue(roundTrip.has(EnumTestUtil.Enum65.E33));
        assertTrue(roundTrip.has(EnumTestUtil.Enum65.E64));
        assertEquals(3, roundTrip.size());
    }

    @Test
    void immutable64SnapshotIsNotAffectedByFurtherBuilderMutations() {
        EEnumSetBuilder<EnumTestUtil.Enum64> builder = EEnumSetBuilder.create(EnumTestUtil.Enum64.class);
        builder.mark(EnumTestUtil.Enum64.E01);

        EEnumSet<EnumTestUtil.Enum64> snapshot = builder.toImmutable();

        builder.mark(EnumTestUtil.Enum64.E02);
        builder.unmark(EnumTestUtil.Enum64.E01);

        assertTrue(snapshot.has(EnumTestUtil.Enum64.E01));
        assertFalse(snapshot.has(EnumTestUtil.Enum64.E02));
        assertEquals(1, snapshot.size());
    }

    @Test
    void immutableLargeSnapshotIsNotAffectedByFurtherBuilderMutations() {
        EEnumSetBuilder<EnumTestUtil.Enum65> builder = EEnumSetBuilder.create(EnumTestUtil.Enum65.class);
        builder.mark(EnumTestUtil.Enum65.E64);

        EEnumSet<EnumTestUtil.Enum65> snapshot = builder.toImmutable();

        builder.unmark(EnumTestUtil.Enum65.E64);
        builder.mark(EnumTestUtil.Enum65.E00);

        assertTrue(snapshot.has(EnumTestUtil.Enum65.E64));
        assertFalse(snapshot.has(EnumTestUtil.Enum65.E00));
        assertEquals(1, snapshot.size());
    }
}
