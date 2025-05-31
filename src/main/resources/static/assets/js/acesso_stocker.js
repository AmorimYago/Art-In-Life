document.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await fetch("/api/me", { method: "GET" });
        if (!response.ok) {
            console.warn("Falha ao buscar /api/me. Código:", response.status);
            return;
        }

        const user = await response.json();
        if (user.type === "STOCKER") {
            window.userIsStocker = true; // marcador global
            aplicarRestricoesParaStocker();

            // Notifica productsadm.js para aplicar restrições na tabela após a renderização
            if (typeof window.aplicarRestricoesTabelaStockerExterno === 'function') {
                window.aplicarRestricoesTabelaStockerExterno();
            }
        }
    } catch (error) {
        console.error("Erro ao carregar dados do usuário:", error);
    }
});

function aplicarRestricoesParaStocker() {
    // 1. Botão "Novo produto"
    const btnNovo = document.querySelector('button[data-bs-target="#modal-cadastrar-produto"]');
    if (btnNovo) {
        btnNovo.disabled = true;
        btnNovo.style.opacity = "0.5";
        btnNovo.style.cursor = "not-allowed";
        btnNovo.title = "Estoquistas não podem criar novos produtos.";
    }

    // 2. Sidebar: Estoquista pode acessar Produtos e Pedidos
    document.querySelectorAll("#accordionSidebar .nav-item").forEach(item => {
        const link = item.querySelector("a");
        if (link) {
            const href = link.getAttribute('href');
            // Permite acesso a productsadm e pedidos (assumindo que "pedidos" está no href)
            if (href && !(href.includes("productsadm") || href.includes("pedidos"))) {
                link.style.pointerEvents = "none";
                link.style.opacity = "0.5";
                link.style.cursor = "default";
                link.title = "Acesso restrito para Estoquistas.";
            } else {
                link.style.pointerEvents = "auto";
                link.style.opacity = "1";
                link.style.cursor = "pointer";
                link.title = ""; // Limpa qualquer título anterior
            }
        }
    });

    // 3. Modal de Edição/Cadastro de Produto (apenas para productsadm.html)
    const modalCadastroEl = document.getElementById("modal-cadastrar-produto");
    if (modalCadastroEl) {
        modalCadastroEl.addEventListener("show.bs.modal", () => {
            setTimeout(() => { // Pequeno delay para garantir que os elementos estejam renderizados
                document.getElementById("nome-produto")?.setAttribute("disabled", true);
                document.getElementById("preco-produto")?.setAttribute("disabled", true);
                document.getElementById("descricao-produto")?.setAttribute("disabled", true);
                document.getElementById("avaliacao-produto")?.setAttribute("disabled", true);
                document.getElementById("imagens-produto")?.setAttribute("disabled", true);

                // Permite apenas a edição da quantidade
                const quantidadeInput = document.getElementById("quantidade-produto");
                if (quantidadeInput) {
                    quantidadeInput.removeAttribute("disabled"); // Garante que não esteja desabilitado
                    quantidadeInput.removeAttribute("readonly"); // Remove readonly para edição
                }



            }, 100);
        });
    }
}

// Esta função será chamada pelo productsadm.js após a renderização da tabela
window.aplicarRestricoesTabelaStockerExterno = function() {
    // Desabilita todos os switches de status (Estoquista não pode mudar status)
    document.querySelectorAll(".switch-status").forEach(sw => {
        sw.disabled = true;
        const formCheck = sw.closest(".form-check");
        if (formCheck) {
            formCheck.style.opacity = "0.5";
            formCheck.style.pointerEvents = "none";
            formCheck.style.cursor = "not-allowed";
            formCheck.title = "Estoquistas não podem mudar o status do produto.";
        }
    });

    // Mantém ícones de visualizar funcional (Estoquista PODE visualizar)
    document.querySelectorAll(".btn-visualizar").forEach(icon => {
        const a = icon.closest("a");
        if (a) {
            a.style.pointerEvents = "auto";
            a.style.opacity = "1";
            a.style.cursor = "pointer";
            a.title = "";
        }
    });

    // Mantém botão de editar funcional (Estoquista PODE editar, mas com restrições no modal)
    document.querySelectorAll(".btn-editar").forEach(icon => {
        const a = icon.closest("a");
        if (a) {
            a.style.pointerEvents = "auto";
            a.style.opacity = "1";
            a.style.cursor = "pointer";
            a.title = "";
        }
    });
};