# Guia de Contribuição

---

## Estrutura de Branches

### `master`
- Branch principal com as **funcionalidades/conteúdo mais recentes**.
- **Não garante** que possui a versão mais atualizada do Forge ou minecraft.

### `release`
- Branch para feature com as **funcionalidades/conteúdo em desenvolvimento**.

### `feature-{nome}` / `hotfix-{nome}`
- Branches para desenvolvimento de novos recursos ou correção de bugs na `release`.
- **Exemplo**: `feature-servos`

### `forge-{versão}` / `fabric-{versão}`
- Branches principais para quando for necessario pra cada versão específica do Forge/Fabric/Neoforge. São branchs para quando for necessario levar funcionalidades mais novas para versões mais antigas
- **Exemplo**: `forge-1.21.3`

### `hotfix-forge-{versão}` / `hotfix-fabric-{versão}`
- Branches destinadas a correções de bugs nas branches do Forge/Fabric/Neoforge.
- **Exemplo**: `hotfix-forge-1.21.3`

---

## Regras para Desenvolvimento

1. **Funcionalidades novas** devem ser implementadas inicialmente nas features que serão mergeadas na release e posteriomente a release na `master` para garantir uma versão estável com todo o conteúdo disponível.
2. Sempre que possível, fazer o merge da `master` nas branches principais (`forge-{versão}` / `fabric-{versão}`) utilizando uma branch intermediária:
    - Nome: `merge-master-{forge/fabric}-{versão}`
    - Criar ela com base na branch principal (forge/fabric) e fazer o merge da master nela
    - Essa branch serve para ajustes antes do merge final, garantindo que as branches principais continuem funcionando enquanto as correções são feitas.
3. **Criar branches principais do Forge/Fabric** a partir da `master`.
4. **Manter o Changelog atualizado**.

---

## Estrutura Específica de Versões

### Versões mais detalhadas
Caso necessário, criar uma versão mais específica para as branches principais:
- **Formato**: `{forge/fabric}-{versão}-{versão-específica}`
- **Exemplo**: `forge-1.21.3-53.0.19`

A regra de hotfix permanece a mesma:
- **Exemplo**: `hotfix-forge-1.21.3-53.0.19`

**Importante**: **Nunca** realizar merge de qualquer branch principal (`forge` ou `fabric`) na `master`.

---

## Tags para Versões Finais

### Formato de Tags

althera-{versão-mod}-{forge/fabric}-{versão}-{versão-específica (se houver)}


**Exemplos**:
- `althera-0.0.0-forge-1.21.3`
- `althera-0.0.0-forge-1.21.3-53.0.19`

### Regras de Incremento
- **Novos recursos menores**: Incrementar o segundo número.
    - Exemplo: `althera-0.1.0-forge-1.21.3`
- **Atualizações grandes ou incompatíveis**: Incrementar o primeiro número.
    - Exemplo: `althera-1.0.0-forge-1.21.3`
- **Correções de bugs**: Incrementar o terceiro número.
    - Exemplo: `althera-0.0.1-forge-1.21.3`

**Nota**: Todas as branches principais devem compartilhar o mesmo primeiro e segundo números que a `master` (caso estejam atualizadas), mas o terceiro número pode variar devido a correções específicas.

---

## Importância do Changelog

O changelog permite identificar facilmente quais funcionalidades estão presentes em cada versão do forge/fabric. Por exemplo:
- Se a versão do mod for `1.1.0` na versão `1.21.3` do Forge, todas as outras versões relacionadas serão `1.1.x`.

---

## Estrutura do Changelog

### Funcionalidades ou Atualizações

{versão do mod} - {Título} {natureza: feature/hotfix ou ambos}

{Descrição detalhada das alterações}

### Hotfix para Versões Específicas

{versão do mod} - {Título} hotfix - {forge/fabric}-{versão}-{versão-específica (se houver)}

{Descrição detalhada das alterações}


---

## Dúvidas ou Sugestões
Caso tenha dúvidas ou sugestões, entre em contato!
