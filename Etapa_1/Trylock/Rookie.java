package Trylock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Rookie implements Runnable {

    private int id;
    private Semaphore[] motos;
    private Semaphore[] pedidos;
    private int N;

    public Rookie(int id, Semaphore[] motos, Semaphore[] pedidos, int N) {
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
                // PASSO 2: pega o pedido (primeiro recurso)
                System.out.println("[Novato " + id + "]: Tentando pegar pedido do Restaurante " + l + "...");
                pedidos[l].acquire();
                System.out.println("[Novato " + id + "]: Peguei o pedido do Restaurante " + l + "!");

                // PASSO 3: caminhada até o estacionamento — janela para o deadlock
                Thread.sleep(1000);

                // PASSO 4: tenta pegar a moto com timeout
                System.out.println("[Novato " + id + "]: Aguardando moto do Restaurante " + l + "...");
                boolean pegouMoto = motos[l].tryAcquire(2, TimeUnit.SECONDS);

                if (!pegouMoto) {
                    // DEADLOCK DETECTADO — solta o pedido e recomeça
                    System.out.println("[Novato " + id + "]: ⚠️  DEADLOCK DETECTADO! Soltando pedido do Restaurante " + l + " e recomeçando...");
                    pedidos[l].release();
                    Thread.sleep(500); // pequena pausa antes de tentar de novo
                    continue;         // volta ao início do while
                }

                System.out.println("[Novato " + id + "]: Peguei a chave da moto do Restaurante " + l + "!");

                // PASSO 5: faz a entrega
                System.out.println("[Novato " + id + "]: Realizando entrega do Restaurante " + l + "...");
                Thread.sleep(1500);

                // PASSO 6 e 7: libera na ordem inversa
                motos[l].release();
                pedidos[l].release();
                System.out.println("[Novato " + id + "]: Entrega do Restaurante " + l + " concluída!");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Novato " + id + "]: Fui interrompido!");
                break;
            }
        }
    }
}