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

        if (selectedAddress && selectedAddress.id === addr.id) {
            input.checked = true;
        } else if (!selectedAddress && container.children.length === 0) {
            input.checked = true;
            selectedAddress = addr;
        }


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
        // Verifica se a resposta é uma string JSON e tenta fazer o parse
        if (typeof json === 'string') {
            return JSON.parse(json);
        }
        return Array.isArray(json) ? json : []; // Garante que retorne um array vazio se não for array
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

    if (!selectedAddress) {
        alert("Por favor, selecione ou adicione um endereço de entrega.");
        return;
    }

    // Verifica se selectedShippingCost é 0 ou um valor válido (diferente de null/undefined)
    if (selectedShippingCost === null || selectedShippingCost === undefined) {
        alert("Por favor, selecione uma opção de frete.");
        return;
    }

    if (!selectedPaymentMethod) {
        alert("Por favor, selecione uma forma de pagamento.");
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

    const freightValue = selectedShippingCost;
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
    // Usando os novos IDs para seleção
    const entregaEl = document.getElementById("resumo-entrega");
    const pagamentoEl = document.getElementById("resumo-pagamento");
    const subtotalEl = document.querySelector("#card-final tr:nth-of-type(1) td.text-end");
    const freteEl = document.querySelector("#card-final tr:nth-of-type(2) td.text-end");
    const totalEl = document.querySelector("#card-final tr:nth-of-type(3) td.text-end");

    // Garante que todos os elementos sejam encontrados antes de prosseguir
    if (!tbody || !entregaEl || !pagamentoEl || !subtotalEl || !freteEl || !totalEl) {
        console.error("Um ou mais elementos para o resumo do pedido não foram encontrados. Verifique os IDs no HTML.");
        return;
    }

    tbody.innerHTML = '';
    let subtotal = 0;

    const render = async () => {
        for (const item of cart) {
            try {
                const produto = await fetchProduct(item.id);
                const total = produto.price * item.quantity;
                subtotal += total;

                const row = document.createElement("tr");
                const imageUrl = produto.images && produto.images.length > 0
                    ? `/images/${produto.images[0].path}`
                    : 'assets/img/placeholder.jpg';

                row.innerHTML = `
                    <td style="min-width: 200px;">
                        <img class="object-fit-cover me-2" style="width: 100px;height: 100px;" src="${imageUrl}">
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

        // Verifica se selectedAddress não é nulo antes de acessar suas propriedades
        if (selectedAddress) {
            entregaEl.textContent = `${selectedAddress.street}, ${selectedAddress.number} - ${selectedAddress.neighborhood}, ${selectedAddress.city}/${selectedAddress.state} - CEP: ${selectedAddress.cep}`;
        } else {
            entregaEl.textContent = "Nenhum endereço selecionado.";
        }

        // Verifica se selectedPaymentMethod não é nulo
        if (selectedPaymentMethod) {
            pagamentoEl.textContent = selectedPaymentMethod;
        } else {
            pagamentoEl.textContent = "Nenhuma forma de pagamento selecionada.";
        }
    };

    render();
}

document.addEventListener("DOMContentLoaded", async () => {
    showStep("card-enderecos");
    cart = getCart();
    client = await getLoggedClient();
    await loadAddresses(client.id);

    // Inicialização do selectedAddress após carregar os endereços
    // Se não houver um endereço selecionado explicitamente, e houver endereços, selecione o primeiro.
    const currentCheckedAddress = document.querySelector("input[name='deliveryAddress']:checked");
    if (currentCheckedAddress) {
        const addressesRes = await fetch(`/api/client-addresses/client/${client.id}`);
        const addresses = await addressesRes.json();
        selectedAddress = addresses.find(a => a.id == currentCheckedAddress.value);
    } else {
        const addressesRes = await fetch(`/api/clients/${client.id}/addresses`);
        const addresses = await addressesRes.json();
        if (addresses.length > 0) {
            selectedAddress = addresses[0];
            // Certifique-se de que o radio button correspondente ao primeiro endereço seja marcado
            const firstAddressRadio = document.getElementById(`address-${selectedAddress.id}`);
            if (firstAddressRadio) {
                firstAddressRadio.checked = true;
            }
        }
    }


    document.querySelector("#card-enderecos .btn-group button.btn-primary").onclick = async () => {
        const selected = document.querySelector("input[name='deliveryAddress']:checked");
        if (selected) {
            const addressesRes = await fetch(`/api/client-addresses/client/${client.id}`);
            const addresses = await addressesRes.json();
            selectedAddress = addresses.find(a => a.id == selected.value);
            showStep("card-frete");

            // Limpa o conteúdo anterior de frete
            const shippingContainer = document.querySelector("#card-frete .card-body .row:nth-of-type(2) .col"); // Seleciona o contêiner correto para o ul
            shippingContainer.innerHTML = '';

            const fretes = await calcularFrete(selectedAddress.cep);
            const ul = document.createElement("ul");
            ul.classList.add("list-group");

            if (fretes.length === 0) {
                const li = document.createElement("li");
                li.className = "list-group-item";
                li.textContent = "Nenhuma opção de frete disponível para este CEP.";
                ul.appendChild(li);
            } else {
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
            }
            shippingContainer.appendChild(ul);
        } else {
            alert("Por favor, selecione um endereço de entrega ou adicione um novo.");
        }
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
        // Recalcula e exibe as opções de frete para o novo endereço
        const shippingContainer = document.querySelector("#card-frete .card-body .row:nth-of-type(2) .col");
        shippingContainer.innerHTML = ''; // Limpa o conteúdo anterior

        const fretes = await calcularFrete(selectedAddress.cep);
        const ul = document.createElement("ul");
        ul.classList.add("list-group");

        if (fretes.length === 0) {
            const li = document.createElement("li");
            li.className = "list-group-item";
            li.textContent = "Nenhuma opção de frete disponível para este CEP.";
            ul.appendChild(li);
        } else {
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
        }
        shippingContainer.appendChild(ul);
    };

    const cards = document.querySelectorAll("#card-pagamento .card");
    cards.forEach(card => {
        card.onclick = () => {
            selectedPaymentMethod = card.querySelector("h4").textContent;
            showStep(selectedPaymentMethod.includes("Cart") ? "card-cartao" : "card-final");
            // Renderiza o resumo imediatamente se não for cartão
            if (!selectedPaymentMethod.includes("Cart")) {
                renderResumoPedido();
            }
        };
    });

    document.querySelector("#card-cartao .btn-primary").onclick = () => {
        // Renderiza o resumo antes de mostrar o passo final do cartão
        renderResumoPedido();
        showStep("card-final");
    };

    document.querySelector("#card-novo-endereco .btn-light").onclick = () => showStep("card-enderecos");
    document.querySelector("#card-frete .btn-light").onclick = () => showStep("card-enderecos");
    document.querySelector("#card-pagamento .btn-light").onclick = () => showStep("card-frete");
    document.querySelector("#card-cartao .btn-light").onclick = () => showStep("card-pagamento");
    document.querySelector("#card-final .btn-light").onclick = () => {
        showStep(selectedPaymentMethod && selectedPaymentMethod.includes("Cart") ? "card-cartao" : "card-pagamento");
    };

    document.querySelector("#card-final .btn-primary").onclick = () => finalizarPedido();
});