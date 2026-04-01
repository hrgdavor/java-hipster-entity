package hr.hrg.hipster.entity.tooling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class EntityMetadataGeneratorTest {

    @Test
    public void generateMetadataJsonForEntityPackage() throws Exception {
        Path sourceRoot = Files.createTempDirectory("entity-metadata-source");
        Path outputRoot = Files.createTempDirectory("entity-metadata-output");

        Path personEntityFile = sourceRoot.resolve("PersonEntity.java");
        Path personSummaryFile = sourceRoot.resolve("PersonSummary.java");
        Path personDtoFile = sourceRoot.resolve("PersonDto.java");

        String entitySource = "package hr.hrg.hipster.entity.person;\n" +
                "import hr.hrg.hipster.entity.api.EntityBase;\n" +
                "public interface PersonEntity extends EntityBase<Long> {}\n";

        String summarySource = "package hr.hrg.hipster.entity.person;\n" +
                "import hr.hrg.hipster.entity.api.View;\n" +
                "import hr.hrg.hipster.entity.api.BooleanOption;\n" +
                "import hr.hrg.hipster.entity.api.FieldSource;\n" +
                "import hr.hrg.hipster.entity.api.FieldKind;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)\n" +
                "public interface PersonSummary extends PersonEntity {\n" +
                "  String firstName();\n" +
                "  String lastName();\n" +
                "  @FieldSource(kind = FieldKind.DERIVED, expression = \"YEAR(NOW()) - YEAR(birthDate)\")\n" +
                "  Integer age();\n" +
                "  @FieldSource(kind = FieldKind.JOINED, relation = \"department.name\")\n" +
                "  String departmentName();\n" +
                "  Map<String, List<Long>> metadata();\n" +
                "}\n";

        // PersonDto: same field name 'age' but as COLUMN (non-derived) with same type,
        // and 'firstName' with different type String vs in summary
        String dtoSource = "package hr.hrg.hipster.entity.person;\n" +
                "import hr.hrg.hipster.entity.api.View;\n" +
                "import hr.hrg.hipster.entity.api.BooleanOption;\n" +
                "@View(read = BooleanOption.TRUE, write = BooleanOption.FALSE)\n" +
                "public interface PersonDto extends PersonEntity {\n" +
                "  String firstName();\n" +
                "  Integer age();\n" +
                "}\n";

        Files.writeString(personEntityFile, entitySource);
        Files.writeString(personSummaryFile, summarySource);
        Files.writeString(personDtoFile, dtoSource);

        EntityMetadataGenerator.generate(sourceRoot, outputRoot);

        Path metadataFile = outputRoot.resolve("Person.metadata.json");
        Assertions.assertTrue(Files.exists(metadataFile), "Metadata file should be generated");

        String json = Files.readString(metadataFile);

        // Entity basics
        Assertions.assertTrue(json.contains("\"entityName\": \"Person\""));
        Assertions.assertTrue(json.contains("\"markerInterface\": \"PersonEntity\""));
        Assertions.assertTrue(json.contains("\"idType\""));
        Assertions.assertTrue(json.contains("java.lang.Long"));

        // View properties
        Assertions.assertTrue(json.contains("\"name\": \"PersonSummary\""));
        Assertions.assertTrue(json.contains("\"firstName\""));
        Assertions.assertTrue(json.contains("\"lastName\""));
        Assertions.assertTrue(json.contains("\"name\": \"metadata\""));
        Assertions.assertTrue(json.contains("\"type\": \"java.util.Map\""));
        Assertions.assertTrue(json.contains("\"genericArguments\""));

        // Primitive/boxed handling
        Assertions.assertTrue(json.contains("\"name\": \"age\""));
        Assertions.assertTrue(json.contains("\"type\": \"java.lang.Integer\""));
        Assertions.assertTrue(json.contains("\"unboxed\": \"int\""));
        Assertions.assertTrue(json.contains("\"primitive\": true"));

        // @FieldSource on age → DERIVED
        Assertions.assertTrue(json.contains("\"fieldKind\": \"DERIVED\""));
        Assertions.assertTrue(json.contains("\"expression\": \"YEAR(NOW()) - YEAR(birthDate)\""));

        // @FieldSource on departmentName → JOINED
        Assertions.assertTrue(json.contains("\"fieldKind\": \"JOINED\""));
        Assertions.assertTrue(json.contains("\"relation\": \"department.name\""));

        // Entity-wide allFields section
        Assertions.assertTrue(json.contains("\"allFields\""));

        // allFields: id is COLUMN and present in both views
        Assertions.assertTrue(json.contains("\"fieldKind\": \"COLUMN\""));

        // allFields: typeByView tracking
        Assertions.assertTrue(json.contains("\"typeByView\""));

        // 'age' field: first seen as DERIVED in PersonSummary, then as COLUMN in PersonDto
        // Non-derived (COLUMN) should win as the primary fieldKind for 'age'
        int allFieldsStart = json.indexOf("\"allFields\"");
        int ageAllFieldsPos = json.indexOf("\"name\": \"age\"", allFieldsStart);
        Assertions.assertTrue(ageAllFieldsPos > 0, "age should appear in allFields");
        // Extract from age entry to the next allFields entry (or end)
        int ageEntryEnd = json.indexOf("\n    }", ageAllFieldsPos);
        String ageSection = json.substring(ageAllFieldsPos, ageEntryEnd);
        Assertions.assertTrue(ageSection.contains("\"fieldKind\": \"COLUMN\""), "age primary fieldKind should be COLUMN (non-derived wins): " + ageSection);

        // typeByView should show both views for 'age'
        Assertions.assertTrue(json.contains("\"PersonSummary\":"), "typeByView should contain PersonSummary");
        Assertions.assertTrue(json.contains("\"PersonDto\":"), "typeByView should contain PersonDto");

        // 'firstName' appears in both views with same type
        int fnAllFieldsPos = json.indexOf("\"name\": \"firstName\"", json.indexOf("\"allFields\""));
        Assertions.assertTrue(fnAllFieldsPos > 0, "firstName should appear in allFields");

        // Enum generation
        Path enumFile = outputRoot.resolve("hr/hrg/hipster/entity/person/PersonSummaryProperty.java");
        Assertions.assertTrue(Files.exists(enumFile), "View property enum should be generated");

        String enumSource = Files.readString(enumFile);
        Assertions.assertTrue(enumSource.contains("enum PersonSummaryProperty"));
        Assertions.assertTrue(enumSource.contains("id(java.lang.Long.class)"));
        Assertions.assertTrue(enumSource.contains("firstName(java.lang.String.class)"));
        Assertions.assertTrue(enumSource.contains("lastName(java.lang.String.class)"));
        Assertions.assertTrue(enumSource.contains("age(java.lang.Integer.class)"));
        Assertions.assertTrue(enumSource.contains("departmentName(java.lang.String.class)"));
    }
}
