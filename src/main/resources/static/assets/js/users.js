let modoEdicao = false;
let idUsuarioEditando = null;
let usuarios = [];
let usuariosFiltrados = [];
let paginaAtual = 1;
const porPagina = 10;

document.addEventListener('DOMContentLoaded', () => {
    carregarUsuarios();

    document.querySelector('[data-bs-target="#modal-cadastrar-user"]').addEventListener('click', resetarModal);
    document.querySelector('#modal-cadastrar-user .btn.btn-primary').addEventListener('click', salvarUsuario);

    document.getElementById('cpf-user').addEventListener('input', formatarCPF);
    document.querySelector('input[type="search"]').addEventListener('input', filtrarUsuarios);
});

async function carregarUsuarios() {
    const response = await fetch('/api/users');
    usuarios = await response.json();
    usuariosFiltrados = [...usuarios];
    paginaAtual = 1;
    renderizarTabela();
    renderizarPaginacao();
}

function renderizarTabela() {
    const tbody = document.querySelector('tbody');
    tbody.innerHTML = '';

    const inicio = (paginaAtual - 1) * porPagina;
    const fim = inicio + porPagina;
    const usuariosPagina = usuariosFiltrados.slice(inicio, fim);

    usuariosPagina.forEach(user => {
        const linha = document.createElement('tr');
        linha.innerHTML = `
      <td style="min-width: 200px;">${user.name}</td>
      <td style="min-width: 200px;">${user.email}</td>
      <td style="max-width: 50px;">${user.status ? 'Ativo' : 'Inativo'}</td>
      <td class="pe-2" style="max-width: 50px;">
        <a href="#" class="me-2" onclick="editarUsuario('${user.id}')"><i class="fas fa-edit"></i></a>
        <div class="d-inline form-check form-switch pt-0">
          <input class="float-none form-check-input" type="checkbox" role="switch"
                 ${user.status ? 'checked' : ''}
                 onchange="mudarStatusUsuario('${user.id}', this.checked)">
        </div>
      </td>
    `;
        tbody.appendChild(linha);
    });
}

function renderizarPaginacao() {
    const totalPaginas = Math.ceil(usuariosFiltrados.length / porPagina);
    const paginacao = document.querySelector('.pagination');
    paginacao.innerHTML = '';

    if (totalPaginas <= 1) return;

    const adicionarPagina = (numero, ativo = false) => {
        const li = document.createElement('li');
        li.className = `page-item ${ativo ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="#">${numero}</a>`;
        li.addEventListener('click', e => {
            e.preventDefault();
            paginaAtual = numero;
            renderizarTabela();
            renderizarPaginacao();
        });
        paginacao.appendChild(li);
    };

    adicionarPagina(1, paginaAtual === 1);
    for (let i = 2; i <= totalPaginas; i++) {
        adicionarPagina(i, paginaAtual === i);
    }
}

function filtrarUsuarios(e) {
    const termo = e.target.value.toLowerCase();
    usuariosFiltrados = usuarios.filter(user =>
        user.name.toLowerCase().includes(termo) || user.email.toLowerCase().includes(termo)
    );
    paginaAtual = 1;
    renderizarTabela();
    renderizarPaginacao();
}

async function salvarUsuario() {
    const nome = document.getElementById('nome-user');
    const email = document.getElementById('email-user');
    const cpf = document.getElementById('cpf-user');
    const senha = document.getElementById('senha-user');
    const repetirSenha = document.getElementById('repetir-senha-user');
    const grupo = document.getElementById('grupo-user');

    [cpf, senha, repetirSenha].forEach(field => field.classList.remove('is-invalid'));
    ['erro-cpf', 'erro-senha', 'erro-repetir-senha'].forEach(id => {
        document.getElementById(id).style.display = 'none';
    });

    let hasError = false;

    if (!validarCPF(cpf.value)) {
        cpf.classList.add('is-invalid');
        document.getElementById('erro-cpf').style.display = 'block';
        hasError = true;
    }

    if (senha.value !== repetirSenha.value) {
        senha.classList.add('is-invalid');
        repetirSenha.classList.add('is-invalid');
        document.getElementById('erro-senha').style.display = 'block';
        document.getElementById('erro-repetir-senha').style.display = 'block';
        hasError = true;
    }

    if (hasError) return;

    const usuario = {
        name: nome.value,
        email: email.value,
        cpf: cpf.value,
        password: senha.value,
        type: grupo.value
    };

    const url = modoEdicao ? `/api/users/${idUsuarioEditando}` : '/api/users';
    const method = modoEdicao ? 'PUT' : 'POST';

    const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuario)
    });

    if (response.ok) {
        bootstrap.Modal.getInstance(document.getElementById('modal-cadastrar-user')).hide();
        carregarUsuarios();
        resetarModal();
    } else {
        alert('Erro ao salvar usuário!');
    }
}

function editarUsuario(id) {
    fetch(`/api/users/${id}`)
        .then(res => res.json())
        .then(user => {
            modoEdicao = true;
            idUsuarioEditando = user.id;

            document.getElementById('nome-user').value = user.name;
            document.getElementById('email-user').value = user.email;
            document.getElementById('cpf-user').value = user.cpf;
            document.getElementById('grupo-user').value = user.type;
            document.getElementById('senha-user').value = '';
            document.getElementById('repetir-senha-user').value = '';

            const modal = new bootstrap.Modal(document.getElementById('modal-cadastrar-user'));
            modal.show();
        });
}

function resetarModal() {
    modoEdicao = false;
    idUsuarioEditando = null;
    document.querySelector('#modal-cadastrar-user form').reset();

    ['cpf-user', 'senha-user', 'repetir-senha-user'].forEach(id => {
        document.getElementById(id).classList.remove('is-invalid');
    });

    ['erro-cpf', 'erro-senha', 'erro-repetir-senha'].forEach(id => {
        document.getElementById(id).style.display = 'none';
    });
}

async function mudarStatusUsuario(id, status) {
    const response = await fetch(`/api/users/${id}/status?status=${status}`, {
        method: 'PATCH'
    });

    if (!response.ok) {
        alert('Erro ao atualizar status');
        carregarUsuarios();
    }
}

function formatarCPF(e) {
    let cpf = e.target.value.replace(/\D/g, '');
    if (cpf.length > 11) cpf = cpf.slice(0, 11);
    e.target.value = cpf
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
}

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
