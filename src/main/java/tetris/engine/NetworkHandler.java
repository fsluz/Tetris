package tetris.engine;

import java.io.*;
import java.net.*;

public class NetworkHandler {
    private ServerSocket servidor;
    private Socket cliente;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void iniciarServidor(int porta) {
        try {
            servidor = new ServerSocket(porta);
            System.out.println("[NET] Servidor na porta " + porta);
            cliente = servidor.accept();
            out = new ObjectOutputStream(cliente.getOutputStream());
            in = new ObjectInputStream(cliente.getInputStream());
            System.out.println("[NET] Cliente conectado");
        } catch (IOException e) {
            System.err.println("[NET] Erro servidor: " + e.getMessage());
        }
    }

    public void conectarComoCliente(String host, int porta) {
        try {
            cliente = new Socket(host, porta);
            out = new ObjectOutputStream(cliente.getOutputStream());
            in = new ObjectInputStream(cliente.getInputStream());
            System.out.println("[NET] Conectado a " + host + ":" + porta);
        } catch (IOException e) {
            System.err.println("[NET] Falha: " + e.getMessage());
        }
    }

    public void enviar(Object obj) {
        try {
            if (out != null) {
                out.writeObject(obj);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("[NET] Erro ao enviar: " + e.getMessage());
        }
    }

    public Object receber() {
        try {
            if (in != null) return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[NET] Erro ao receber: " + e.getMessage());
        }
        return null;
    }

    public void fechar() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (cliente != null) cliente.close();
            if (servidor != null) servidor.close();
            System.out.println("[NET] Conexão encerrada");
        } catch (IOException e) {
            System.err.println("[NET] Erro ao fechar: " + e.getMessage());
        }
    }
}
