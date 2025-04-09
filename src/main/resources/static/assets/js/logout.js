document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.getElementById('btn-logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            const response = await fetch('/logout', {
                method: 'POST'
            });

            if (response.ok || response.redirected) {
                window.location.href = '/login?logout';
            } else {
                alert('Erro ao fazer logout.');
            }
        });
    }
});