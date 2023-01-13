package desafio.tecnico.entity;

import javax.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Producer extends PanacheEntity {

    private String nameProducer;

    public Producer() {
    }

    public Producer(String nameProducer) {
        this.nameProducer = nameProducer;
    }

    public String getNameProducer() {
        return this.nameProducer;
    }

    public void setNameProducer(String nameProducer) {
        this.nameProducer = nameProducer;
    }
}