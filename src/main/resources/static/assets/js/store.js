document.addEventListener("DOMContentLoaded", async () => {
    const container = document.querySelector(".row.gy-4");

    try {
        const response = await fetch("/api/products");
        const produtos = await response.json();

        container.innerHTML = ""; // limpa placeholder

        produtos.forEach(produto => {
            if (!produto.status) return; // exibe apenas produtos ativos

            const imagem = produto.images.length > 0
                ? `/images/${produto.images[0].path}`
                : "assets/img/placeholder.jpg";

            const card = document.createElement("div");
            card.className = "col";
            card.innerHTML = `
    <div class="card h-100" style="min-height: 500px;">
        <img class="card-img-top w-100 d-block"
             src="${imagem}"
             alt="${produto.name}"
             style="height: 350px; object-fit: cover; object-position: center;">
        <div class="card-body d-flex flex-column p-4">
            <h4 class="card-title">${produto.name}</h4>
            <div class="mt-auto">
                <p class="text-primary card-text mb-2">R$ ${produto.price.toFixed(2)}</p>
                <div class="btn-group btn-group-sm gap-1" role="group">
                    <button class="btn btn-light btn-add-cart" type="button" data-id="${produto.id}">Adicionar ao carrinho</button>
                    <a class="btn btn-primary" href="product?id=${produto.id}">Visualizar</a>
                </div>
            </div>
        </div>
    </div>
`;

            container.appendChild(card);
        });

        // Aguarda o DOM atualizar, então padroniza altura
        setTimeout(padronizarAlturaDosCards, 50);

    } catch (err) {
        console.error("Erro ao carregar produtos:", err);
        container.innerHTML = "<p>Não foi possível carregar os produtos.</p>";
    }
});

function padronizarAlturaDosCards() {
    const cards = document.querySelectorAll(".card");
    let maxHeight = 0;

    cards.forEach(card => {
        card.style.height = "auto"; // reseta
        const height = card.offsetHeight;
        if (height > maxHeight) maxHeight = height;
    });

    cards.forEach(card => {
        card.style.height = `${maxHeight}px`;
    });
}

document.addEventListener("click", e => {
    if (e.target.classList.contains("btn-add-cart")) {
        const id = e.target.getAttribute("data-id");
        addToCart(id);
    }
});

