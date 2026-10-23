/**
 * Digital Wallet Simulator - Interactive Frontend Scripts
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Mobile Sidebar Toggle
    const mobileToggle = document.getElementById('mobileNavToggle');
    const sidebar = document.querySelector('.sidebar');
    if (mobileToggle && sidebar) {
        mobileToggle.addEventListener('click', function () {
            sidebar.classList.toggle('open');
        });
    }

    // 2. Quick Amount Pills for Add-Money page
    const amountPills = document.querySelectorAll('.pill-btn');
    const amountInput = document.getElementById('amountInput');
    if (amountPills.length > 0 && amountInput) {
        amountPills.forEach(pill => {
            pill.addEventListener('click', function () {
                amountPills.forEach(p => p.classList.remove('active'));
                this.classList.add('active');
                amountInput.value = this.getAttribute('data-value');
                amountInput.focus();
            });
        });
    }

    // 3. Dynamic Receiver Lookup on Transfer page
    const receiverEmailInput = document.getElementById('receiverEmail');
    const receiverFeedback = document.getElementById('receiverFeedback');
    let debounceTimer;

    if (receiverEmailInput && receiverFeedback) {
        receiverEmailInput.addEventListener('input', function () {
            clearTimeout(debounceTimer);
            const email = this.value.trim();
            if (email.length < 5 || !email.includes('@')) {
                receiverFeedback.innerHTML = '';
                return;
            }

            debounceTimer = setTimeout(() => {
                fetch(`transfer?lookupEmail=${encodeURIComponent(email)}`)
                    .then(res => res.json())
                    .then(data => {
                        if (data.found) {
                            receiverFeedback.innerHTML = `<span style="color: #10B981; font-size: 0.85rem; font-weight: 600;">✓ Recipient Found: ${data.fullName}</span>`;
                        } else {
                            receiverFeedback.innerHTML = `<span style="color: #EF4444; font-size: 0.85rem;">✕ No active account found for this email</span>`;
                        }
                    })
                    .catch(() => {
                        receiverFeedback.innerHTML = '';
                    });
            }, 400);
        });
    }

    // 4. Transfer Confirmation Modal/Prompt
    const transferForm = document.getElementById('transferForm');
    if (transferForm) {
        transferForm.addEventListener('submit', function (e) {
            const email = document.getElementById('receiverEmail').value;
            const amount = document.getElementById('amountInput').value;
            if (!confirm(`Are you sure you want to transfer ₹${amount} to ${email}? This action cannot be undone.`)) {
                e.preventDefault();
            }
        });
    }

    // 5. Password Confirmation Check on Register
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function (e) {
            const pwd = document.getElementById('password').value;
            const confirmPwd = document.getElementById('confirmPassword').value;
            if (pwd !== confirmPwd) {
                alert("Passwords do not match. Please verify.");
                e.preventDefault();
            }
        });
    }
});
