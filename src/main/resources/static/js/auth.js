const form = document.getElementById('loginForm');
const loginBtn = document.getElementById('loginBtn');

function showFieldError(inputId, errorId, message) {
    document.getElementById(inputId).classList.add('invalid');
    document.getElementById(errorId).textContent = message;
}

function clearFieldError(inputId, errorId) {
    document.getElementById(inputId).classList.remove('invalid');
    document.getElementById(errorId).textContent = '';
}

function showToast(message, type) {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = 'toast ' + type;
    toast.textContent = message;
    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('fade-out');
        setTimeout(() => toast.remove(), 250);
    }, 3000);
}

function setLoading(isLoading) {
    loginBtn.disabled = isLoading;
    loginBtn.innerHTML = isLoading
        ? '<span class="spinner"></span>Logging in...'
        : 'Login';
}

form.addEventListener('submit', async function (e) {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    // --- Client-side validation ---
    let hasError = false;

    if (username === '') {
        showFieldError('username', 'usernameError', 'Username is required');
        hasError = true;
    } else {
        clearFieldError('username', 'usernameError');
    }

    if (password === '') {
        showFieldError('password', 'passwordError', 'Password is required');
        hasError = true;
    } else if (password.length < 4) {
        showFieldError('password', 'passwordError', 'Password must be at least 4 characters');
        hasError = true;
    } else {
        clearFieldError('password', 'passwordError');
    }

    if (hasError) return;

    // --- API call with loading state ---
    setLoading(true);
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error('Invalid username or password');
        }

        const token = await response.text();
        localStorage.setItem('token', token);
        window.location.href = 'dashboard.html';

    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        setLoading(false);
    }
});