const CART_KEY = 'shoppingCart';

function getCart() {
    return JSON.parse(localStorage.getItem(CART_KEY)) || [];
}

function saveCart(cart) {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
}

async function fetchProduct(id) {
    const response = await fetch(`/api/products/${id}`);
    if (!response.ok) throw new Error("Produto não encontrado");
    return await response.json();
}

window.addToCart = async function (productId, quantity = 1) {
    try {
        const product = await fetchProduct(productId);
        const cart = getCart();
        const item = cart.find(i => i.id === productId);

        const atual = item ? item.quantity : 0;
        const novaQuantidade = atual + quantity;

        if (novaQuantidade > product.stock) {
            alert(`Estoque insuficiente. Apenas ${product.stock} unidade(s) disponível(is).`);
            return;
        }

        if (item) {
            item.quantity = novaQuantidade;
        } else {
            cart.push({ id: productId, quantity });
        }

        saveCart(cart);
        renderCartDropdown();
        updateCartBadge();
    } catch (e) {
        console.error("Erro ao adicionar ao carrinho:", e);
        alert("Erro ao adicionar produto ao carrinho.");
    }
};

window.removeFromCart = function (productId) {
    const cart = getCart().filter(item => item.id !== productId);
    saveCart(cart);
    renderCartDropdown();
    updateCartBadge();
    if (typeof renderCartPage === "function") renderCartPage();
};

window.updateQuantity = async function (productId, quantity) {
    if (quantity < 1) return;

    try {
        const product = await fetchProduct(productId);
        if (quantity > product.stock) {
            alert(`Estoque insuficiente. Apenas ${product.stock} unidade(s) disponível(is).`);
            return;
        }

        const cart = getCart();
        const item = cart.find(i => i.id === productId);
        if (item) {
            item.quantity = quantity;
            saveCart(cart);
            renderCartDropdown();
            updateCartBadge();
            if (typeof renderCartPage === "function") renderCartPage();
        }
    } catch (e) {
        console.error("Erro ao atualizar quantidade:", e);
        alert("Erro ao atualizar produto no carrinho.");
    }
};

async function renderCartDropdown() {
    const cart = getCart();
    const tbody = document.querySelector('.dropdown-menu table tbody');
    const subtotalEl = document.querySelector('.dropdown-menu p span');
    if (!tbody || !subtotalEl) return;

    tbody.innerHTML = '';
    let subtotal = 0;
    const cartFiltrado = [];

    for (const item of cart) {
        try {
            const produto = await fetchProduct(item.id);
            cartFiltrado.push(item);
            const total = produto.price * item.quantity;
            subtotal += total;
            const imagem = produto.images.length > 0
                ? `/images/${produto.images[0].path}`
                : "assets/img/placeholder.jpg";

            const row = document.createElement("tr");
            row.innerHTML = `
                <td><img src="${imagem}" style="width: 50px; height: 50px;"></td>
                <td style="min-width: 200px;">${produto.name}</td>
                <td>R$ ${produto.price.toFixed(2)}</td>
                <td>x${item.quantity}</td>
                <td><a href="#" onclick="removeFromCart('${item.id}')"><i class="fas fa-trash"></i></a></td>
            `;
            tbody.appendChild(row);
        } catch (e) {
            console.warn(`Produto ${item.id} removido do carrinho (não encontrado).`);
        }
    }

    saveCart(cartFiltrado);
    subtotalEl.textContent = `R$ ${subtotal.toFixed(2)}`;
}

