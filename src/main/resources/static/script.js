const shortenForm = document.getElementById('shortenForm');
const longUrlInput = document.getElementById('longUrl');
const result = document.getElementById('result');
const redirectCodeInput = document.getElementById('redirectCode');
const redirectButton = document.getElementById('redirectButton');
const analyticsCodeInput = document.getElementById('analyticsCode');
const analyticsButton = document.getElementById('analyticsButton');
const analytics = document.getElementById('analytics');

shortenForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    hide(result);

    try {
        const response = await fetch('/api/urls/shorten', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({longUrl: longUrlInput.value})
        });
        const payload = await response.json();

        if (!response.ok) {
            show(result, `<strong>Error:</strong> ${payload.message}`);
            return;
        }

        const data = payload.data;
        redirectCodeInput.value = data.shortCode;
        analyticsCodeInput.value = data.shortCode;
        show(result, `
            <span class="label">Short URL</span>
            <a href="${data.shortUrl}" target="_blank" rel="noopener">${data.shortUrl}</a>
            <span class="meta">Code: ${data.shortCode} | Clicks: ${data.clickCount}</span>
        `);
    } catch (error) {
        show(result, `<strong>Error:</strong> Unable to connect to the API`);
    }
});

redirectButton.addEventListener('click', () => {
    const shortCode = redirectCodeInput.value.trim();
    if (shortCode) {
        window.open(`/${shortCode}`, '_blank');
    }
});

analyticsButton.addEventListener('click', async () => {
    const shortCode = analyticsCodeInput.value.trim();
    if (!shortCode) {
        return;
    }

    hide(analytics);
    try {
        const response = await fetch(`/api/urls/${shortCode}/analytics`);
        const payload = await response.json();

        if (!response.ok) {
            show(analytics, `<strong>Error:</strong> ${payload.message}`);
            return;
        }

        const data = payload.data;
        show(analytics, `
            <div><span>Original URL</span><strong>${data.longUrl}</strong></div>
            <div><span>Short URL</span><strong>${data.shortUrl}</strong></div>
            <div><span>Total Clicks</span><strong>${data.clickCount}</strong></div>
            <div><span>Created</span><strong>${formatDate(data.createdAt)}</strong></div>
            <div><span>Last Accessed</span><strong>${formatDate(data.lastAccessedAt)}</strong></div>
        `);
    } catch (error) {
        show(analytics, `<strong>Error:</strong> Unable to fetch analytics`);
    }
});

function show(element, html) {
    element.innerHTML = html;
    element.classList.remove('hidden');
}

function hide(element) {
    element.innerHTML = '';
    element.classList.add('hidden');
}

function formatDate(value) {
    if (!value) {
        return 'Not accessed yet';
    }
    return new Date(value).toLocaleString();
}
