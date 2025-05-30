document.addEventListener("DOMContentLoaded", async () => {

    async function loadOrders() {
        try {
            const ordersResp = await fetch('/api/orders');
            if (!ordersResp.ok) throw new Error('Erro ao carregar pedidos do cliente.');

            const orders = await ordersResp.json();
            renderOrders(orders);
        } catch (err) {
            console.error(err);
            alert(err.message);
        }
    }

    function renderOrders(orders) {
        const container = document.getElementById("orders-list");
        container.innerHTML = '';

        if (orders.length === 0) {
            container.innerHTML = `<p>Nenhum pedido encontrado.</p>`;
            return;
        }

        orders.forEach(order => {
            const card = document.createElement("div");
            card.className = "card mb-3";
            card.innerHTML = `
                <div class="card-body">
                    <h4>Pedido nº ${order.id}</h4>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Data</th>
                                    <th>Valor total</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>${new Date(order.orderDate).toLocaleString('pt-BR')}</td>
                                    <td>R$ ${order.totalPrice.toFixed(2)}</td>
                                    <td>${formatStatus(order.status)}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <button class="btn btn-primary details-btn" data-order-id="${order.id}">
                        Mais detalhes
                    </button>
                </div>
            `;
            container.appendChild(card);
        });
    }

    async function openOrderDetails(orderId) {
        try {
            const resp = await fetch(`/api/orders/${orderId}`);
            if (!resp.ok) throw new Error("Erro ao buscar detalhes do pedido.");

            const order = await resp.json();

            // Preencher tabela de itens
            const tbody = document.querySelector("#modal-1 tbody");
            tbody.innerHTML = "";
            order.items.forEach(item => {
                const totalItem = item.price * item.quantity;
                const row = `
                    <tr>
                        <td style="min-width: 200px;">
                            <img class="object-fit-cover me-2" src="/images/${item.product.mainImage}" style="width: 100px; height: 100px;">
                            ${item.product.name}
                        </td>
                        <td class="align-content-center">${item.quantity}</td>
                        <td class="align-content-center">R$ ${item.price.toFixed(2)}</td>
                        <td class="align-content-center">R$ ${totalItem.toFixed(2)}</td>
                    </tr>
                `;
                tbody.insertAdjacentHTML('beforeend', row);
            });

            // Dados de entrega
            document.querySelector('#modal-1 p.delivery').innerText = `
                ${order.shippingAddress.street}, ${order.shippingAddress.number}, 
                ${order.shippingAddress.city} - ${order.shippingAddress.state}, 
                CEP: ${order.shippingAddress.postalCode}
            `;

            // Forma de pagamento
            document.querySelector('#modal-1 p.payment').innerText = order.paymentMethod;

            // Totais
            const totalTable = document.querySelectorAll('#modal-1 .table-bordered td.text-end');
            totalTable[0].innerText = `R$ ${order.subtotal.toFixed(2)}`;
            totalTable[1].innerText = `R$ ${order.shippingPrice.toFixed(2)}`;
            totalTable[2].innerText = `R$ ${order.totalPrice.toFixed(2)}`;

            // Abrir modal
            const modal = new bootstrap.Modal(document.getElementById('modal-1'));
            modal.show();

        } catch (err) {
            console.error(err);
            alert(err.message);
        }
    }

    function formatStatus(status) {
        switch (status) {
            case 'AGUARDANDO_PAGAMENTO': return 'Aguardando pagamento';
            case 'PAGO': return 'Pago';
            case 'ENVIADO': return 'Enviado';
            case 'ENTREGUE': return 'Entregue';
            case 'CANCELADO': return 'Cancelado';
            default: return status;
        }
    }

    // Event listener para os botões "Mais detalhes"
    document.addEventListener("click", function (e) {
        if (e.target.classList.contains("details-btn")) {
            const orderId = e.target.getAttribute("data-order-id");
            openOrderDetails(orderId);
        }
    });

    await loadOrders();
});
