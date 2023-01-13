package desafio.tecnico.dto;

public class ErroDTO {
    private String erro;

    public ErroDTO() {
    }

    public ErroDTO(String erro) {
        this.erro = erro;
    }

    public String getErro() {
        return this.erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }
}
