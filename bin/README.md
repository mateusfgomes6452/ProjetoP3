# Divisão de Responsabilidades da Equipe

## Samara Karen

### Configuração Inicial

* Criação e configuração do projeto Maven

### CRUDs

* CRUD de Fornecedores

### Regras de Negócio

* RN01 - Disponibilidade

### Controle de Acesso

* Cliente
* Funcionário
* Administrador

### Testes

* Desenvolvimento dos testes automatizados utilizando JUnit 5

### Observações

* Caso novas atividades sejam atribuídas, esta seção será atualizada.

---

## Fabiane Jennyfer

### Modelagem

* Modelagem UML do sistema

### CRUDs

* CRUD de Categorias
* CRUD de Multas

### Regras de Negócio

* RN04 - Inadimplência

### Documentação

* Elaboração do relatório seguindo as normas ABNT

### Observações

* Caso novas atividades sejam atribuídas, esta seção será atualizada.

---

## Eric Felix

### Arquitetura

* Estruturação do projeto
* Definição e organização dos packages
* Criação das interfaces do sistema

### CRUDs

* CRUD de Usuários
* CRUD de Contratos de Aluguel

### Regras de Negócio

* RN02 - Cálculo de Valor
* RN03 - Multa por Atraso

### Persistência de Dados

* Implementação do salvamento e carregamento dos dados utilizando arquivos
* Manipulação da persistência sem utilização de banco de dados

### Padrões de Projeto

* Estudo e implementação do padrão Facade

### Observações

* Caso novas atividades sejam atribuídas, esta seção será atualizada.

## João Henrique
 ### Cruds
 * CRUD de Itens
 ### Regra de negócio
 * RN05 - Integridade


</content>

# Convenção de Commits

Para manter o histórico do projeto organizado e facilitar o acompanhamento da participação de todos os integrantes, a equipe adotará uma convenção para as mensagens de commit.

## Evite mensagens genéricas

```bash
git commit -m "alterações"
git commit -m "corrigido"
git commit -m "teste"
```

Essas mensagens não deixam claro o que foi feito e dificultam a manutenção do projeto.

## Utilize mensagens padronizadas

```bash
git commit -m "feat: adiciona CRUD de clientes"
git commit -m "fix: corrige cálculo de multa por atraso"
git commit -m "docs: atualiza README"
git commit -m "test: adiciona testes do ContratoService"
```

## Prefixos Utilizados

| Prefixo     | Descrição                                          |
| ----------- | -------------------------------------------------- |
| `feat:`     | Nova funcionalidade                                |
| `fix:`      | Correção de bug                                    |
| `refactor:` | Refatoração sem alterar o comportamento do sistema |
| `test:`     | Adição ou alteração de testes                      |
| `docs:`     | Alterações na documentação                         |
| `style:`    | Formatação e organização de código                 |
| `chore:`    | Configurações, dependências e tarefas gerais       |



## Boas Práticas

* Realizar commits pequenos e frequentes.
* Escrever mensagens objetivas e descritivas.
* Evitar commits contendo muitas funcionalidades diferentes.
* Sempre que possível, associar o commit à funcionalidade ou regra de negócio implementada.


