package tetris.engine;

public class ThreadLoop extends Thread {
    private final Runnable tarefa;
    private volatile boolean rodando = true;
    private volatile int delay = 200; // Velocidade inicial mais rápida (200ms)

    public ThreadLoop(Runnable tarefa) {
        this.tarefa = tarefa;
    }

    @Override
    public void run() {
        while (rodando) {
            long inicio = System.currentTimeMillis();
            tarefa.run();
            long tempoDecorrido = System.currentTimeMillis() - inicio;
            long sleepTime = Math.max(1, delay - tempoDecorrido); // Compensa tempo de execução
            try { 
                Thread.sleep(sleepTime); 
            } catch (InterruptedException e) { 
                break; 
            }
        }
    }

    public void parar() { rodando = false; }
    public void setDelay(int delay) { this.delay = delay; }
    public int getDelay() { return delay; }
}
