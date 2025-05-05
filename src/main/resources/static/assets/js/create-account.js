document.addEventListener("DOMContentLoaded", () => {
    const cepInput = document.getElementById("cep-input");
    const cpfInput = document.querySelector("[name='cpf-input']");
    const passwordInput = document.querySelector("[name='password-input']");
    const confirmPasswordInput = document.querySelector("[name='password-confirm-input']");
    const form = document.querySelector("form.user");

    // Formatação do CPF
    cpfInput.addEventListener("input", (e) => {
        let cpf = e.target.value.replace(/\D/g, "");
        if (cpf.length > 11) cpf = cpf.slice(0, 11);
        e.target.value = cpf
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d)/, "$1.$2")
            .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
    });

    // Validação do CPF
    cpfInput.addEventListener("blur", () => {
        const isValid = validarCPF(cpfInput.value);
        cpfInput.classList.toggle("is-invalid", !isValid);
    });

    // Verificação de senha
    confirmPasswordInput.addEventListener("input", () => {
        const match = passwordInput.value === confirmPasswordInput.value;
        confirmPasswordInput.classList.toggle("is-invalid", !match);
    });

    // Busca do endereço via ViaCEP
    cepInput.addEventListener("blur", async () => {
        const cep = cepInput.value.replace(/\D/g, "");
        if (cep.length !== 8) return;

        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();
            if (data.erro) throw new Error("CEP inválido");

            document.getElementById("street-input").value = data.logradouro || "";
            document.getElementById("neighborhood-input").value = data.bairro || "";
            document.getElementById("city-input").value = data.localidade || "";
            document.getElementById("state-select").value = data.uf || "";
        } catch (err) {
            alert("Erro ao buscar o endereço. Verifique o CEP.");
        }
    });

    // Envio do formulário
    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const dto = {
            fullName: document.querySelector("[name='name-input']").value,
            email: document.querySelector("[name='email-input']").value,
            cpf: cpfInput.value,
            birthDate: document.querySelector("[name='birthdate-input']").value,
            password: passwordInput.value,
            gender: document.querySelector("[name='gender-select']").value,
            billingAddress: {
                cep: document.getElementById("cep-input").value,
                street: document.getElementById("street-input").value,
                number: document.getElementById("number-input-1").value,
                complement: document.getElementById("complement-input-3").value,
                neighborhood: document.getElementById("neighborhood-input").value,
                city: document.getElementById("city-input").value,
                state: document.getElementById("state-select").value,
                billingAddress: true,
                defaultDeliveryAddress: false
            },
            deliveryAddress: {
                cep: document.getElementById("cep-input").value,
                street: document.getElementById("street-input").value,
                number: document.getElementById("number-input-1").value,
                complement: document.getElementById("complement-input-3").value,
                neighborhood: document.getElementById("neighborhood-input").value,
                city: document.getElementById("city-input").value,
                state: document.getElementById("state-select").value,
                billingAddress: false,
                defaultDeliveryAddress: true
            },
            additionalDeliveryAddresses: []
        };

        if (!validarCPF(dto.cpf)) {
            alert("CPF inválido.");
            cpfInput.classList.add("is-invalid");
            return;
        }

        if (dto.password !== confirmPasswordInput.value) {
            alert("As senhas não coincidem.");
            confirmPasswordInput.classList.add("is-invalid");
            return;
        }

        try {
            const response = await fetch("/api/clients", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });

            if (!response.ok) throw new Error("Erro ao cadastrar cliente");

            alert("Cadastro realizado com sucesso!");
            window.location.href = "/index";
        } catch (err) {
            console.error(err);
            alert("Erro ao cadastrar cliente. Tente novamente.");
        }
    });

    // Validação de CPF
    function validarCPF(cpf) {
        cpf = cpf.replace(/[^\d]+/g, '');
        if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;

        let soma = 0;
        for (let i = 0; i < 9; i++) soma += parseInt(cpf.charAt(i)) * (10 - i);
        let resto = 11 - (soma % 11);
        if (resto >= 10) resto = 0;
        if (resto !== parseInt(cpf.charAt(9))) return false;

        soma = 0;
        for (let i = 0; i < 10; i++) soma += parseInt(cpf.charAt(i)) * (11 - i);
        resto = 11 - (soma % 11);
        if (resto >= 10) resto = 0;
        return resto === parseInt(cpf.charAt(10));
    }
});
