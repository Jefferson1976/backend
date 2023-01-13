package desafio.tecnico.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class PiorFilme extends PanacheEntity {

	private String title;
	private Integer yearMovie;
    private Boolean winner;

	@OneToMany
	@Cascade(CascadeType.ALL)
    private List<Studio> studios;

	@OneToMany
	@Cascade(CascadeType.ALL)
    private List<Producer> producers;

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getYearMovie() {
		return this.yearMovie;
	}

	public void setYear(Integer yearMovie) {
		this.yearMovie = yearMovie;
	}

	public boolean isWinner() {
		return this.winner;
	}

	public boolean getWinner() {
		return this.winner;
	}

	public void setWinner(boolean winner) {
		this.winner = winner;
	}

	public List<Studio> getStudios() {
		return this.studios;
	}

	public void setStudios(List<Studio> studios) {
		this.studios = studios;
	}

	public List<Producer> getProducers() {
		return this.producers;
	}

	public void setProducers(List<Producer> producers) {
		this.producers = producers;
	}
}