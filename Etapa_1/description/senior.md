## 👴 Entregador Veterano (`Senior.java`)

**Perfil:** Prioriza a logística. Não carrega comida sem garantir transporte primeiro.

### Ordem de execução

```
1. Sorteia restaurante l  →  l = rand.nextInt(N)
2. Adquire motos[l]       →  motos[l].acquire()
3. Imprime: "Peguei a chave da moto do Restaurante l"
4. Dorme 1s               →  Thread.sleep(1000)  ← janela para o deadlock!
5. Tenta adquirir pedidos[l]  →  pedidos[l].acquire()
6. Imprime: "Aguardando pedido do Restaurante l..." (antes do acquire)
7. Imprime: "Peguei o pedido do Restaurante l!" (após o acquire)
8. Dorme 1.5s             →  simula a entrega
9. Libera pedidos[l]      →  pedidos[l].release()
10. Libera motos[l]       →  motos[l].release()
11. Imprime: "Entrega concluída!"
```

### Diagrama de recursos

```
Senior:   MOTO[l] ──adquire──► PEDIDO[l]
                                    │
                               faz entrega
                                    │
          MOTO[l] ◄──libera── PEDIDO[l]
```