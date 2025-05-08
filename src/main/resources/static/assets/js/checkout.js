let selectedAddress = null;
let selectedShippingCost = 0;
let selectedShippingText = "";
let selectedPaymentMethod = null;
let cart = [];

const steps = [
    "card-enderecos",
    "card-novo-endereco",
    "card-frete",
    "card-pagamento",
    "card-cartao",
    "card-final"
];

function showStep(idToShow) {
    steps.forEach(id => {
        document.getElementById(id).style.display = id === idToShow ? "block" : "none";
    });
}

async function getLoggedClient() {
    const res = await fetch("/api/client/me");
    const data = await res.json();
    const email = data.email;
    const clientRes = await fetch(`/api/clients/email/${email}`);
    return await clientRes.json();
}

async function loadAddresses(clientId) {
    const res = await fetch(`/api/clients/${clientId}/addresses`);
    const addresses = await res.json();

    const container = document.querySelector("#card-enderecos .btn-group-vertical");
    container.innerHTML = "";

    addresses.forEach(addr => {
        const radioId = `address-${addr.id}`;
        const input = document.createElement("input");
        input.type = "radio";
        input.className = "btn-check";
        input.name = "deliveryAddress";
        input.id = radioId;
        input.autocomplete = "off";
        input.value = addr.id;

        const label = document.createElement("label");
        label.className = "form-label btn btn-outline-primary mb-1 text-start";
        label.setAttribute("for", radioId);
        label.innerHTML = `${addr.street}, ${addr.number} - ${addr.neighborhood}, ${addr.city}/${addr.state} - CEP: ${addr.cep}`;

        container.appendChild(input);
        container.appendChild(label);
    });
}

async function submitNewAddress(clientId) {
    const dto = {
        cep: document.getElementById("cep-input").value,
        street: document.getElementById("street-input").value,
        number: document.getElementById("number-input").value,
        complement: document.getElementById("complement-input").value,
        neighborhood: document.getElementById("neighborhood-input").value,
        city: document.getElementById("city-input").value,
        state: document.getElementById("state-select").value,
        billingAddress: false,
        defaultDeliveryAddress: true
    };

    const res = await fetch(`/api/clients/${clientId}/addresses`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto)
    });
    return await res.json();
}

async function calcularFrete(cep) {
    const res = await fetch("/api/shipping/calculate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ cep })
    });
    return await res.json();
}

async function renderResumoPedido() {
    const tbody = document.querySelector("#card-final table tbody");
    const rows = Array.from(tbody.querySelectorAll("tr"));
    const infoRows = rows.slice(-3);
    rows.slice(0, -3).forEach(row => row.remove());

    let subtotal = 0;

    const enderecoEl = document.querySelector("#card-final p:nth-of-type(1)");
    const formaPagamentoEl = document.querySelector("#card-final p:nth-of-type(2)");
    const subtotalEl = infoRows[0].querySelector("td.text-end");
    const freteEl = infoRows[1].querySelector("td.text-end");
    const totalEl = infoRows[2].querySelector("td.text-end");

    for (const item of cart) {
        try {
            const product = await fetch(`/api/products/${item.id}`).then(r => r.ok ? r.json() : null);
            if (!product) continue;

            const total = product.price * item.quantity;
            subtotal += total;

            const row = document.createElement("tr");
            const imagem = product.images?.[0]?.path || "placeholder.jpg";
            row.innerHTML = `
                <td style="min-width: 200px;"><img class="object-fit-cover me-2" src="/images/${imagem}" style="width: 100px;height: 100px;">${product.name}</td>
                <td>${item.quantity}</td>
                <td>R$ ${product.price.toFixed(2)}</td>
                <td>R$ ${total.toFixed(2)}</td>
            `;
            tbody.insertBefore(row, infoRows[0]);
        } catch (e) {
            console.error("Erro ao renderizar produto no resumo:", e);
        }
    }

    subtotalEl.textContent = `R$ ${subtotal.toFixed(2)}`;
    freteEl.textContent = `R$ ${selectedShippingCost.toFixed(2)}`;
    totalEl.textContent = `R$ ${(subtotal + selectedShippingCost).toFixed(2)}`;
    enderecoEl.textContent = `Rua ${selectedAddress.street}, ${selectedAddress.number} - ${selectedAddress.neighborhood}, ${selectedAddress.city}/${selectedAddress.state} - CEP: ${selectedAddress.cep}`;
    formaPagamentoEl.textContent = selectedPaymentMethod;
}

