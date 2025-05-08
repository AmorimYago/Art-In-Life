let selectedAddress = null;
let selectedShippingCost = 0;
let selectedShippingText = "";
let selectedPaymentMethod = null;
let cart = [];
let client = null;

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

function getCart() {
    return JSON.parse(localStorage.getItem("shoppingCart")) || [];
}

async function fetchProduct(id) {
    const response = await fetch(`/api/products/${id}`);
    if (!response.ok) throw new Error("Produto não encontrado");
    return await response.json();
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
    if (!cep || cep.length < 8) {
        alert("Digite um CEP válido.");
        return [];
    }

    try {
        const res = await fetch("/api/shipping/calculate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ cep })
        });

        const json = await res.json();
        return Array.isArray(json) ? json : JSON.parse(json);
    } catch (err) {
        console.error("Erro ao calcular frete:", err);
        alert("Erro ao calcular frete.");
        return [];
    }
}

async function finalizarPedido() {
    const cart = getCart();
    if (!cart.length) {
        alert("Seu carrinho está vazio.");
        return;
    }

    const items = [];
    let subtotal = 0;

    for (const item of cart) {
        try {
            const produto = await fetchProduct(item.id);
            const unitPrice = produto.price;
            const quantity = item.quantity;

            items.push({
                productId: item.id,
                quantity: quantity,
                unitPrice: unitPrice
            });

            subtotal += unitPrice * quantity;

        } catch (e) {
            console.error("Erro ao carregar produto:", e);
            alert("Erro ao carregar produto do carrinho.");
            return;
        }
    }

    const freightValue = selectedShippingCost || 0;
    const totalPrice = subtotal + freightValue;

    const paymentDetails = selectedPaymentMethod.includes("Cart") ? {
        cardNumber: document.getElementById("card-number-input").value,
        cardHolderName: document.getElementById("card-name-input").value,
        cvv: document.getElementById("card-codigo-input").value,
        cardExpirationDate: document.getElementById("card-date-input").value,
        installments: parseInt(document.getElementById("card-parcela-select").value)
    } : null;

    const payload = {
        clientId: client.id,
        addressId: selectedAddress.id.toString(),
        paymentMethod: selectedPaymentMethod.includes("Cart") ? "CARD" : "BOLETO",
        paymentDetails: paymentDetails,
        items: items,
        freightValue: freightValue,
        totalPrice: totalPrice
    };

    try {
        const response = await fetch("/api/orders/checkout", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error("Erro ao finalizar pedido.");
        }

        const pedido = await response.json();
        alert("Pedido realizado com sucesso!");
        localStorage.removeItem("shoppingCart");
        window.location.href = `/client/order-confirmation?id=${pedido.id}`;

    } catch (error) {
        console.error("Erro ao enviar pedido:", error);
        alert("Erro ao finalizar pedido. Tente novamente.");
    }
}

function renderResumoPedido() {
    const cart = getCart();
    const tbody = document.querySelector("#card-final tbody");
    const entregaEl = document.querySelector("#card-final p:nth-of-type(1)");
    const pagamentoEl = document.querySelector("#card-final p:nth-of-type(2)");
    const subtotalEl = document.querySelector("#card-final tr:nth-of-type(1) td.text-end");
    const freteEl = document.querySelector("#card-final tr:nth-of-type(2) td.text-end");
    const totalEl = document.querySelector("#card-final tr:nth-of-type(3) td.text-end");

    if (!tbody || !entregaEl || !pagamentoEl || !subtotalEl || !freteEl || !totalEl) return;

    tbody.innerHTML = '';
    let subtotal = 0;

    const render = async () => {
        for (const item of cart) {
            try {
                const produto = await fetchProduct(item.id);
                const total = produto.price * item.quantity;
                subtotal += total;

                const row = document.createElement("tr");
                row.innerHTML = `
                    <td style="min-width: 200px;">
                        <img class="object-fit-cover me-2" style="width: 100px;height: 100px;" src="/images/${produto.images[0]?.path || 'placeholder.jpg'}">
                        ${produto.name}
                    </td>
                    <td class="align-content-center">${item.quantity}</td>
                    <td class="align-content-center">R$ ${produto.price.toFixed(2)}</td>
                    <td class="align-content-center">R$ ${total.toFixed(2)}</td>
                `;
                tbody.appendChild(row);
            } catch (e) {
                console.error("Erro ao renderizar item no resumo:", e);
            }
        }

        subtotalEl.textContent = `R$ ${subtotal.toFixed(2)}`;
        freteEl.textContent = `R$ ${selectedShippingCost.toFixed(2)}`;
        totalEl.textContent = `R$ ${(subtotal + selectedShippingCost).toFixed(2)}`;
        entregaEl.textContent = `${selectedAddress.street}, ${selectedAddress.number} - ${selectedAddress.neighborhood}, ${selectedAddress.city}/${selectedAddress.state} - CEP: ${selectedAddress.cep}`;
        pagamentoEl.textContent = selectedPaymentMethod;
    };

    render();
}

document.addEventListener("DOMContentLoaded", async () => {
    showStep("card-enderecos");
    cart = getCart();
    client = await getLoggedClient();
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

    document.querySelector("#card-final .btn-primary").onclick = () => finalizarPedido();
});
