package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TCPserver {

    private static ArrayList<Socket> sockets = new ArrayList<>();
    private static ArrayList<DataOutputStream> outputs = new ArrayList<>();
    private static ArrayList<BufferedReader> inputs = new ArrayList<>();

    public static void main(String[] args) throws Exception {
    	
    	

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        int porta = 6789;
        ServerSocket servidor = new ServerSocket(porta);

        System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Servidor iniciado na porta " + porta);
        System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Aguardando 3 conexões...");

        // ===== CONEXÃO DOS 3 CLIENTES =====
        while (sockets.size() < 3) {
            Socket socket = servidor.accept();

            sockets.add(socket);
            outputs.add(new DataOutputStream(socket.getOutputStream()));
            inputs.add(new BufferedReader(new InputStreamReader(socket.getInputStream())));

            int idJogador = sockets.size();

            System.out.println("[" + dtf.format(LocalDateTime.now()) + "] Jogador " + idJogador + " conectado.");
            outputs.get(idJogador - 1).writeBytes("Bem-vindo, Jogador " + idJogador + "!\n");
        }

        // aqui vai ser pra quando os clientes confirmarem.
        broadcast("Os 3 jogadores se conectaram.");
        broadcast("Para iniciar o jogo, todos devem confirmar.\n1 - SIM\n2 - NAO");

        boolean confirmar = true;

        for (int i = 0; i < 3; i++) {
        	// aqui é pra mensagem abaixo aparecer para cada cliente/jogador, comecando
        	// do 1 ate o 3.

        	outputs.get(i).writeBytes("Jogador " + (i + 1) + "[INPUT], confirme:\n");
        	// esse [INPUT] serve pra se comunicar com o cliente/jogador, mas isso 
        	// acima seria praticamente um "sysout" e isso abaixo é pra ler a resposta.
            String esc = inputs.get(i).readLine();

            if (esc == null || !esc.equals("1")) {
                confirmar = false;
                break;
                // esse break ta aqui pq se um jogador digitar "2" = "não"
                // ele vai parar o for na mesma hora, pq não faz sentido continuar
                // se um jogador digitar "2" = "não".
            }
        }

        // aqui vai ser pra caso algum usuario digitar 2- "não".
        if (!confirmar) {
            broadcast("Algum jogador nao confirmou. Encerrando o servidor...");
            System.out.println("Jogo cancelado.");
            //o break vai direcionar pra essa parte do codigo.

            for (Socket s : sockets) {
                s.close();
                // aq ele fecha a conexão.
            }
            servidor.close();
            return;
        }

        broadcast("Todos confirmaram! Iniciando sessao de jogo!");
        System.out.println("Jogo iniciado.");

        boolean jogoRodando = true;

        try {
        	 // Loop principal do jogo
            while (jogoRodando) {
                // Codigo do jogo aqui (ex: uma rodada termina e você decide perguntar)

                // Simulação do fim de uma rodada
                
                
                
                
            	ArrayList<Integer> escolhas = new ArrayList<>();

            	for (int i = 0; i < 3; i++) {
            	    outputs.get(i).writeBytes(
            	        "Jogador " + (i + 1) +
            	        " [INPUT] - Escolha um número (0 ou 1):\n"
            	    );

            	    String entrada = inputs.get(i).readLine();

            	    while (entrada == null || (!entrada.equals("0") && !entrada.equals("1"))) {
            	        outputs.get(i).writeBytes(
            	            "Entrada invalida! Digite apenas 0 ou 1:\n"
            	        );
            	        entrada = inputs.get(i).readLine();
            	    }

            	    escolhas.add(Integer.parseInt(entrada));
            	}

            	broadcast("Resultado da rodada:");
            	for (int i = 0; i < escolhas.size(); i++) {
            	    broadcast("Jogador " + (i + 1) + " jogou: " + escolhas.get(i));
            	}

                   
                    int[] jogadas = new int[3];// vetor com a posição de cada jogado do jogador
                    for (int i = 0; i < 3; i++) {// armazena a jogada de cada jogador se foi 0 ou 1
                        jogadas[i] = escolhas.get(i);
                    }

                    int resultado = verificarVencedor(jogadas); // chama o método verificadorVencedor que vai receber um número 

                    if (resultado == -1) { // apois receber o número vai compara para ver se os jogadoes empataram, venceram ou perderam
                        broadcast("EMPATE! Todos escolheram o mesmo valor.");
                        broadcast("O jogo sera reiniciado.\n");
                        continue;// vai pular o resto do código e voltar para while (jogoRodando) para jogar novamente
                        
                    } else {
                        broadcast("Jogador " + (resultado + 1) + " GANHOU!");

                        for (int i = 0; i < 3; i++) {
                            if (i != resultado) {
                                outputs.get(i).writeBytes("Você PERDEU.\n");
                            }
                        }
                        // sai do while (jogoRodando) e permite que o código abaixo dele seja rodado
                    }
            

                broadcast("Rodada Finalizada!!!");
                         
                // PERGUNTAR SE QUEREM JOGAR NOVAMENTE
                boolean todosQueremJogarNovamente = true;
                broadcast("Desejam jogar novamente?\n1 - SIM\n2 - NAO");

                for (int i = 0; i < 3; i++) {
                    outputs.get(i).writeBytes("Jogador " + (i + 1) + "[INPUT], votar:\n");
                    // .trim() foi adicionado para garantir que espaços em branco não interfiram na comparação.
                    String voto = inputs.get(i).readLine().trim(); 

                    if (voto == null || !voto.equals("1")) {
                        todosQueremJogarNovamente = false;
                        break; // Se um disser "não" (2 ou qualquer outra coisa), pare o loop
                    }
                }

                if (todosQueremJogarNovamente) {
                    broadcast("Todos votaram 'SIM'. Iniciando proxima rodada!");
                    // Aqui você resetaria o estado do jogo para uma nova rodada
                } else {
                    broadcast("Algum jogador votou 'NAO'. Encerrando a sessao de jogo...");
                    jogoRodando = false; // Define a flag para sair do loop principal
                }
            }
    }
        
        
        
        
        catch (Exception e) {
            broadcast("Ocorreu algum erro interno ou alguem desconectou. " + e.getMessage());
            System.out.println("Erro: " + e.getMessage());
        } finally {
             //ENCERRAMENTO FINAL DO SERVIDOR, movido para o bloco finally para garantir execução
            System.out.println("Sessao de jogo encerrada. Fechando conexoes.");
            for (Socket s : sockets) {
                 try {
                     s.close();
                 } catch (Exception e) {}
            }
            servidor.close();
        }
    }
    private static int verificarVencedor(int[] jogadas) {

	    if (jogadas[0] == jogadas[1] && jogadas[1] == jogadas[2]) {
	        return -1;
	    }

	    if (jogadas[0] != jogadas[1] && jogadas[1] == jogadas[2]) {
	        return 0;// retorna 0 pois 0+1=jogador 1 vence 
	    }

	    if (jogadas[1] != jogadas[0] && jogadas[0] == jogadas[2]) {
	        return 1;//retorna 1 pois 1+1=jogador 2 vence 
	    }

	    return 2;//retorna 2 pois 2+1=jogador 3 vence 
	}

    private static void broadcast(String msg) {
        for (DataOutputStream out : outputs) {
            try {
                out.writeBytes(msg + "\n");
            } catch (Exception e) {
            }
        }
    }
}