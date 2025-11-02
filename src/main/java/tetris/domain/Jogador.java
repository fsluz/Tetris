package tetris.domain;

import java.io.Serializable;
import java.util.UUID;

public class Jogador implements Serializable {
    private final UUID id;
    private String nome;

    public Jogador(String nome) {
        this.id = UUID.randomUUID();
        this.nome = nome;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public String toString() {
        return "Jogador{id=" + id + ", nome='" + nome + "'}";
    }
}
