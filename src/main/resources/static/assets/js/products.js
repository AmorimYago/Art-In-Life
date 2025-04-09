// produtos.js

let produtos = [];
let produtosFiltrados = [];
let paginaAtual = 1;
const porPagina = 10;
let editandoId = null;
let mainImageFileName = null;

const API_URL = '/api/products';
const IMG_BASE = '/images/';

// Inicializa eventos
window.addEventListener('DOMContentLoaded', () => {
    carregarProdutos();

    document.querySelector('input[type="search"]').addEventListener('input', filtrarProdutos);
    document.getElementById('modal-cadastrar-produto').querySelector('.btn.btn-primary').addEventListener('click', salvarProduto);
    document.getElementById('imagens-produto').addEventListener('change', atualizarCarrosselCadastro);
});

async function carregarProdutos() {
    const res = await fetch(API_URL);
    produtos = await res.json();
    produtosFiltrados = [...produtos];
    paginaAtual = 1;
    renderizarTabela();
    renderizarPaginacao();
}

function renderizarTabela() {
    const tbody = document.querySelector('tbody');
    tbody.innerHTML = '';
    const inicio = (paginaAtual - 1) * porPagina;
    const produtosPagina = produtosFiltrados.slice(inicio, inicio + porPagina);

    produtosPagina.forEach(produto => {
        const linha = document.createElement('tr');
        linha.innerHTML = `
      <td>${produto.id}</td>
      <td>${produto.name}</td>
      <td>${produto.stock}</td>
      <td>R$ ${produto.price.toFixed(2)}</td>
      <td>${produto.status ? 'Ativo' : 'Inativo'}</td>
      <td>
        <a href="#" onclick="abrirEditar('${produto.id}')"><i class="fas fa-edit"></i></a>
        <a href="#" onclick="visualizarProduto('${produto.id}')"><i class="far fa-eye"></i></a>
        <div class="form-check form-switch d-inline">
          <input class="form-check-input" type="checkbox" ${produto.status ? 'checked' : ''} onchange="alternarStatus('${produto.id}', this.checked)">
        </div>
      </td>
    `;
        tbody.appendChild(linha);
    });
}

function renderizarPaginacao() {
    const paginacao = document.querySelector('.pagination');
    paginacao.innerHTML = '';
    const totalPaginas = Math.ceil(produtosFiltrados.length / porPagina);

    for (let i = 1; i <= totalPaginas; i++) {
        const li = document.createElement('li');
        li.className = `page-item ${paginaAtual === i ? 'active' : ''}`;
        li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        li.addEventListener('click', e => {
            e.preventDefault();
            paginaAtual = i;
            renderizarTabela();
            renderizarPaginacao();
        });
        paginacao.appendChild(li);
    }
}

function filtrarProdutos(e) {
    const termo = e.target.value.toLowerCase();
    produtosFiltrados = produtos.filter(p => p.name.toLowerCase().includes(termo));
    paginaAtual = 1;
    renderizarTabela();
    renderizarPaginacao();
}

function abrirEditar(id) {
    const produto = produtos.find(p => p.id === id);
    if (!produto) return;

    editandoId = id;
    document.getElementById('nome-produto').value = produto.name;
    document.getElementById('preco-produto').value = produto.price;
    document.getElementById('quantidade-produto').value = produto.stock;
    document.getElementById('descricao-produto').value = produto.description;
    document.getElementById('avaliacao-produto').value = produto.rating;
    mainImageFileName = produto.images.find(img => img.main)?.path || produto.images[0]?.path || null;

    const imagens = produto.images || [];
    const carousel = document.querySelector('#carousel-1');
    const inner = carousel.querySelector('.carousel-inner');
    const indicators = carousel.querySelector('.carousel-indicators');
    inner.innerHTML = '';
    indicators.innerHTML = '';

    imagens.forEach((img, index) => {
        const item = document.createElement('div');
        item.className = 'carousel-item' + (index === 0 ? ' active' : '');

        const estrela = document.createElement('i');
        estrela.className = 'position-absolute fa-star';
        estrela.style.top = '10px';
        estrela.style.left = '50%';
        estrela.style.transform = 'translateX(-50%)';
        estrela.style.cursor = 'pointer';
        estrela.classList.add(img.path === mainImageFileName ? 'fas' : 'far');

        estrela.addEventListener('click', () => {
            mainImageFileName = img.path;
            abrirEditar(id);
        });

        item.innerHTML = `
            <div class="position-relative w-100 h-100">
              <img class="object-fit-cover w-100 d-block" src="${IMG_BASE + img.path}" alt="Imagem ${index + 1}">
            </div>
        `;
        item.querySelector('.position-relative').appendChild(estrela);
        inner.appendChild(item);

        const indicator = document.createElement('button');
        indicator.type = 'button';
        indicator.setAttribute('data-bs-target', '#carousel-1');
        indicator.setAttribute('data-bs-slide-to', index);
        if (index === 0) indicator.className = 'active';
        indicators.appendChild(indicator);
    });

    new bootstrap.Modal(document.getElementById('modal-cadastrar-produto')).show();
}