async function syncCartWithBackend() {
    const cart = getCart();
    if (!cart.length) {
        console.warn("Carrinho local vazio.");
        return;
    }

    const res = await fetch("/api/client/me");
    if (!res.ok) {
        alert("Você precisa estar logado para finalizar a compra.");
        return;
    }

    const payload = cart.map(item => ({
        productId: item.id,
        quantity: item.quantity
    }));

    const syncRes = await fetch("/api/cart/sync", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        credentials: "include" // <- ESSENCIAL para enviar o cookie de sessão
    });


    if (!syncRes.ok) {
        alert("Erro ao sincronizar o carrinho com o servidor.");
        throw new Error("Erro na sincronização do carrinho");
    }

    console.log("Carrinho sincronizado com sucesso.");
}

async function finalizarPedido(clientId) {
    await syncCartWithBackend();

    const paymentDetails = selectedPaymentMethod === "Cartão de crédito" ? {
        cardNumber: document.getElementById("card-number-input").value,
        cardHolderName: document.getElementById("card-name-input").value,
        cvv: document.getElementById("card-codigo-input").value,
        cardExpirationDate: document.getElementById("card-date-input").value,
        installments: parseInt(document.getElementById("card-parcela-select").value)
    } : {};

    const res = await fetch(`/api/orders/checkout`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ address: selectedAddress, paymentDetails })
    });

    if (res.ok) {
        localStorage.removeItem("shoppingCart");
        alert("Pedido finalizado com sucesso!");
        window.location.href = "/client/orders";
    } else {
        alert("Erro ao finalizar pedido.");
    }
}

document.addEventListener("DOMContentLoaded", async () => {
    showStep("card-enderecos");
    cart = getCart();
    const client = await getLoggedClient();
    await loadAddresses(client.id);

    document.querySelector("#card-enderecos .btn-group button.btn-primary").onclick = async () => {
        const selected = document.querySelector("input[name='deliveryAddress']:checked");
        const res = await fetch(`/api/client-addresses/client/${client.id}`);
        const addresses = await res.json();
        selectedAddress = addresses.find(a => a.id == selected.value);
        showStep("card-frete");

        const fretes = await calcularFrete(selectedAddress.cep);
        const ul = document.createElement("ul");
        ul.classList.add("list-group");

        fretes.forEach(frete => {
            const li = document.createElement("li");
            li.className = "list-group-item d-flex justify-content-between align-items-center";
            li.innerHTML = `
                <div>
                    <img src="${frete.company.picture}" style="height: 24px;" class="me-2">
                    <strong>${frete.name}</strong><br>
                    <small>${frete.delivery_time} dia(s)</small>
                </div>
                <button class="btn btn-outline-success btn-sm">R$ ${parseFloat(frete.custom_price).toFixed(2)}</button>
            `;
            li.querySelector("button").onclick = () => {
                selectedShippingCost = parseFloat(frete.custom_price);
                selectedShippingText = frete.name;
                showStep("card-pagamento");
            };
            ul.appendChild(li);
        });
        document.querySelector("#card-frete").appendChild(ul);
    };

    document.querySelector("#card-enderecos .btn-group button.btn-light").onclick = () => showStep("card-novo-endereco");

    document.querySelector("#cep-input").addEventListener("blur", async () => {
        const cep = document.getElementById("cep-input").value.replace(/\D/g, "");
        if (cep.length !== 8) return;

        const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
        const data = await res.json();
        if (!data.erro) {
            document.getElementById("street-input").value = data.logradouro || "";
            document.getElementById("neighborhood-input").value = data.bairro || "";
            document.getElementById("city-input").value = data.localidade || "";
            document.getElementById("state-select").value = data.uf || "";
        }
    });

    document.querySelector("#card-novo-endereco .btn-primary").onclick = async () => {
        selectedAddress = await submitNewAddress(client.id);
        showStep("card-frete");
    };

    const cards = document.querySelectorAll("#card-pagamento .card");
    cards.forEach(card => {
        card.onclick = () => {
            selectedPaymentMethod = card.querySelector("h4").textContent;
            showStep(selectedPaymentMethod.includes("Cart") ? "card-cartao" : "card-final");
            if (!selectedPaymentMethod.includes("Cart")) renderResumoPedido();
        };
    });

    document.querySelector("#card-cartao .btn-primary").onclick = () => {
        renderResumoPedido();
        showStep("card-final");
    };

    document.querySelector("#card-novo-endereco .btn-light").onclick = () => showStep("card-enderecos");
    document.querySelector("#card-frete .btn-light").onclick = () => showStep("card-enderecos");
    document.querySelector("#card-pagamento .btn-light").onclick = () => showStep("card-frete");
    document.querySelector("#card-cartao .btn-light").onclick = () => showStep("card-pagamento");
    document.querySelector("#card-final .btn-light").onclick = () => {
        showStep(selectedPaymentMethod.includes("Cart") ? "card-cartao" : "card-pagamento");
    };

    document.querySelector("#card-final .btn-primary").onclick = () => finalizarPedido(client.id);
});
