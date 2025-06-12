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
    let imagensSelecionadas = []; // Pode conter File objects ou strings de path (para edição)
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

        if (typeof window.userIsStocker !== 'undefined' && window.userIsStocker) {
            aplicarRestricoesTabelaStocker();
        }
    }

    function renderizarPaginacao(totalItens) {
        const totalPaginas = Math.ceil(totalItens / itensPorPagina);
        let html = "";

        // Botão "Anterior"
        html += `<li class="page-item ${paginaAtual === 1 ? 'disabled' : ''}">
                    <a class="page-link" href="#" data-pagina="${paginaAtual - 1}">«</a>
                 </li>`;

        for (let i = 1; i <= totalPaginas; i++) {
            html += `<li class="page-item ${i === paginaAtual ? "active" : ""}">
                        <a class="page-link" href="#" data-pagina="${i}">${i}</a>
                     </li>`;
        }

        // Botão "Próximo"
        html += `<li class="page-item ${paginaAtual === totalPaginas ? 'disabled' : ''}">
                    <a class="page-link" href="#" data-pagina="${paginaAtual + 1}">»</a>
                 </li>`;
        paginacao.innerHTML = html;
    }

    paginacao.addEventListener("click", e => {
        e.preventDefault();
        if (e.target.tagName === "A") {
            const novaPagina = parseInt(e.target.dataset.pagina);
            // Verifica se a novaPagina é válida e se não é a página atual
            if (!isNaN(novaPagina) && novaPagina > 0 && novaPagina <= Math.ceil(todosProdutos.length / itensPorPagina) && novaPagina !== paginaAtual) {
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
            try {
                const response = await fetch(url, { method: "PATCH" });
                if (!response.ok) {
                    throw new Error("Falha ao atualizar status.");
                }
                await carregarProdutos(); // Recarrega os produtos para atualizar a tabela
            } catch (error) {
                console.error("Erro ao mudar status:", error);
                alert("Erro ao mudar status do produto.");
                e.target.checked = !e.target.checked; // Reverte o estado do switch se houver erro
            }
        }
    });

    tabelaCorpo.addEventListener("click", async e => {
        if (e.target.closest(".btn-visualizar")) {
            e.preventDefault();
            const id = e.target.closest(".btn-visualizar").dataset.id;
            const resposta = await fetch(`/api/products/${id}`);
            const produto = await resposta.json();

            document.querySelector("#modal-mostrar-produto h1").textContent = produto.name;
            document.querySelector("#modal-mostrar-produto h5").textContent = `Código: ${produto.id}`;
            document.querySelector("#modal-mostrar-produto p").textContent = produto.description;
            document.querySelector("#modal-mostrar-produto h3").textContent = "R$ " + (produto.price ? produto.price.toFixed(2) : '0.00');
            renderizarEstrelas(produto.rating);

            carouselVisualizar.innerHTML = "";
            carouselIndicadoresVisualizar.innerHTML = "";

            if (!produto.images || produto.images.length === 0) {
                // Se não há imagens, mostre um placeholder
                carouselVisualizar.innerHTML = `
                    <div class="carousel-item active">
                        <img class="object-fit-contain w-100 d-block" src="https://cdn.bootstrapstudio.io/placeholders/1400x800.png" alt="Sem Imagem" height="600">
                    </div>
                `;
                carouselIndicadoresVisualizar.innerHTML = `<button type="button" data-bs-target="#carousel-5" data-bs-slide-to="0" class="active"></button>`;
            } else {
                produto.images.forEach((img, index) => {

                    const imageUrl = `/images/${img.path || 'placeholder.jpg'}`;

                    // A primeira imagem é ativa por padrão, ou a que tem isPrimary true
                    const isActive = img.isPrimary || (index === 0 && !produto.images.some(i => i.isPrimary));

                    carouselVisualizar.innerHTML += `
                        <div class="carousel-item ${isActive ? "active" : ""}">
                            <img class="object-fit-contain w-100 d-block" src="${imageUrl}" alt="${produto.name}" height="600">
                        </div>
                    `;
                    carouselIndicadoresVisualizar.innerHTML += `
                        <button type="button" data-bs-target="#carousel-5" data-bs-slide-to="${index}" class="${isActive ? "active" : ""}"></button>
                    `;
                });
            }
            modalVisualizar.show();
        }
    });

    function renderizarEstrelas(rating) {
        if (!estrelasContainer) return;
        estrelasContainer.innerHTML = "";
        const numericRating = parseFloat(rating);
        for (let i = 1; i <= 5; i++) {
            estrelasContainer.innerHTML += `<i class="${i <= numericRating ? "fas" : "far"} fa-star"></i>`;
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

    // Função original renderizarCarrossel (para criação/edição com base em paths/ProductImage objects)
    // Usada para o carrossel de criação/edição
    function renderizarCarrossel(imagens, imagemPrincipal = null) {
        carouselCriar.innerHTML = "";
        carouselIndicadoresCriar.innerHTML = "";

        if (!imagens || imagens.length === 0) {
            carouselCriar.innerHTML = `
                <div class="carousel-item active">
                    <img class="object-fit-cover w-100 d-block" src="https://cdn.bootstrapstudio.io/placeholders/1400x800.png" alt="Slide Image" width="433" height="500">
                </div>
            `;
            carouselIndicadoresCriar.innerHTML = `<button type="button" data-bs-target="#carousel-1" data-bs-slide-to="0" class="active"></button>`;
            return;
        }

        imagens.forEach((img, i) => {
            let imageUrl;

            const nomeDoArquivo = typeof img === "string" ? img : img.path;

            if (img instanceof File) {
                // Se for um objeto File (nova imagem selecionada), cria uma URL de objeto para preview
                imageUrl = URL.createObjectURL(img);
            } else {
                // Se for uma string (path de imagem existente) ou ProductImage object, constrói a URL
                // Usa '/images/' + path para imagens vindas do backend
                imageUrl = `/images/${nomeDoArquivo || 'placeholder.jpg'}`;
            }

            // Ativação da imagem principal para edição/criação
            const ativa = imagemPrincipal
                ? (typeof img === "string" ? nomeDoArquivo === imagemPrincipal : (img.path === imagemPrincipal || img.isPrimary))
                : (i === 0); // Ativa o primeiro se não houver principal ou se for o primeiro item

            carouselCriar.innerHTML += `
                <div class="carousel-item ${ativa ? "active" : ""}">
                    <img class="object-fit-cover w-100 d-block" src="${imageUrl}" alt="Imagem do Produto" height="500">
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

        let pathsImagensParaEnvio = [];

        const newFilesToUpload = imagensSelecionadas.filter(item => item instanceof File);

        if (newFilesToUpload.length > 0) {
            const formData = new FormData();
            newFilesToUpload.forEach(img => formData.append("files", img));

            try {
                const respUpload = await fetch("/api/images/upload", {
                    method: "POST",
                    body: formData
                });
                if (!respUpload.ok) {
                    throw new Error("Falha ao fazer upload das novas imagens.");
                }
                const uploadedPaths = await respUpload.json();
                pathsImagensParaEnvio.push(...uploadedPaths);
            } catch (error) {
                console.error("Erro no upload de imagens:", error);
                alert("Erro ao enviar novas imagens. Verifique o console.");
                return;
            }
        }

        const existingImagePaths = imagensSelecionadas
            .filter(item => typeof item === "string");

        pathsImagensParaEnvio.push(...existingImagePaths);


        let mainPathForDto = null;
        if (pathsImagensParaEnvio.length > 0) {
            // A imagem principal será a primeira da lista final (nova ou existente)
            mainPathForDto = pathsImagensParaEnvio[0];
        }

        const dto = {
            name: nome,
            price: preco,
            stock: estoque,
            description: descricao,
            rating: avaliacao,
            imagePaths: pathsImagensParaEnvio, // Envia todos os paths (novos + existentes)
            mainImagePath: mainPathForDto // Define a primeira como principal
        };

        const url = produtoEmEdicaoId
            ? `/api/products/${produtoEmEdicaoId}`
            : "/api/products";
        const method = produtoEmEdicaoId ? "PUT" : "POST";

        try {
            const respProduct = await fetch(url, {
                method,
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });

            if (!respProduct.ok) {
                const errorData = await respProduct.json();
                throw new Error(errorData.message || "Falha ao salvar o produto.");
            }

            modalCadastro.hide();
            carregarProdutos();
        } catch (error) {
            console.error("Erro ao salvar produto:", error);
            if (error.message && error.message.includes("constraint")) {
                alert("Erro de validação: " + error.message + ". Verifique se todos os campos obrigatórios estão preenchidos corretamente.");
            } else {
                alert("Erro ao salvar produto. Verifique o console para mais detalhes.");
            }
        }
    });

    modalCadastroEl.addEventListener("hidden.bs.modal", () => {
        modalCadastroEl.querySelector("form").reset();
        carouselCriar.innerHTML = "";
        carouselIndicadoresCriar.innerHTML = "";
        imagensSelecionadas.forEach(file => {
            if (file instanceof File) {
                URL.revokeObjectURL(URL.createObjectURL(file));
            }
        });
        imagensSelecionadas = [];
        produtoEmEdicaoId = null;

        renderizarCarrossel([]);
    });

    renderizarCarrossel([]);

    carregarProdutos();
});