window.renderCartPage = async function () {
    const cart = getCart();
    const tbody = document.querySelector("#cart-table tbody");
    const subtotalEl = document.getElementById("cart-subtotal");
    const totalEl = document.getElementById("cart-total");

    if (!tbody || !subtotalEl || !totalEl) return;

    tbody.innerHTML = '';
    let subtotal = 0;
    const cartFiltrado = [];

    for (const item of cart) {
        try {
            const produto = await fetchProduct(item.id);
            cartFiltrado.push(item);
            const total = produto.price * item.quantity;
            subtotal += total;

            const imagem = produto.images.length > 0
                ? `/images/${produto.images[0].path}`
                : "assets/img/placeholder.jpg";

            const row = document.createElement("tr");
            row.innerHTML = `
                <td><img src="${imagem}" style="width: 100px; height: 100px;" class="me-2"> ${produto.name}</td>
                <td>
                    <div class="input-group input-group-sm" style="max-width: 130px;">
                        <button class="btn btn-light" onclick="updateQuantity('${item.id}', ${item.quantity - 1})">-</button>
                        <input class="form-control text-center" type="text" value="${item.quantity}" readonly style="width: 40px;">
                        <button class="btn btn-primary" onclick="updateQuantity('${item.id}', ${item.quantity + 1})">+</button>
                    </div>
                </td>
                <td>R$ ${produto.price.toFixed(2)}</td>
                <td>R$ ${(produto.price * item.quantity).toFixed(2)}</td>
                <td><a href="#" onclick="removeFromCart('${item.id}')"><i class="fas fa-trash"></i></a></td>
            `;
            tbody.appendChild(row);
        } catch (e) {
            console.warn(`Produto ${item.id} removido do carrinho (não encontrado).`);
        }
    }

    saveCart(cartFiltrado);
    subtotalEl.textContent = `R$ ${subtotal.toFixed(2)}`;

    const freteEl = document.getElementById("frete-valor");
    const frete = freteEl
        ? parseFloat(freteEl.textContent.replace("R$", "").replace(",", ".").trim()) || 0
        : 0;

    totalEl.textContent = `R$ ${(subtotal + frete).toFixed(2)}`;
};

function updateCartBadge() {
    const cart = getCart();
    const badgeId = "cart-count";
    const count = cart.length;

    let icon = document.getElementById("cart");
    if (!icon) return;

    let badge = document.getElementById(badgeId);
    if (!badge) {
        badge = document.createElement("span");
        badge.id = badgeId;
        badge.className = "position-absolute translate-middle badge rounded-pill bg-danger";
        badge.style.top = "10px";
        badge.style.right = "8px";
        badge.style.fontSize = "0.7rem";
        badge.style.zIndex = "1000";
        badge.style.pointerEvents = "none";
        badge.style.position = "absolute";

        icon.parentElement.style.position = "relative";
        icon.parentElement.appendChild(badge);
    }

    badge.textContent = count;
    badge.style.display = count > 0 ? "inline-block" : "none";
}

window.calcularFrete = async function () {
    const cep = document.getElementById("cep").value.trim();
    if (!cep || cep.length < 8) {
        alert("Digite um CEP válido.");
        return;
    }

    try {
        const res = await fetch("/api/shipping/calculate", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ cep })
        });

        const result = await res.json();
        const ul = document.getElementById("frete-opcoes");
        ul.innerHTML = "";

        result.forEach(frete => {
            if (frete.error) return;

            const li = document.createElement("li");
            li.className = "list-group-item d-flex justify-content-between align-items-center";

            li.innerHTML = `
                <div>
                    <img src="${frete.company.picture}" style="height: 24px;" class="me-2">
                    <strong>${frete.name}</strong><br>
                    <small>${frete.delivery_time} dia(s)</small>
                </div>
                <button class="btn btn-outline-success btn-sm" onclick="selecionarFrete(${frete.custom_price})">
                    R$ ${parseFloat(frete.custom_price).toFixed(2)}
                </button>
            `;

            ul.appendChild(li);
        });
    } catch (err) {
        console.error("Erro ao calcular frete:", err);
        alert("Erro ao calcular frete.");
    }
};

window.selecionarFrete = function (valor) {
    document.getElementById("frete-valor").textContent = `R$ ${parseFloat(valor).toFixed(2)}`;
    renderCartPage();
};

document.addEventListener("DOMContentLoaded", () => {
    renderCartDropdown();
    updateCartBadge();
    if (document.body.classList.contains("cart-page")) {
        renderCartPage();
    }
});

async function syncCartWithBackend() {
    const cart = getCart(); // [{ id, quantity }]
    if (!cart.length) return;

    // Converte o campo 'id' para 'productId'
    const payload = cart.map(item => ({
        productId: item.id,
        quantity: item.quantity
    }));

    try {
        const response = await fetch("/api/cart/sync", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error("Falha ao sincronizar carrinho");
        }

        console.log("Carrinho sincronizado com o backend.");
    } catch (e) {
        console.error("Erro ao sincronizar carrinho:", e);
        alert("Erro ao sincronizar carrinho. Tente novamente.");
    }
}

