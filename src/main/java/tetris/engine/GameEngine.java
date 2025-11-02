package tetris.engine;

import tetris.domain.Partida;
import tetris.io.SaveManager;
import tetris.domain.Tetromino;
import tetris.domain.Tabuleiro;

public class GameEngine implements Runnable {
    private final Partida partida;
    private boolean rodando = false;
    private ThreadLoop threadLoop;

    public GameEngine(Partida partida) {
        this.partida = partida;
    }

    public void iniciar() {
        if (!rodando) {
            rodando = true;
            threadLoop = new ThreadLoop(this);
            threadLoop.start();
            System.out.println("[ENGINE] Loop iniciado");
        }
    }

    @Override
    public void run() {
        if (partida.isGameOver()) {
            parar();
            System.out.println("[ENGINE] Game Over!");
            return;
        }
        partida.tick(); // tick() já faz validação de game over, não precisa duplicar
    }

    public void parar() {
        rodando = false;
        if (threadLoop != null) {
            threadLoop.parar();
        }
        SaveManager.salvar(partida);
        System.out.println("[ENGINE] Loop encerrado e partida salva");
    }
    
    public ThreadLoop getThreadLoop() {
        return threadLoop;
    }
}
