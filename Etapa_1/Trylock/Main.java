package Trylock;

import java.util.concurrent.Semaphore;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Número de restaurantes (5 a 10): ");
        int N = scanner.nextInt();

        if (N < 5 || N > 10) {
            System.out.println("Valor inválido! Use entre 5 e 10.");
            scanner.close();
            return;
        }

        int nSeniors = N + 2;
        int nRookies = N + 2;

        Semaphore[] motos   = new Semaphore[N];
        Semaphore[] pedidos = new Semaphore[N];

        for (int l = 0; l < N; l++) {
            motos[l]   = new Semaphore(1);
            pedidos[l] = new Semaphore(1);
        }

        // cria os arrays de threads
        Thread[] seniorThreads = new Thread[nSeniors];
        Thread[] rookieThreads = new Thread[nRookies];

        for (int l = 0; l < nSeniors; l++) {
            seniorThreads[l] = new Thread(new Senior(l, motos, pedidos, N));
            seniorThreads[l].start();
        }

        for (int l = 0; l < nRookies; l++) {
            rookieThreads[l] = new Thread(new Rookie(l, motos, pedidos, N));
            rookieThreads[l].start();
        }

        for (int l = 0; l < nSeniors; l++) {
            try {
                seniorThreads[l].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (int l = 0; l < nRookies; l++) {
            try {
                rookieThreads[l].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        scanner.close();
    }
}