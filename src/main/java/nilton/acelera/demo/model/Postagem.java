package nilton.acelera.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



@Entity
@Table(name = "postagens") 
public class Postagem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100) 
    private String titulo;
    
    @Column(nullable = false, length = 1000)
    private String texto;
    
    @Column(name = "data_criacao")
    private LocalDateTime data = LocalDateTime.now(); 
    
    @ManyToOne
   @JsonIgnoreProperties("postagens") // adicionado para o front
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    
    @JoinColumn(name = "tema_id", nullable = false)
    private Tema tema;

    public Postagem() {

    } 

    public Postagem(String titulo, String texto, Usuario usuario, Tema tema) {
        this.titulo = titulo;
        this.texto = texto;
        this.usuario = usuario;
        this.tema = tema;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setTema(Tema tema) {
        this.tema = tema;
    }
    public Tema getTema() {
        return tema;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }
    public LocalDateTime getData() {
        return data;
    }
}
