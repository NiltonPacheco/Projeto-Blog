package nilton.acelera.demo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table (name = "Temas")
public class Tema {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    @OneToMany(mappedBy = "tema", cascade = CascadeType.ALL)
    private List<Postagem> postagens;
    public Tema(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setPostagens(List<Postagem> postagens) {
        this.postagens = postagens;
    }
    public List<Postagem> getPostagens() {
        return postagens;
    }
}