function visualizarProduto(id) {
    const produto = produtos.find(p => p.id === id);
    if (!produto) return;

    const modal = document.getElementById('modal-mostrar-produto');
    modal.querySelector('.modal-title').innerText = produto.name;
    modal.querySelectorAll('.col-form-label')[0].innerText = produto.name;
    modal.querySelectorAll('.col-form-label')[1].innerText = `R$ ${produto.price.toFixed(2)}`;
    modal.querySelectorAll('.col-form-label')[2].innerText = produto.stock;
    modal.querySelectorAll('.col-form-label')[3].innerText = produto.description;

    const estrelasContainer = modal.querySelector('.mb-3 .row .col');
    estrelasContainer.innerHTML = renderizarEstrelas(produto.rating);

    const carouselInner = modal.querySelector('#carousel-2 .carousel-inner');
    const indicators = modal.querySelector('#carousel-2 .carousel-indicators');
    carouselInner.innerHTML = '';
    indicators.innerHTML = '';
    produto.images.forEach((img, index) => {
        const item = document.createElement('div');
        item.className = 'carousel-item' + (index === 0 ? ' active' : '');
        item.innerHTML = `<img class="object-fit-cover w-100 d-block" src="${IMG_BASE + img.path}" alt="Imagem do produto">`;
        carouselInner.appendChild(item);

        const indicator = document.createElement('button');
        indicator.setAttribute('type', 'button');
        indicator.setAttribute('data-bs-target', '#carousel-2');
        indicator.setAttribute('data-bs-slide-to', index);
        if (index === 0) indicator.className = 'active';
        indicators.appendChild(indicator);
    });

    new bootstrap.Modal(modal).show();
}

function renderizarEstrelas(rating) {
    const full = Math.floor(rating);
    const empty = 5 - full;
    return '<i class="fas fa-star"></i>'.repeat(full) + '<i class="far fa-star"></i>'.repeat(empty);
}

async function salvarProduto() {
    const nome = document.getElementById('nome-produto').value;
    const preco = parseFloat(document.getElementById('preco-produto').value);
    const quantidade = parseInt(document.getElementById('quantidade-produto').value);
    const descricao = document.getElementById('descricao-produto').value;
    const avaliacao = parseFloat(document.getElementById('avaliacao-produto').value);

    const inputImagens = document.getElementById('imagens-produto');
    const arquivos = Array.from(inputImagens.files);

    let imagePaths = [];
    if (arquivos.length > 0) {
        const formData = new FormData();
        arquivos.forEach(file => formData.append('files', file));

        const uploadResponse = await fetch('/api/images/upload', {
            method: 'POST',
            body: formData
        });

        if (!uploadResponse.ok) {
            alert('Erro ao fazer upload das imagens!');
            return;
        }

        imagePaths = await uploadResponse.json();
    }

    const produto = {
        name: nome,
        price: preco,
        stock: quantidade,
        description: descricao,
        rating: avaliacao,
        imagePaths: imagePaths.length > 0 ? imagePaths : undefined,
        mainImagePath: mainImageFileName
    };

    const response = await fetch(editandoId ? `${API_URL}/${editandoId}` : API_URL, {
        method: editandoId ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(produto)
    });

    if (response.ok) {
        bootstrap.Modal.getInstance(document.getElementById('modal-cadastrar-produto')).hide();
        editandoId = null;
        carregarProdutos();
    } else {
        alert('Erro ao salvar produto!');
    }
}

function atualizarCarrosselCadastro() {
    const input = document.getElementById('imagens-produto');
    const arquivos = Array.from(input.files);

    const carousel = document.querySelector('#carousel-1');
    const inner = carousel.querySelector('.carousel-inner');
    const indicators = carousel.querySelector('.carousel-indicators');

    inner.innerHTML = '';
    indicators.innerHTML = '';
    mainImageFileName = arquivos[0]?.name || null;

    arquivos.forEach((file, index) => {
        const reader = new FileReader();
        reader.onload = function (e) {
            const item = document.createElement('div');
            item.className = 'carousel-item' + (index === 0 ? ' active' : '');

            const estrela = document.createElement('i');
            estrela.className = 'position-absolute fa-star';
            estrela.style.top = '10px';
            estrela.style.left = '50%';
            estrela.style.transform = 'translateX(-50%)';
            estrela.style.cursor = 'pointer';
            estrela.classList.add(file.name === mainImageFileName ? 'fas' : 'far');

            estrela.addEventListener('click', () => {
                mainImageFileName = file.name;
                atualizarCarrosselCadastro();
            });

            item.innerHTML = `
                <div class="position-relative w-100 h-100">
                  <img class="object-fit-cover w-100 d-block" src="${e.target.result}" alt="Imagem ${index + 1}">
                </div>
            `;
            item.querySelector('.position-relative').appendChild(estrela);

            inner.appendChild(item);

            const indicator = document.createElement('button');
            indicator.type = 'button';
            indicator.setAttribute('data-bs-target', '#carousel-1');
            indicator.setAttribute('data-bs-slide-to', index);
            if (index === 0) indicator.className = 'active';
            indicators.appendChild(indicator);
        };
        reader.readAsDataURL(file);
    });
}

async function alternarStatus(id, status) {
    await fetch(`${API_URL}/${id}/${status ? 'enable' : 'disable'}`, { method: 'PATCH' });
    carregarProdutos();
}
