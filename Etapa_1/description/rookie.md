## 🧑 Entregador Novato (`Rookie.java`)

**Perfil:** Ansioso pela comissão. Garante o pacote primeiro para marcar "coletado" no app.

### Ordem de execução

```
1. Sorteia restaurante l  →  l = rand.nextInt(N)
2. Adquire pedidos[l]     →  pedidos[l].acquire()
3. Imprime: "Peguei o pedido do Restaurante l"
4. Dorme 1s               →  Thread.sleep(1000)  ← janela para o deadlock!
5. Tenta adquirir motos[l]    →  motos[l].acquire()
6. Imprime: "Aguardando moto do Restaurante l..." (antes do acquire)
7. Imprime: "Peguei a moto do Restaurante l!" (após o acquire)
8. Dorme 1.5s             →  simula a entrega
9. Libera motos[l]        →  motos[l].release()
10. Libera pedidos[l]     →  pedidos[l].release()
11. Imprime: "Entrega concluída!"
```

### Diagrama de recursos

```
Rookie:   PEDIDO[l] ──adquire──► MOTO[l]
                                     │
                                faz entrega
                                     │
          PEDIDO[l] ◄──libera── MOTO[l]
```
