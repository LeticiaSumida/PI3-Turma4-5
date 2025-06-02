# Super-ID — Grupo 5 | Turma 4

## Sobre o Projeto
SuperID é um sistema de autenticação desenvolvido como parte do Projeto Integrador 3 do curso de Engenharia de Software da PUC-Campinas. Ele simula um gerenciador de autenticações com foco em segurança de senhas e login sem uso de credenciais tradicionais, utilizando tecnologias modernas como Kotlin, Android Studio e Google Firebase.

O projeto é dividido em duas partes principais:

Um aplicativo Android, que permite ao usuário criar uma conta, armazenar senhas de forma segura e realizar logins.

Um sistema web com APIs em Firebase Functions, que permite a sites parceiros oferecerem login via QR Code, eliminando a necessidade de digitar senha.

## Instalação
1. Acesse a aba Releases deste repositório.
2. Baixe o APK mais recente.
3. Ative a instalação de apps de fontes desconhecidas no seu celular.
4. Instale o APK.

## Como Usar
- Ao abrir o aplicativo pela primeira vez, leia e aceite os termos de uso para continuar.

- Cadastre-se informando seu nome, e-mail e senha mestre, e em seguida acesse sua conta.

- (Opcional) Verifique seu e-mail para liberar funcionalidades como recuperação de senha e login sem senha.

- Organize suas informações criando categorias e adicionando suas senhas.

- Quando quiser fazer login em um site parceiro, toque no ícone de QR Code e escaneie o código exibido na tela do site.

- Para encerrar o uso, utilize a função de logout disponível no menu do usuário.

## Testar o Login Sem Senha
Você pode testar a leitura de QR Code com o site de testes:

https://superid-d1bf3.web.app/

1. Clique em "Login com Super ID".
2. Um QR Code será gerado.
3. Abra o app e escaneie o código.
4. O login será feito automaticamente no site.

## Tecnologias Utilizadas
- Kotlin (app Android)
- Jetpack Compose (interface)
- Firebase Authentication (login e cadastro)
- Firebase Firestore (banco de dados em nuvem)
- Firebase Cloud Functions (backend)
- ML Kit (leitura de QR Codes)
- HTML, CSS e JavaScript (site de testes)

## Observações
- A verificação de e-mail libera o login sem senha e recuperação de conta.
- O login por QR Code só funciona em sites parceiros autorizados.
- É necessário estar conectado à internet.

## Links
- Descritivo do projeto
- Protótipos
- Desenvolvido por Grupo 5 — Turma 4
