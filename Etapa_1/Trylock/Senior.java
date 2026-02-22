package Trylock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
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
                // PASSO 2: pega a moto (primeiro recurso)
                System.out.println("[Veterano " + id + "]: Tentando pegar a moto do Restaurante " + l + "...");
                motos[l].acquire();
                System.out.println("[Veterano " + id + "]: Peguei a chave da moto do Restaurante " + l + "!");

                // PASSO 3: caminhada até o balcão — janela para o deadlock
                Thread.sleep(1000);

                // PASSO 4: tenta pegar o pedido com timeout
                System.out.println("[Veterano " + id + "]: Aguardando pedido do Restaurante " + l + "...");
                boolean pegouPedido = pedidos[l].tryAcquire(2, TimeUnit.SECONDS);

                if (!pegouPedido) {
                    // DEADLOCK DETECTADO — solta a moto e recomeça
                    System.out.println("[Veterano " + id + "]: ⚠️  DEADLOCK DETECTADO! Soltando moto do Restaurante " + l + " e recomeçando...");
                    motos[l].release();
                    Thread.sleep(500); // pequena pausa antes de tentar de novo
                    continue;         // volta ao início do while
                }

                System.out.println("[Veterano " + id + "]: Peguei o pedido do Restaurante " + l + "!");

                // PASSO 5: faz a entrega
                System.out.println("[Veterano " + id + "]: Realizando entrega do Restaurante " + l + "...");
                Thread.sleep(1500);

                // PASSO 6 e 7: libera na ordem inversa
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