# 🛵 Guia de Desenvolvimento — Etapa 1: Impasse do Delivery

> Este guia é um roteiro de estudo e desenvolvimento. Ele **não contém o código pronto**,
> mas te diz **o que fazer, em que ordem, e como pensar** em cada etapa.

---

## 📌 Entendendo o Problema

Antes de escrever uma linha de código, entenda o cenário:

| Elemento | O que representa no código |

| Restaurante | Um índice `i` (0 até N-1) |
| Moto do restaurante `i` | `pthread_mutex_t motos[i]` |
| Pedido do restaurante `i` | `pthread_mutex_t pedidos[i]` |
| Entregador Veterano | Uma thread que executa `funcao_veterano` |
| Entregador Novato | Uma thread que executa `funcao_novato` |

### Por que o deadlock acontece?

```
Veterano escolhe restaurante 0:
  → trava motos[0]       ✅
  → tenta travar pedidos[0]  ⏳ (bloqueado!)

Novato escolhe restaurante 0 (ao mesmo tempo):
  → trava pedidos[0]     ✅
  → tenta travar motos[0]    ⏳ (bloqueado!)

Resultado: os dois ficam esperando um pelo outro. Para sempre. = DEADLOCK
```

### Por que precisa de mais threads do que restaurantes?

Se há 5 restaurantes e só 4 threads, as chances de colisão são baixas.
Com 10 threads para 5 restaurantes, a probabilidade de dois entregadores
escolherem o mesmo restaurante aumenta muito — o deadlock aparece naturalmente.

---

## 🗂️ Estrutura do Arquivo

Organize seu `main.c` nesta ordem:

```
1. includes e defines
2. variáveis globais (mutexes)
3. função do Veterano
4. função do Novato
5. main()
```

---

## 📋 Passo a Passo de Desenvolvimento

### PASSO 1 — Includes e Constantes

Você vai precisar das seguintes bibliotecas:

```c
#include <stdio.h>      // printf
#include <pthread.h>    // threads e mutexes
#include <unistd.h>     // sleep
#include <stdlib.h>     // rand, malloc
```

Defina as constantes. Lembre: threads > restaurantes para forçar deadlock:

```c
#define N_RESTAURANTES  5
#define N_VETERANOS     4   // ajuste conforme necessário
#define N_NOVATOS       4   // ajuste conforme necessário
```

> ⚠️ **Regra do trabalho:** em todo laço de repetição, use a variável `l` em vez de `i`.

---

### PASSO 2 — Variáveis Globais

Declare os arrays de mutex **fora do main**, como variáveis globais,
para que as threads consigam acessá-los:

```c
pthread_mutex_t motos[N_RESTAURANTES];
pthread_mutex_t pedidos[N_RESTAURANTES];
```

---

### PASSO 3 — Função do Veterano

O Veterano segue esta lógica **na ordem exata**:

```
loop infinito (ou por N entregas):
  1. Sorteia um restaurante alvo l  →  l = rand() % N_RESTAURANTES
  2. Trava motos[l]                 →  pthread_mutex_lock(...)
  3. Imprime: "Peguei a chave da moto do Restaurante l"
  4. Dorme um pouco                 →  sleep(1)  [simula caminhada ao balcão]
  5. Tenta travar pedidos[l]        →  pthread_mutex_lock(...)
  6. Imprime: "Peguei o pedido do Restaurante l" OU "Aguardando pedido..."
  7. [Se chegou aqui] Faz a entrega (sleep)
  8. Destrava pedidos[l]            →  pthread_mutex_unlock(...)
  9. Destrava motos[l]              →  pthread_mutex_unlock(...)
 10. Imprime: "Entrega concluída!"
```

> 💡 **Dica:** O `printf` entre o lock da moto e a tentativa de lock do pedido
> é fundamental para tornar o deadlock visível no terminal.

Assinatura da função (o que o pthread_create espera):

```c
void *funcao_veterano(void *arg) {
    int id = *((int *)arg);  // identificador do entregador
    // sua lógica aqui
    return NULL;
}
```

---

### PASSO 4 — Função do Novato

O Novato faz **o inverso** do Veterano:

```
loop infinito (ou por N entregas):
  1. Sorteia um restaurante alvo l  →  l = rand() % N_RESTAURANTES
  2. Trava pedidos[l]               →  pthread_mutex_lock(...)
  3. Imprime: "Peguei o pedido do Restaurante l"
  4. Dorme um pouco                 →  sleep(1)  [simula caminhada ao estacionamento]
  5. Tenta travar motos[l]          →  pthread_mutex_lock(...)
  6. Imprime: "Peguei a moto do Restaurante l" OU "Aguardando moto..."
  7. [Se chegou aqui] Faz a entrega (sleep)
  8. Destrava motos[l]
  9. Destrava pedidos[l]
 10. Imprime: "Entrega concluída!"
```

