package hr.hrg.hipster.entity.tooling;

import hr.hrg.hipster.entity.tooling.EntityFieldMeta;
import hr.hrg.hipster.entity.tooling.EntityMeta;
import hr.hrg.hipster.entity.tooling.Property;
import hr.hrg.hipster.entity.tooling.ViewMeta;
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

        EntityMeta entityMeta = EntityMetadataGenerator.fromJson(json);

        // Entity basics
        Assertions.assertEquals("Person", entityMeta.entityName);
        Assertions.assertEquals("PersonEntity", entityMeta.markerInterface);
        Assertions.assertEquals("Long", entityMeta.idType);

        // View properties
        Assertions.assertNotNull(entityMeta.views);
        Assertions.assertEquals(2, entityMeta.views.size());

        ViewMeta personSummary = null;
        for (ViewMeta view : entityMeta.views) {
            if ("PersonSummary".equals(view.name)) {
                personSummary = view;
                break;
            }
        }
        Assertions.assertNotNull(personSummary, "PersonSummary view should exist");
        Assertions.assertEquals(8, personSummary.lineNumber);

        Property firstNameProp = null;
        for (Property prop : personSummary.properties) {
            if ("firstName".equals(prop.name)) {
                firstNameProp = prop;
                break;
            }
        }
        Assertions.assertNotNull(firstNameProp, "firstName property should exist");
        Assertions.assertTrue(firstNameProp.lineNumber > 0, "firstName lineNumber should be > 0");

        Assertions.assertNotNull(entityMeta.allFields);
        EntityFieldMeta ageField = null;
        EntityFieldMeta firstNameField = null;
        for (EntityFieldMeta field : entityMeta.allFields) {
            if ("age".equals(field.name)) ageField = field;
            if ("firstName".equals(field.name)) firstNameField = field;
        }
        Assertions.assertNotNull(ageField, "age should be in allFields");
        Assertions.assertNotNull(firstNameField, "firstName should be in allFields");
        Assertions.assertEquals("COLUMN", ageField.fieldKind);
        Assertions.assertTrue(firstNameField.lineNumber > 0, "firstName allFields lineNumber should be > 0");

        // allFields: id is COLUMN and present in both views
        Assertions.assertEquals("COLUMN", ageField.fieldKind);

        // allFields: typeByView tracking
        Assertions.assertTrue(ageField.typeByView.containsKey("PersonSummary"));
        Assertions.assertTrue(ageField.typeByView.containsKey("PersonDto"));

        // 'age' field: first seen as DERIVED in PersonSummary, then as COLUMN in PersonDto
        Assertions.assertNotNull(ageField, "age should appear in allFields");
        Assertions.assertEquals("COLUMN", ageField.fieldKind);

        Assertions.assertTrue(ageField.typeByView.containsKey("PersonSummary"));
        Assertions.assertTrue(ageField.typeByView.containsKey("PersonDto"));

        // 'firstName' appears in allFields with a line number
        Assertions.assertTrue(firstNameField.lineNumber > 0);

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
