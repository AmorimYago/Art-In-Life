
    document.addEventListener("DOMContentLoaded", function () {
    const userDropdown = document.querySelectorAll('.dropdown')[1];
    const userTrigger = userDropdown.querySelector('a');
    const userMenu = userDropdown.querySelector('.dropdown-menu');

    fetch("/api/client/me", { credentials: 'include' })
    .then(res => {
    if (!res.ok) throw new Error();
    return res.json();
})
    .then(data => {
    // Cliente está logado — mantém o dropdown
    console.log("Cliente logado:", data.email);
})
    .catch(() => {
    // Não logado — remove dropdown e redireciona no clique
    userTrigger.removeAttribute("data-bs-toggle");
    userTrigger.addEventListener("click", function (e) {
    e.preventDefault();
    window.location.href = "/login-client";
});
    if (userMenu) userMenu.remove();
});
});

