package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPclient {

    public static void main(String[] argv) throws Exception {

        // Configurando a conexão
        String servidorIp = "Localhost";
        int servidorPorta = 6789;

        System.out.println("Conectando ao servidor " + servidorIp + " na porta " + servidorPorta);
        Socket clienteSocket = new Socket(servidorIp, servidorPorta);
        System.out.println("Conectado ao servidor.");

        DataOutputStream outToServer = new DataOutputStream(clienteSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        boolean conectado = true;

        while (conectado) {
            String mensagem = inFromServer.readLine();

            if (mensagem == null) {
                System.out.println("Sem conexao com o servidor. Encerrando cliente.");
                break;
            }

            if (mensagem.contains("[INPUT]")) {

                System.out.print(mensagem.replace("[INPUT]", "") + " ");

                String chute = teclado.readLine();
                outToServer.writeBytes(chute + "\n");
            } else {
                System.out.println(mensagem);
            }
        }

        clienteSocket.close();

    }
}