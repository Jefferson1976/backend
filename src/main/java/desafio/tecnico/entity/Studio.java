package desafio.tecnico.entity;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Studio extends PanacheEntity {

    private String nameStudio;

    public Studio() {
    }

    public Studio(String nameStudio) {
        this.nameStudio = nameStudio;
    }

    public String getNameStudio() {
        return this.nameStudio;
    }

    public void setNameStudio(String nameStudio) {
        this.nameStudio = nameStudio;
    }
}