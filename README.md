# Assistente Financeiro (FluxoCaixa)

Aplicativo Android para controle de fluxo de caixa com lançamentos de receitas e despesas, extrato, saldo e assistente rápido na tela inicial.

**Repositório:** https://github.com/mthportes/FluxoCaixa

## Requisitos

- Android Studio (versão recente recomendada)
- JDK 17
- Android SDK 35
- Dispositivo Android 7.0+ (API 24) ou emulador
- Conexão com internet na primeira build (download de dependências)

## Como executar (Android Studio)

1. Clone o repositório:
   ```bash
   git clone https://github.com/mthportes/FluxoCaixa.git
   cd FluxoCaixa
   ```
2. Abra a pasta do projeto no Android Studio (**File → Open**).
3. Aguarde o **Gradle Sync** concluir.
4. Conecte um dispositivo com depuração USB ativada ou inicie um emulador.
5. Clique em **Run** (▶) e selecione o dispositivo.

## Como executar (linha de comando)

Gerar APK de debug:

```bash
./gradlew assembleDebug
```

O APK será gerado em:

```
app/build/outputs/apk/debug/app-debug.apk
```

Instalar diretamente em um dispositivo conectado:

```bash
./gradlew installDebug
```

## Configuração adicional

Não é necessário configurar variáveis de ambiente, chaves de API ou banco externo. Os dados são persistidos localmente em SQLite (`fluxo_caixa.db`).

Se houver erro de build, verifique em **Settings → Build, Execution, Deployment → Build Tools → Gradle** se o **Gradle JDK** está apontando para o **JDK 17**.

## Stack

- Kotlin
- Android SDK 35 (minSdk 24)
- MVVM + LiveData
- SQLite
- Material Design 3
- View Binding

## Estrutura principal

```
app/src/main/java/br/com/fluxocaixa/
├── data/          # modelos, SQLite e repository
├── ui/            # telas (Extrato, Lançamento, MainActivity)
└── util/          # formatadores, feedback e helpers
```

## Funcionalidades

- Cadastro de receitas e despesas
- Extrato com saldo atual
- Filtro por tipo (Todos / Receitas / Despesas)
- Assistente financeiro com atalhos na tela inicial
- Formatação monetária brasileira (R$)
- Navegação por menu lateral
