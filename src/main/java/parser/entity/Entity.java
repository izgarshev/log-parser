package parser.entity;

public class Entity {

    private String typeId;
    private long id;
    private int versionId;

    public Entity() {
    }

    public Entity(String typeId, long id, int versionId) {
        this.typeId = typeId;
        this.id = id;
        this.versionId = versionId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getTypeId() {
        return typeId;
    }

    public long getId() {
        return id;
    }

    public int getVersionId() {
        return versionId;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "typeId='" + typeId + '\'' +
                ", id=" + id +
                ", versionId=" + versionId +
                '}';
    }
}
