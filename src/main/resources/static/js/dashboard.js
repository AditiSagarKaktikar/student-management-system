const token = localStorage.getItem('token');
if (!token) window.location.href = 'index.html';

function getRoleFromToken(t) {
    return JSON.parse(atob(t.split('.')[1])).role;
}
const role = getRoleFromToken(token);
if (role === 'ADMIN') {
    document.getElementById('adminPanel').style.display = 'block';
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

const tbody = document.getElementById('studentTableBody');
const searchInput = document.getElementById('searchInput');
const prevBtn = document.getElementById('prevBtn');
const nextBtn = document.getElementById('nextBtn');
const pageInfo = document.getElementById('pageInfo');

let state = {
    search: '',
    page: 0,
    size: 10,
    sortBy: 'id',
    direction: 'asc'
};

let debounceTimer;

async function loadStudents() {
    tbody.innerHTML = '<tr><td colspan="5" class="empty-state">Loading...</td></tr>';

    const params = new URLSearchParams({
        search: state.search,
        page: state.page,
        size: state.size,
        sortBy: state.sortBy,
        direction: state.direction
    });

    const response = await fetch('/api/students?' + params.toString(), {
        headers: { 'Authorization': 'Bearer ' + token }
    });

    if (response.status === 401) {
        localStorage.removeItem('token');
        window.location.href = 'index.html';
        return;
    }

    const data = await response.json();

    if (data.content.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty-state">No students found.</td></tr>';
    } else {
        tbody.innerHTML = '';
        data.content.forEach(s => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${s.id}</td>
                <td>${s.name}</td>
                <td>${s.email}</td>
                <td>${s.course}</td>
                <td>${role === 'ADMIN' ? `
                    <button class="secondary" onclick="openEditModal(${s.id}, '${s.name}', '${s.email}', '${s.course}')">Edit</button>
                    <button class="danger" onclick="deleteStudent(${s.id})">Delete</button>
                ` : ''}</td>
            `;
            tbody.appendChild(row);
        });
    }

    pageInfo.textContent = `Page ${data.currentPage + 1} of ${Math.max(data.totalPages, 1)} (${data.totalElements} total)`;
    prevBtn.disabled = data.currentPage === 0;
    nextBtn.disabled = data.isLast;
}

searchInput.addEventListener('input', function () {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        state.search = searchInput.value.trim();
        state.page = 0;
        loadStudents();
    }, 400);
});

document.querySelectorAll('th.sortable').forEach(header => {
    header.addEventListener('click', function () {
        const column = header.getAttribute('data-sort');

        if (state.sortBy === column) {
            state.direction = state.direction === 'asc' ? 'desc' : 'asc';
        } else {
            state.sortBy = column;
            state.direction = 'asc';
        }

        document.querySelectorAll('th.sortable').forEach(h => h.classList.remove('active'));
        header.classList.add('active');

        state.page = 0;
        loadStudents();
    });
});

prevBtn.addEventListener('click', function () {
    if (state.page > 0) {
        state.page--;
        loadStudents();
    }
});

nextBtn.addEventListener('click', function () {
    state.page++;
    loadStudents();
});

function validateAndGet(fieldId, errorId, label) {
    const value = document.getElementById(fieldId).value.trim();
    if (value === '') {
        document.getElementById(fieldId).classList.add('invalid');
        document.getElementById(errorId).textContent = label + ' is required';
        return null;
    }
    document.getElementById(fieldId).classList.remove('invalid');
    document.getElementById(errorId).textContent = '';
    return value;
}

document.getElementById('addBtn')?.addEventListener('click', async function () {
    const name = validateAndGet('name', 'nameError', 'Name');
    const email = validateAndGet('email', 'emailError', 'Email');
    const course = validateAndGet('course', 'courseError', 'Course');

    if (!name || !email || !course) return;

    const btn = document.getElementById('addBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span>Adding...';

    try {
        const response = await fetch('/api/students', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ name, email, course })
        });

        if (!response.ok) {
            const errData = await response.json();
            throw new Error(errData.message || 'Failed to add student');
        }

        document.getElementById('name').value = '';
        document.getElementById('email').value = '';
        document.getElementById('course').value = '';

        showToast('Student added successfully', 'success');
        loadStudents();

    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = 'Add Student';
    }
});

async function deleteStudent(id) {
    if (!confirm('Delete this student?')) return;

    try {
        const response = await fetch('/api/students/' + id, {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + token }
        });
        if (!response.ok) throw new Error('Failed to delete student');

        showToast('Student deleted', 'success');
        loadStudents();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

const editModalOverlay = document.getElementById('editModalOverlay');

function openEditModal(id, name, email, course) {
    document.getElementById('editId').value = id;
    document.getElementById('editName').value = name;
    document.getElementById('editEmail').value = email;
    document.getElementById('editCourse').value = course;
    editModalOverlay.classList.add('open');
}

function closeEditModal() {
    editModalOverlay.classList.remove('open');
}

document.getElementById('cancelEditBtn').addEventListener('click', closeEditModal);

editModalOverlay.addEventListener('click', function (e) {
    if (e.target === editModalOverlay) closeEditModal();
});

document.getElementById('saveEditBtn').addEventListener('click', async function () {
    const id = document.getElementById('editId').value;
    const name = validateAndGet('editName', 'editNameError', 'Name');
    const email = validateAndGet('editEmail', 'editEmailError', 'Email');
    const course = validateAndGet('editCourse', 'editCourseError', 'Course');

    if (!name || !email || !course) return;

    const btn = document.getElementById('saveEditBtn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span>Saving...';

    try {
        const response = await fetch('/api/students/' + id, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ name, email, course })
        });

        if (!response.ok) {
            const errData = await response.json();
            throw new Error(errData.message || 'Failed to update student');
        }

        showToast('Student updated successfully', 'success');
        closeEditModal();
        loadStudents();

    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.innerHTML = 'Save Changes';
    }
});

document.getElementById('logoutBtn').addEventListener('click', function () {
    localStorage.removeItem('token');
    window.location.href = 'index.html';
});

loadStudents();