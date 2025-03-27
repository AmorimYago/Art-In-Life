# ArtInLife E-commerce Backend

Este é o projeto backend de um e-commerce de quadros, desenvolvido em **Spring Boot 3.4.4** com banco de dados **PostgreSQL**.

## 🧱 Estrutura do Projeto

- **Spring Boot** com arquitetura em camadas
- **JPA (Hibernate)** para mapeamento objeto-relacional
- **Lombok** para reduzir boilerplate
- **Spring Security** (temporariamente desabilitado)
- **UUID** como identificador padrão
- **Pasta `images/`** para armazenar imagens localmente
- **Tudo padronizado em inglês**

## 🧠 Funcionalidades

- Cadastro de produtos com múltiplas imagens (uma principal)
- Carrinho anônimo ou vinculado a usuário logado
- Avaliação de produtos com notas de 1 a 5 estrelas
- Geração de pedidos a partir do carrinho
- Controle de acesso por tipo de usuário (CLIENT, ADMIN, STOCKER)
- Usuários e produtos podem ser desabilitados (não excluídos)

## 📦 Entidades

| Entidade         | Descrição                                |
|------------------|--------------------------------------------|
| `AppUser`        | Usuários do sistema (CLIENT, ADMIN, STOCKER) |
| `Product`        | Produto à venda                            |
| `ProductImage`   | Imagens do produto                          |
| `ProductReview`  | Avaliação com estrelas e comentário         |
| `Cart`           | Carrinho com total, itens, usuário (opcional) |
| `CartItem`       | Itens no carrinho                           |
| `DeliveryAddress`| Endereço de entrega                        |
| `Order`          | Pedido finalizado                          |
| `OrderItem`      | Itens de um pedido                          |

## 📂 Estrutura de Pacotes

```
src/main/java/com/br/pi4/artinlife/
├── config/        # Configurações do Spring (segurança, uploads)
├── controller/    # Controllers (em breve)
├── dto/           # Data Transfer Objects (validação de entrada)
├── model/         # Entidades JPA
├── repository/    # Repositórios com Spring Data JPA
├── service/       # Regras de negócio
```

## 🔐 Segurança

A segurança padrão do Spring Security foi temporariamente desabilitada durante o desenvolvimento com:

```java
http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
    .formLogin(form -> form.disable())
    .httpBasic(basic -> basic.disable());
```

## 🖼️ Upload de Imagens

- Imagens são salvas na pasta `/images`
- A URL pública é configurada via `WebMvcConfigurer`
- Apenas o caminho é salvo no banco, não o arquivo

## 🚀 Como rodar

1. Crie o banco PostgreSQL e configure em `application.properties`
2. Compile e rode o projeto com o Spring Boot
3. Acesse `http://localhost:8080` (sem login obrigatório no momento)

## ✅ Status

✔️ Entidades prontas  
✔️ DTOs criados com validações  
✔️ Services prontos e comentados  
🚧 Controllers serão implementados conforme o front for evoluindo

## 📊 Diagrama das Entidades

![Entity Relationship Diagram](entities_diagram.png)



---

> Esse README pode ser atualizado conforme as funcionalidades forem evoluindo no projeto.
