package nilton.acelera.demo.dto;

public class UsuarioTokenDTO {

    private Long id;
    private String nome;
    private String usuario;
    private String foto;
    private String token;
    private String tipo; 

    public UsuarioTokenDTO() {
    }
 
    public UsuarioTokenDTO(Long id, String nome, String usuario, String foto, String token,String tipo) {
        this.id = id;
        this.nome = nome;
        this.usuario = usuario;
        this.foto = foto;
        this.token = token;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
