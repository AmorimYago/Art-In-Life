document.addEventListener("DOMContentLoaded", async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get("id");

    if (!id) return;

    try {
        const response = await fetch(`/api/products/${id}`);
        if (!response.ok) throw new Error("Produto não encontrado");

        const produto = await response.json();
        document.getElementById("add-to-cart-btn").setAttribute("data-id", produto.id);

        // Atualiza informações principais
        document.querySelector("h1").textContent = produto.name;
        document.querySelector("h5").textContent = produto.id;
        document.getElementById("product-description").textContent = produto.description;
        document.querySelector("h3").textContent = "R$ " + produto.price.toFixed(2);

        // Preenche carrossel com as imagens
        const inner = document.querySelector("#carousel-1 .carousel-inner");
        const indicators = document.querySelector("#carousel-1 .carousel-indicators");

        inner.innerHTML = "";
        indicators.innerHTML = "";

        produto.images.forEach((img, i) => {
            inner.innerHTML += `
                <div class="carousel-item ${i === 0 ? "active" : ""}">
                    <img class="object-fit-contain w-100 d-block" src="/images/${img.path}" height="600">
                </div>
            `;
            indicators.innerHTML += `
                <button type="button" data-bs-target="#carousel-1" data-bs-slide-to="${i}" class="${i === 0 ? "active" : ""}"></button>
            `;
        });

        // Renderiza as estrelas de avaliação
        const starsContainer = document.getElementById("rating-stars");
        starsContainer.innerHTML = "";
        const rating = Math.round(produto.rating || 0); // garante valor inteiro de 0 a 5

        for (let i = 1; i <= 5; i++) {
            if (i <= rating) {
                starsContainer.innerHTML += '<i class="fas fa-star text-warning"></i>'; // estrela cheia
            } else {
                starsContainer.innerHTML += '<i class="far fa-star text-warning"></i>'; // estrela vazia
            }
        }

    } catch (err) {
        console.error("Erro ao carregar produto:", err);
        document.querySelector("h1").textContent = "Produto não encontrado";
        document.querySelector("#carousel-1 .carousel-inner").innerHTML = "";
    }
});

document.getElementById("add-to-cart-btn").addEventListener("click", () => {
    const id = document.getElementById("add-to-cart-btn").getAttribute("data-id");
    const quantity = parseInt(document.getElementById("quantity").value) || 1;
    addToCart(id, quantity);
});

