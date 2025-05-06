document.addEventListener("DOMContentLoaded", () => {
    const tabelaCorpo = document.querySelector("table tbody");
    const campoBusca = document.querySelector("input[type='search']");
    const paginacao = document.querySelector(".pagination");
    const modalVisualizar = new bootstrap.Modal(document.getElementById("modal-mostrar-produto"));
    const modalCadastroEl = document.getElementById("modal-cadastrar-produto");
    const modalCadastro = new bootstrap.Modal(modalCadastroEl);
    const estrelasContainer = document.getElementById("produto-avaliacao-estrelas");
    const carouselCriar = document.querySelector("#carousel-1 .carousel-inner");
    const carouselIndicadoresCriar = document.querySelector("#carousel-1 .carousel-indicators");
    const carouselVisualizar = document.querySelector("#carousel-5 .carousel-inner");
    const carouselIndicadoresVisualizar = document.querySelector("#carousel-5 .carousel-indicators");
    const inputImagens = document.getElementById("imagens-produto");
    const btnSalvarProduto = document.querySelector("#modal-cadastrar-produto .btn-primary");

    let todosProdutos = [];
    let paginaAtual = 1;
    const itensPorPagina = 10;
    let imagensSelecionadas = [];
    let produtoEmEdicaoId = null;

    async function carregarProdutos() {
        const resposta = await fetch("/api/products");
        todosProdutos = (await resposta.json()).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        renderizarTabela();
    }

    function renderizarTabela(filtro = "") {
        const produtosFiltrados = todosProdutos.filter(p =>
            p.name.toLowerCase().includes(filtro.toLowerCase())
        );

        const inicio = (paginaAtual - 1) * itensPorPagina;
        const fim = inicio + itensPorPagina;
        const produtosPagina = produtosFiltrados.slice(inicio, fim);

        tabelaCorpo.innerHTML = produtosPagina.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.name}</td>
                <td>${p.stock}</td>
                <td>R$ ${p.price.toFixed(2)}</td>
                <td>${p.status ? "Ativo" : "Inativo"}</td>
                <td style="display: flex; align-items: center; gap: 8px;">
                    <a href="#" class="btn-editar" data-id="${p.id}"><i class="fas fa-edit"></i></a>
                    <a href="#" class="btn-visualizar" data-id="${p.id}"><i class="far fa-eye"></i></a>
                    <div class="form-check form-switch m-0">
                        <input class="form-check-input switch-status" type="checkbox" data-id="${p.id}" ${p.status ? "checked" : ""}>
                    </div>
                </td>
            </tr>
        `).join("");

        renderizarPaginacao(produtosFiltrados.length);

        if (window.userIsStocker) {
            aplicarRestricoesTabelaStocker();
        }
    }

    function renderizarPaginacao(totalItens) {
        const totalPaginas = Math.ceil(totalItens / itensPorPagina);
        let html = "";

        html += `<li class="page-item"><a class="page-link" href="#" data-pagina="${paginaAtual - 1}">«</a></li>`;
        for (let i = 1; i <= totalPaginas; i++) {
            html += `<li class="page-item ${i === paginaAtual ? "active" : ""}">
                        <a class="page-link" href="#" data-pagina="${i}">${i}</a>
                     </li>`;
        }
        html += `<li class="page-item"><a class="page-link" href="#" data-pagina="${paginaAtual + 1}">»</a></li>`;
        paginacao.innerHTML = html;
    }

    paginacao.addEventListener("click", e => {
        e.preventDefault();
        if (e.target.tagName === "A") {
            const novaPagina = parseInt(e.target.dataset.pagina);
            if (!isNaN(novaPagina) && novaPagina > 0) {
                paginaAtual = novaPagina;
                renderizarTabela(campoBusca.value);
            }
        }
    });

    campoBusca.addEventListener("input", () => {
        paginaAtual = 1;
        renderizarTabela(campoBusca.value);
    });

    tabelaCorpo.addEventListener("change", async e => {
        if (e.target.classList.contains("switch-status")) {
            const id = e.target.dataset.id;
            const url = `/api/products/${id}/${e.target.checked ? "enable" : "disable"}`;
            await fetch(url, { method: "PATCH" });
            carregarProdutos();
        }
    });

    tabelaCorpo.addEventListener("click", async e => {
        if (e.target.closest(".btn-visualizar")) {
            e.preventDefault();
            const id = e.target.closest(".btn-visualizar").dataset.id;
            const resposta = await fetch(`/api/products/${id}`);
            const produto = await resposta.json();

            document.querySelector("#modal-mostrar-produto h1").textContent = produto.name;
            document.querySelector("#modal-mostrar-produto h5").textContent = produto.id;
            document.querySelector("#modal-mostrar-produto p").textContent = produto.description;
            document.querySelector("#modal-mostrar-produto h3").textContent = "R$ " + produto.price.toFixed(2);
            renderizarEstrelas(produto.rating);

            carouselVisualizar.innerHTML = "";
            carouselIndicadoresVisualizar.innerHTML = "";
            produto.images.forEach((img, index) => {
                carouselVisualizar.innerHTML += `
                    <div class="carousel-item ${img.mainImage ? "active" : ""}">
                        <img class="object-fit-contain w-100 d-block" src="/images/${img.path}" height="600">
                    </div>
                `;
                carouselIndicadoresVisualizar.innerHTML += `
                    <button type="button" data-bs-target="#carousel-5" data-bs-slide-to="${index}" class="${img.mainImage ? "active" : ""}"></button>
                `;
            });

            modalVisualizar.show();
        }
    });

    function renderizarEstrelas(rating) {
        if (!estrelasContainer) return;
        estrelasContainer.innerHTML = "";
        for (let i = 1; i <= 5; i++) {
            estrelasContainer.innerHTML += `<i class="${i <= rating ? "fas" : "far"} fa-star"></i>`;
        }
    }

    tabelaCorpo.addEventListener("click", async e => {
        if (e.target.closest(".btn-editar")) {
            e.preventDefault();
            const id = e.target.closest(".btn-editar").dataset.id;
            const resposta = await fetch(`/api/products/${id}`);
            const produto = await resposta.json();

            produtoEmEdicaoId = produto.id;

            document.getElementById("nome-produto").value = produto.name;
            document.getElementById("preco-produto").value = produto.price;
            document.getElementById("quantidade-produto").value = produto.stock;
            document.getElementById("descricao-produto").value = produto.description;
            document.getElementById("avaliacao-produto").value = produto.rating;

            imagensSelecionadas = produto.images.map(img => img.path);
            renderizarCarrossel(produto.images, produto.images.find(i => i.mainImage)?.path);
            modalCadastro.show();
        }
    });

    function renderizarCarrossel(imagens, imagemPrincipal = null) {
        carouselCriar.innerHTML = "";
        carouselIndicadoresCriar.innerHTML = "";
        imagens.forEach((img, i) => {
            const nome = typeof img === "string" ? img : img.path;
            const ativa = imagemPrincipal
                ? (nome === imagemPrincipal || img.mainImage)
                : (i === 0);

            carouselCriar.innerHTML += `
                <div class="carousel-item ${ativa ? "active" : ""}">
                    <img class="object-fit-cover w-100 d-block" src="/images/${nome}" height="500">
                </div>
            `;
            carouselIndicadoresCriar.innerHTML += `
                <button type="button" data-bs-target="#carousel-1" data-bs-slide-to="${i}" class="${ativa ? "active" : ""}"></button>
            `;
        });
    }

    inputImagens.addEventListener("change", e => {
        imagensSelecionadas = Array.from(e.target.files);
        renderizarCarrossel(imagensSelecionadas);
    });

    btnSalvarProduto.addEventListener("click", async () => {
        const nome = document.getElementById("nome-produto").value;
        const preco = parseFloat(document.getElementById("preco-produto").value);
        const estoque = parseInt(document.getElementById("quantidade-produto").value);
        const descricao = document.getElementById("descricao-produto").value;
        const avaliacao = parseInt(document.getElementById("avaliacao-produto").value);

        let pathsImagens = [];

        if (imagensSelecionadas.length && imagensSelecionadas[0] instanceof File) {
            const formData = new FormData();
            imagensSelecionadas.forEach(img => formData.append("files", img));

            const respUpload = await fetch("/api/images/upload", {
                method: "POST",
                body: formData
            });
            pathsImagens = await respUpload.json();
        } else {
            pathsImagens = imagensSelecionadas;
        }

        const dto = {
            name: nome,
            price: preco,
            stock: estoque,
            description: descricao,
            rating: avaliacao,
            imagePaths: pathsImagens,
            mainImagePath: pathsImagens[0]
        };

        const url = produtoEmEdicaoId
            ? `/api/products/${produtoEmEdicaoId}`
            : "/api/products";
        const method = produtoEmEdicaoId ? "PUT" : "POST";

        await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(dto)
        });

        modalCadastro.hide();
        carregarProdutos();
    });

    modalCadastroEl.addEventListener("hidden.bs.modal", () => {
        modalCadastroEl.querySelector("form").reset();
        carouselCriar.innerHTML = "";
        carouselIndicadoresCriar.innerHTML = "";
        imagensSelecionadas = [];
        produtoEmEdicaoId = null;
    });

    carregarProdutos();
});