> ⚠️ **Atenção:** A ordem de lock/unlock importa muito.
> Sempre destrave na **ordem inversa** que travou.

---

### PASSO 5 — O main()

No `main`, você precisa fazer **4 coisas**:

#### 5a. Inicializar os mutexes
```
para l de 0 até N_RESTAURANTES-1:
    pthread_mutex_init(&motos[l], NULL)
    pthread_mutex_init(&pedidos[l], NULL)
```

#### 5b. Criar as threads

Você precisa de um array de `pthread_t` e um array de IDs inteiros:

```c
pthread_t vet_threads[N_VETERANOS];
pthread_t nov_threads[N_NOVATOS];
int ids_vet[N_VETERANOS];
int ids_nov[N_NOVATOS];
```

Depois, em laços separados, crie cada thread:

```
para l de 0 até N_VETERANOS-1:
    ids_vet[l] = l
    pthread_create(&vet_threads[l], NULL, funcao_veterano, &ids_vet[l])

para l de 0 até N_NOVATOS-1:
    ids_nov[l] = l
    pthread_create(&nov_threads[l], NULL, funcao_novato, &ids_nov[l])
```

#### 5c. Aguardar as threads (pthread_join)
```
para l de 0 até N_VETERANOS-1:
    pthread_join(vet_threads[l], NULL)

para l de 0 até N_NOVATOS-1:
    pthread_join(nov_threads[l], NULL)
```

#### 5d. Destruir os mutexes
```
para l de 0 até N_RESTAURANTES-1:
    pthread_mutex_destroy(&motos[l])
    pthread_mutex_destroy(&pedidos[l])
```

---

## 🔨 Como Compilar

O GCC precisa da flag `-lpthread` para usar a biblioteca de threads:

```bash
gcc -o delivery main.c -lpthread
./delivery
```

---

## ✅ Saída Esperada (antes do deadlock)

```
[Veterano 0]: Peguei a chave da moto do Restaurante 3.
[Novato 1]: Peguei o pedido do Restaurante 3.
[Veterano 0]: Aguardando pedido do Restaurante 3...
[Novato 1]: Aguardando moto do Restaurante 3...
```

Depois dessas mensagens, **nenhuma outra mensagem sobre o Restaurante 3 aparece** → Deadlock confirmado.

---

## 🚨 Parte Importante: Detectar e Resolver o Deadlock "em execução"

O trabalho pede que você **force o bug** e depois **resolva durante a execução**.

### Como forçar o deadlock
- Muitas threads + poucos restaurantes
- Um `sleep()` entre o primeiro e o segundo lock (dá tempo para outra thread entrar)

### Estratégias para detectar e resolver (pesquise e escolha uma):

| Estratégia | Como funciona |
|---|---|
| **Timeout com trylock** | Usa `pthread_mutex_trylock` — se não conseguir travar em X tentativas, desiste e solta o que tem |
| **Thread watchdog** | Uma thread separada monitora o tempo que cada thread está bloqueada e "mata" ou reinicia as travadas |
| **Ordenação de recursos** | Todos sempre travam na mesma ordem (ex: sempre motos antes de pedidos) — elimina o deadlock por design |

> 💡 O professor quer ver a **detecção e resolução acontecendo em tempo real**,
> com mensagens no terminal mostrando isso.

---

## 📝 Checklist Final

Antes de entregar, verifique:

- [ ] Uso de `l` em vez de `i` em todos os laços
- [ ] Array de mutexes para motos
- [ ] Array de mutexes para pedidos
- [ ] Threads Veteranas com comportamento correto (moto → pedido)
- [ ] Threads Novatas com comportamento correto (pedido → moto)
- [ ] Logging detalhado mostrando o deadlock acontecendo
- [ ] Mecanismo de detecção/resolução implementado e visível
- [ ] O número de threads é maior que o número de restaurantes
- [ ] Compila sem warnings com `-lpthread`
- [ ] Código é autoral

---

## 🧠 Perguntas para Guiar seu Raciocínio

Use essas perguntas enquanto implementa:

1. O que acontece se dois Veteranos escolherem o mesmo restaurante? Há deadlock?
2. Por que o `sleep()` **entre** os dois locks é essencial para reproduzir o deadlock?
3. Se você usar `pthread_mutex_trylock` e falhar, o que deve fazer com o mutex que já travou?
4. Como você vai saber, no código, que um deadlock ocorreu?

---

*Guia elaborado como apoio de estudo — a implementação deve ser autoral.*