package hr.hrg.hipster.entity.tooling.meta;

import java.util.List;

public class EntityMeta {
    public final String entityName;
    public final String packageName;
    public final String markerInterface;
    public final String idType;
    public final List<ViewMeta> views;
    public final List<EntityFieldMeta> allFields;

    public EntityMeta(String entityName, String packageName, String markerInterface, String idType, List<ViewMeta> views, List<EntityFieldMeta> allFields) {
        this.entityName = entityName;
        this.packageName = packageName;
        this.markerInterface = markerInterface;
        this.idType = idType;
        this.views = views;
        this.allFields = allFields;
    }

    public String getEntityName() { return entityName; }
    public String getPackageName() { return packageName; }
    public String getMarkerInterface() { return markerInterface; }
    public String getIdType() { return idType; }
    public List<ViewMeta> getViews() { return views; }
    public List<EntityFieldMeta> getAllFields() { return allFields; }
}
