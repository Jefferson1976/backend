package desafio.tecnico.vo;

import java.util.List;

public class PiorFilmeMinMaxVO {
	private List<ProducerVO> min;
	private List<ProducerVO> max;

	public List<ProducerVO> getMin() {
		return this.min;
	}

	public void setMin(List<ProducerVO> min) {
		this.min = min;
	}

	public List<ProducerVO> getMax() {
		return this.max;
	}

	public void setMax(List<ProducerVO> max) {
		this.max = max;
	}

}
