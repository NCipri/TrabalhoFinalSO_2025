# 🛵 Comportamento dos Entregadores — Laranjal Foods

---

## 🔑 Recursos Compartilhados

| Recurso | Representa | No código |

| `motos[l]` | Chave da moto do restaurante `l` | `Semaphore` com 1 permissão |
| `pedidos[l]` | Pacote de comida do restaurante `l` | `Semaphore` com 1 permissão |

> Cada restaurante tem **exatamente 1 moto** e **1 pedido** disponível por vez.

---

## 💀 Como o Deadlock Acontece

Quando um `Senior` e um `Rookie` escolhem o **mesmo restaurante `l`** quase simultaneamente:

```
Tempo →

Senior: adquire motos[l] ✅ ... dorme ... tenta pedidos[l] ⏳ BLOQUEADO
Rookie: adquire pedidos[l] ✅ ... dorme ... tenta motos[l] ⏳ BLOQUEADO

Resultado: cada um segura o que o outro precisa. Ninguém avança. = DEADLOCK
```

### Saída esperada no terminal

```
[Veterano 0]: Peguei a chave da moto do Restaurante 2!
[Novato 1]:   Peguei o pedido do Restaurante 2!
[Veterano 0]: Aguardando pedido do Restaurante 2...
[Novato 1]:   Aguardando moto do Restaurante 2...
(silêncio sobre o Restaurante 2 para sempre → deadlock confirmado)
```

---

## ↔️ Comparação lado a lado

| Etapa | Senior (Veterano) | Rookie (Novato) |

| 1º recurso adquirido | `motos[l]` | `pedidos[l]` |
| 2º recurso adquirido | `pedidos[l]` | `motos[l]` |
| 1º recurso liberado | `pedidos[l]` | `motos[l]` |
| 2º recurso liberado | `motos[l]` | `pedidos[l]` |
| Sleep entre locks | ✅ Sim (cria janela) | ✅ Sim (cria janela) |

> ⚠️ A liberação é sempre na **ordem inversa** da aquisição.

---

## 📋 Regras de Implementação

- Usar `l` em vez de `i` em todos os laços de repetição
- Semáforos inicializados com `new Semaphore(1)` — binários (equivalente a mutex)
- Número de threads **maior** que número de restaurantes para forçar colisões
- Proibido usar `synchronized` — apenas `Semaphore` do `java.util.concurrent`

---

## 🚨 Estratégias de Resolução (escolha uma para implementar)

| Estratégia | Mecanismo | Método Java |

| **Trylock com timeout** | Tenta adquirir por X segundos; se falhar, solta o que tem e recomeça | `semaphore.tryAcquire(tempo, TimeUnit.SECONDS)` |
| **Thread Watchdog** | Thread separada monitora o tempo bloqueado e interrompe travadas | `thread.interrupt()` |
| **Ordenação de recursos** | Todos sempre adquirem na mesma ordem (elimina deadlock por design) | Reordenar os `acquire()` |