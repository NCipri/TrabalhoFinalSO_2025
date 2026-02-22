import java.util.concurrent.Semaphore;
import java.util.Random;

public class Senior implements Runnable {

    private int id;
    private Semaphore[] motos;
    private Semaphore[] pedidos;
    private int N; 

    public Senior(int id, Semaphore[] motos, Semaphore[] pedidos, int N) {
        this.id = id;
        this.motos = motos;
        this.pedidos = pedidos;
        this.N = N;
    }

    @Override
    public void run() {
        Random rand = new Random();

        while (true) {
            int l = rand.nextInt(N);

            try {
                System.out.println("[Veterano " + id + "]: Tentando pegar a moto do Restaurante " + l + "...");
                motos[l].acquire();
                System.out.println("[Veterano " + id + "]: Peguei a chave da moto do Restaurante " + l + "!");

                Thread.sleep(1000);

                System.out.println("[Veterano " + id + "]: Aguardando o pedido do Restaurante " + l + "...");
                pedidos[l].acquire();
                System.out.println("[Veterano " + id + "]: Peguei o pedido do Restaurante " + l + "!");

                System.out.println("[Veterano " + id + "]: Realizando entrega do Restaurante " + l + "...");
                Thread.sleep(1500);

                pedidos[l].release();
                motos[l].release();
                System.out.println("[Veterano " + id + "]: Entrega do Restaurante " + l + " concluída!");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Veterano " + id + "]: Fui interrompido!");
                break;
            }
        }
    }
}