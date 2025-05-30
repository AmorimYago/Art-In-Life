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
            console.log("Detalhes do Pedido Recebidos (para depuração):", order); // MUITO IMPORTANTE AQUI!

            // Preencher tabela de itens
            const tbody = document.querySelector("#modal-1 tbody");
            tbody.innerHTML = "";

            if (order && Array.isArray(order.items)) {
                order.items.forEach(item => {
                    // Verificações para item.price e item.quantity
                    const itemPrice = item.unitPrice !== undefined && item.unitPrice !== null ? item.unitPrice : 0; // Use unitPrice como no OrderItem.java
                    const itemQuantity = item.quantity !== undefined && item.quantity !== null ? item.quantity : 0;
                    const totalItem = itemPrice * itemQuantity;

                    // Verificações para item.product
                    const productName = item.product && item.product.name ? item.product.name : 'Produto Desconhecido';
                    const imageUrl = (item.product && item.product.mainImage) ? `/images/${item.product.mainImage}` : 'assets/img/placeholder.jpg';

                    const row = `
                    <tr>
                        <td style="min-width: 200px;">
                            <img class="object-fit-cover me-2" src="${imageUrl}" style="width: 100px; height: 100px;">
                            ${productName}
                        </td>
                        <td class="align-content-center">${itemQuantity}</td>
                        <td class="align-content-center">R$ ${itemPrice.toFixed(2)}</td>
                        <td class="align-content-center">R$ ${totalItem.toFixed(2)}</td>
                    </tr>
                `;
                    tbody.insertAdjacentHTML('beforeend', row);
                });
            } else {
                console.warn("A propriedade 'items' não foi encontrada ou não é um array válido no objeto do pedido:", order);
                tbody.innerHTML = '<tr><td colspan="4">Nenhum item encontrado para este pedido.</td></tr>';
            }


            // Dados de entrega
            const deliveryAddressEl = document.querySelector('#modal-1 p.delivery');
            if (deliveryAddressEl && order.address) { // Usar 'address' conforme Order.java e OrderResponseDTO.java
                deliveryAddressEl.innerText = `
                ${order.address.street || ''}, ${order.address.number || ''},
                ${order.address.city || ''} - ${order.address.state || ''},
                CEP: ${order.address.cep || ''}
            `;
            } else if (deliveryAddressEl) {
                deliveryAddressEl.innerText = "Endereço de entrega não disponível.";
            }


            // Forma de pagamento
            const paymentMethodEl = document.querySelector('#modal-1 p.payment');
            if (paymentMethodEl && order.paymentMethod) {
                paymentMethodEl.innerText = order.paymentMethod;
            } else if (paymentMethodEl) {
                paymentMethodEl.innerText = "Forma de pagamento não disponível.";
            }


            // Totais
            const totalTableCells = document.querySelectorAll('#modal-1 .table-bordered td.text-end');
            if (totalTableCells.length >= 3) {
                // Verificações para os totais
                totalTableCells[0].innerText = `R$ ${order.totalPrice !== undefined && order.totalPrice !== null ? (order.totalPrice - (order.freightValue || 0)).toFixed(2) : '0.00'}`; // Subtotal
                totalTableCells[1].innerText = `R$ ${order.freightValue !== undefined && order.freightValue !== null ? order.freightValue.toFixed(2) : '0.00'}`; // Frete
                totalTableCells[2].innerText = `R$ ${order.totalPrice !== undefined && order.totalPrice !== null ? order.totalPrice.toFixed(2) : '0.00'}`; // Total
            } else {
                console.warn("Células da tabela de totais não encontradas ou insuficientes.");
            }


            // Abrir modal
            const modal = new bootstrap.Modal(document.getElementById('modal-1'));
            modal.show();

        } catch (err) {
            console.error("Erro na função openOrderDetails:", err);
            alert("Ocorreu um erro ao carregar os detalhes do pedido: " + err.message);
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
