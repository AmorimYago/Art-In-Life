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
        }
    } catch (error) {
        console.error("Erro ao carregar dados do usuário:", error);
    }
});

function aplicarRestricoesParaStocker() {
    // Botão "Novo produto"
    const btnNovo = document.querySelector('button[data-bs-target="#modal-cadastrar-produto"]');
    if (btnNovo) btnNovo.disabled = true;

    // Sidebar: desativa tudo, menos Produtos
    document.querySelectorAll("#accordionSidebar .nav-item").forEach(item => {
        const link = item.querySelector("a");
        if (link && !link.href.includes("productsadm")) {
            link.style.pointerEvents = "none";
            link.style.opacity = "0.5";
            link.style.cursor = "default";
        }
    });

    // Reaplica restrições na tabela (para a renderização inicial)
    aplicarRestricoesTabelaStocker();

    // Quando o modal abrir, trava todos os campos, exceto a quantidade
    const modal = document.getElementById("modal-cadastrar-produto");
    modal.addEventListener("show.bs.modal", () => {
        setTimeout(() => {
            document.getElementById("nome-produto")?.setAttribute("disabled", true);
            document.getElementById("preco-produto")?.setAttribute("disabled", true);
            document.getElementById("descricao-produto")?.setAttribute("disabled", true);
            document.getElementById("avaliacao-produto")?.setAttribute("disabled", true);
            document.getElementById("imagens-produto")?.setAttribute("disabled", true);
            document.getElementById("quantidade-produto")?.removeAttribute("readonly");

        }, 100);
    });
}

function aplicarRestricoesTabelaStocker() {
    // Desabilita todos os switches
    document.querySelectorAll(".switch-status").forEach(sw => {
        sw.disabled = true;
        const formCheck = sw.closest(".form-check");
        if (formCheck) {
            formCheck.style.opacity = "0.5";
            formCheck.style.pointerEvents = "none";
            formCheck.style.cursor = "default";
        }
    });

    // Desabilita ícones de visualizar
    document.querySelectorAll(".btn-visualizar").forEach(icon => {
        const a = icon.closest("a");
        if (a) {
            a.addEventListener("click", e => e.preventDefault());
            a.style.pointerEvents = "none";
            a.style.opacity = "0.5";
            a.style.cursor = "default";
        }
    });

    // Mantém botão de editar funcional
    document.querySelectorAll(".btn-editar").forEach(icon => {
        const a = icon.closest("a");
        if (a) {
            a.style.pointerEvents = "auto";
            a.style.opacity = "1";
            a.style.cursor = "pointer";
        }
    });
}
