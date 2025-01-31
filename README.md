Gerenciador de Documentos

Introdução

Este projeto tem como objetivo fornecer uma solução eficiente para o gerenciamento de documentos, garantindo a integridade, organização e validação dos dados de forma automatizada. A aplicação permite a importação de documentos via CSV, validação de campos obrigatórios, e armazenamento seguro dos registros.

Tecnologias Utilizadas

Java + Spring Boot: Escolhido por sua robustez, escalabilidade e suporte nativo a API REST.

JUnit + Mockito: Para garantir a confiabilidade e qualidade do código através de testes unitários e mock de dependências.

Apache Commons CSV: Para facilitar o processamento e leitura de arquivos CSV.

Angular: Utilizado no frontend para proporcionar uma interface dinâmica e responsiva.

Banco de Dados Relacional: PostgreSQL, garantindo persistência e integridade referencial dos dados.

Funcionalidades

1. Importação e Validação de Documentos via CSV

Cada documento importado passa por um processo de validação para garantir que todos os campos obrigatórios estejam preenchidos corretamente. Caso contrário, uma exceção é lançada.

Justificativa: A validação evita que dados inconsistentes entrem no sistema, garantindo qualidade e padronização das informações.

2. Unicidade de Documentos (Sigla + Versão)

Antes de armazenar um novo documento, o sistema verifica se a combinação sigla e versão já existe. Caso positivo, a importação é interrompida.

Justificativa: Esta regra de negócio impede duplicações que possam comprometer a rastreabilidade e organização dos documentos.

3. Armazenamento e Gerenciamento de Documentos

Os documentos são salvos no banco de dados, seguindo uma estrutura bem definida para otimizar consultas e garantir integridade.

Justificativa: O uso de um banco de dados relacional permite melhor controle sobre os registros, além de facilitar futuras integrações.

Testes Automatizados

Foram implementados testes unitários utilizando JUnit e Mockito para simular comportamentos e garantir que cada funcionalidade seja testada de forma isolada.

Configuração e Execução

Requisitos

Java 17+

Maven 3+

Banco de Dados PostgreSQL

Como Executar

Clone o repositório:

git clone https://github.com/seuusuario/gerenciador-documentos.git

Acesse a pasta do projeto:

cd gerenciador-documentos

Configure as variáveis de ambiente no application.properties.

Execute o projeto com:

mvn spring-boot:run

Problemas e Melhorias Futuras

Aviso do Mockito sobre inline-mock-maker: O projeto apresentou um alerta de que o uso dinâmico de agentes será descontinuado. Para evitar problemas futuros, será necessário adicionar o Mockito como um agente no build.

Melhoria na importação de CSV: Implementação de um validador mais flexível para lidar com diferentes formatos de arquivos.

Contribuição

Caso queira contribuir, sinta-se à vontade para abrir uma issue ou enviar um pull request. Toda ajuda é bem-vinda!
