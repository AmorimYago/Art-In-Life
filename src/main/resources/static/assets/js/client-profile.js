document.addEventListener("DOMContentLoaded", () => {

    const clientDataUrl = '/api/client/me';
    let clientId;

    // Carregar dados do cliente
    async function loadClientData() {
        const response = await fetch(clientDataUrl);
        const clientData = await response.json();

        if (!response.ok) return alert("Erro ao carregar dados do cliente.");

        const email = clientData.email;
        const clientResponse = await fetch(`/api/clients/email/${email}`);
        const client = await clientResponse.json();

        if (!clientResponse.ok) return alert("Erro ao carregar detalhes do cliente.");

        clientId = client.id;
        console.log("Client ID obtido:", clientId);

        if (!clientId) {
            alert("Cliente não encontrado ou erro de autenticação.");
            return;
        }

        document.querySelector("[name='name-input']").value = client.fullName;
        document.querySelector("#email-input").value = client.email;
        document.querySelector("[name='cpf-input']").value = client.cpf;
        document.querySelector("[name='birthdate-input']").value = client.birthDate;
        document.querySelector("#gender-select").value = client.gender;

        loadClientAddresses();
    }

    // Atualizar dados pessoais
    document.querySelector("#profile-card form").addEventListener("submit", async (e) => {
        e.preventDefault();

        const passwordInput = document.querySelector("#password-input");
        const passwordConfirmInput = document.querySelector("#password-confirm-input");
        const pw = passwordInput.value;
        const pwConfirm = passwordConfirmInput.value;

        // Remover estados de validação anteriores
        [passwordInput, passwordConfirmInput].forEach(input => input.classList.remove("is-invalid"));
        const previousFeedback = passwordConfirmInput.parentNode.querySelector(".invalid-feedback");
        if (previousFeedback) previousFeedback.remove();

        // Verificar se as senhas coincidem
        if (pw !== pwConfirm) {
            [passwordInput, passwordConfirmInput].forEach(input => input.classList.add("is-invalid"));
            const errorDiv = document.createElement("div");
            errorDiv.className = "invalid-feedback";
            errorDiv.innerText = "As senhas devem ser iguais";
            passwordConfirmInput.parentNode.appendChild(errorDiv);
            return;
        }

        const updatedData = {
            fullName: document.querySelector("[name='name-input']").value,
            birthDate: document.querySelector("[name='birthdate-input']").value,
            gender: document.querySelector("#gender-select").value
        };
        // Incluir senha apenas se foi informada
        if (pw) {
            updatedData.password = pw;
        }

        const response = await fetch(`/api/clients/${clientId}`, {
            method: 'PUT',
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(updatedData)
        });

        if (response.ok) alert("Dados atualizados com sucesso!");
        else alert("Erro ao atualizar dados.");
    });

    // Cadastro de novo endereço usando ViaCEP
    document.querySelector("#cep-input").addEventListener("blur", async () => {
        const cep = document.querySelector("#cep-input").value.replace(/\D/g, '');
        if (cep.length !== 8) return;

        try {
            const resp = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await resp.json();
            if (data.erro) throw new Error("CEP inválido");

            document.querySelector("#street-input").value = data.logradouro;
            document.querySelector("#neighborhood-input").value = data.bairro;
            document.querySelector("#city-input").value = data.localidade;
            document.querySelector("#state-select").value = data.uf;
        } catch (err) {
            alert("Erro ao buscar endereço pelo CEP.");
        }
    });

    document.querySelector("#new-address-card form").addEventListener("submit", async (e) => {
        e.preventDefault();

        if (!clientId) return alert("Erro: Cliente não definido!");

        const addressData = {
            cep: document.querySelector("#cep-input").value,
            street: document.querySelector("#street-input").value,
            number: document.querySelector("#number-input").value,
            complement: document.querySelector("#complement-input").value || "",
            neighborhood: document.querySelector("#neighborhood-input").value,
            city: document.querySelector("#city-input").value,
            state: document.querySelector("#state-select").value,
            billingAddress: false,
            defaultDeliveryAddress: false
        };

        console.log("Enviando dados:", addressData);

        const response = await fetch(`/api/clients/${clientId}/addresses`, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(addressData)
        });

        if (response.ok) {
            alert("Endereço cadastrado com sucesso!");
            loadClientAddresses();
        } else {
            const errorData = await response.json();
            console.error("Erro do backend:", errorData);
            alert("Erro ao cadastrar endereço. Confira o console para detalhes.");
        }
    });

    // Carregar e listar endereços para seleção padrão
    async function loadClientAddresses() {
        if (!clientId) return alert("Erro ao carregar endereços: cliente indefinido.");

        const response = await fetch(`/api/client-addresses/client/${clientId}`);
        const addresses = await response.json();

        const container = document.querySelector("#default-address-card .btn-group-vertical");
        container.innerHTML = '';

        let currentMain = addresses.find(addr => addr.defaultDeliveryAddress);

        addresses.forEach(address => {
            const isChecked = currentMain && address.id === currentMain.id;

            const radioHtml = `<input type="radio" id="address-${address.id}" class="btn-check" name="btnradio" ${isChecked ? 'checked' : ''} autocomplete="off">
                               <label class="btn btn-outline-primary" for="address-${address.id}">
                                   ${address.street}, ${address.number}, ${address.city}, ${address.state}
                               </label>`;

            container.innerHTML += radioHtml;
        });
    }

    document.querySelector("#default-address-card form").addEventListener("submit", async (e) => {
        e.preventDefault();

        const selectedRadio = document.querySelector("input[name='btnradio']:checked");
        if (!selectedRadio) return alert("Selecione um endereço.");

        const addressId = selectedRadio.id.split("-")[1];

        const response = await fetch(`/api/clients/${clientId}/addresses/${addressId}/default`, {
            method: "PUT"
        });

        if (response.ok) {
            alert("Endereço padrão atualizado!");
            loadClientAddresses();
        } else {
            alert("Erro ao atualizar endereço padrão.");
        }
    });

    loadClientData();
});
