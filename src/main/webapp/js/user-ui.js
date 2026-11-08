/* =========================================================
   Regent Books - User Management interface helpers
   Put this file at:  webapp/js/user-ui.js
   Loaded by: login, register, forgot-password, profile, edit, users-list, welcome
   Front-end only: it never changes what a form sends to the server.
   ========================================================= */
(function () {
    'use strict';

    var d = document;
    function $(s, r) { return (r || d).querySelector(s); }
    function $$(s, r) { return Array.prototype.slice.call((r || d).querySelectorAll(s)); }
    function h(tag, cls, html) {
        var e = d.createElement(tag);
        if (cls) e.className = cls;
        if (html != null) e.innerHTML = html;
        return e;
    }
    function pad(n) { return (n < 10 ? '0' : '') + n; }

    /* ---------- dialog ---------- */
    function openModal(content, label) {
        var previous = d.activeElement;
        var backdrop = h('div', 'um-backdrop');
        var box = h('div', 'um-modal');
        box.setAttribute('role', 'dialog');
        box.setAttribute('aria-modal', 'true');
        if (label) box.setAttribute('aria-label', label);
        box.appendChild(content);
        backdrop.appendChild(box);
        d.body.appendChild(backdrop);
        d.body.classList.add('um-lock');

        function close() {
            d.removeEventListener('keydown', onKey);
            backdrop.remove();
            d.body.classList.remove('um-lock');
            if (previous && previous.focus) previous.focus();
        }
        function onKey(e) {
            if (e.key === 'Escape') { close(); return; }
            if (e.key !== 'Tab') return;
            var f = $$('button:not([disabled])', box);
            if (!f.length) return;
            var first = f[0], last = f[f.length - 1];
            if (e.shiftKey && d.activeElement === first) { e.preventDefault(); last.focus(); }
            else if (!e.shiftKey && d.activeElement === last) { e.preventDefault(); first.focus(); }
        }
        backdrop.addEventListener('mousedown', function (e) { if (e.target === backdrop) close(); });
        d.addEventListener('keydown', onKey);
        $$('[data-close]', box).forEach(function (b) { b.addEventListener('click', close); });
        var focusTarget = $('[data-autofocus]', box) || $('button', box);
        if (focusTarget) focusTarget.focus();
        return { close: close };
    }

    /* ---------- delete confirmation (replaces the browser confirm box) ---------- */
    function confirms() {
        $$('form[onsubmit]').forEach(function (f) {
            var m = (f.getAttribute('onsubmit') || '').match(/confirm\((['"])(.*?)\1\)/);
            if (!m) return;
            var message = m[2].replace(/\\'/g, "'");
            f.removeAttribute('onsubmit');
            f.addEventListener('submit', function (e) {
                if (f.dataset.confirmed) return;
                e.preventDefault();
                var danger = /delete|remove/i.test(message);
                var box = h('div', 'um-confirm',
                    '<div class="um-confirm__icon"><i class="fa-solid ' + (danger ? 'fa-trash-can' : 'fa-circle-question') + '"></i></div>' +
                    '<h3>' + (danger ? 'Please confirm' : 'Are you sure?') + '</h3><p></p>' +
                    '<div class="um-actions"><button type="button" class="btn btn-outline-light" data-close>Cancel</button>' +
                    '<button type="button" class="btn ' + (danger ? 'btn-danger' : 'btn-primary') + '" data-autofocus data-yes>' +
                    (danger ? 'Yes, delete' : 'Yes, continue') + '</button></div>');
                $('p', box).textContent = message;
                var modal = openModal(box, 'Confirm');
                $('[data-yes]', box).addEventListener('click', function () {
                    f.dataset.confirmed = '1';
                    modal.close();
                    f.submit();
                });
            });
        });
    }

    /* ---------- success messages become toasts (errors stay on the page) ---------- */
    function toasts() {
        var items = $$('.alert').filter(function (a) {
            if (a.closest('.auth-card')) return false;
            if (a.dataset.toast === 'yes') return true;
            if (a.dataset.toast === 'no') return false;
            return !/not |already|required|invalid|must |cannot|denied|failed|commas/i.test(a.textContent);
        });
        if (!items.length) return;
        var box = h('div', 'um-toasts');
        box.setAttribute('role', 'region');
        box.setAttribute('aria-label', 'Notifications');
        d.body.appendChild(box);
        items.forEach(function (a) {
            var t = h('div', 'um-toast', '<i class="fa-solid fa-circle-check"></i><span></span>' +
                '<button type="button" aria-label="Dismiss">&times;</button>');
            t.setAttribute('role', 'status');
            $('span', t).textContent = a.textContent.trim();
            a.hidden = true;
            box.appendChild(t);
            function gone() { t.classList.add('is-leaving'); setTimeout(function () { t.remove(); }, 250); }
            $('button', t).addEventListener('click', gone);
            setTimeout(gone, 6000);
        });
    }

    /* ---------- password fields ---------- */
    function decoratePassword(inp) {
        var wrap = h('div', 'pw-wrap');
        inp.parentNode.insertBefore(wrap, inp);
        wrap.appendChild(inp);
        var btn = h('button', 'pw-toggle', '<i class="fa-regular fa-eye"></i>');
        btn.type = 'button';
        btn.setAttribute('aria-label', 'Show password');
        wrap.appendChild(btn);
        btn.addEventListener('click', function () {
            var show = inp.type === 'password';
            inp.type = show ? 'text' : 'password';
            btn.firstChild.className = show ? 'fa-regular fa-eye-slash' : 'fa-regular fa-eye';
            btn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
        });
        var caps = h('div', 'pw-caps', '<i class="fa-solid fa-triangle-exclamation me-1"></i>Caps Lock is on');
        caps.hidden = true;
        wrap.parentNode.insertBefore(caps, wrap.nextSibling);
        function check(e) { if (e.getModifierState) caps.hidden = !e.getModifierState('CapsLock'); }
        inp.addEventListener('keydown', check);
        inp.addEventListener('keyup', check);
        inp.addEventListener('blur', function () { caps.hidden = true; });
    }

    function passwords() {
        var fields = $$('input[type=password]');
        fields.forEach(decoratePassword);
        fields.forEach(function (inp) {
            var form = inp.form;
            // strength meter only on the "new password" input (marked data-strength in the JSP)
            if (!form || !inp.hasAttribute('data-strength')) return;

            var group = inp.parentNode.parentNode;
            var meter = h('div', 'pw-meter', '<span></span>');
            var tip = 'Use 8+ characters with a mix of letters, numbers and symbols.';
            var hint = h('small', 'pw-hint', tip);
            group.insertBefore(meter, $('.pw-caps', group).nextSibling);
            group.insertBefore(hint, meter.nextSibling);

            var labels = ['Too weak', 'Weak', 'Fair', 'Good', 'Strong'];
            inp.addEventListener('input', function () {
                var v = inp.value, s = 0;
                if (v.length >= 8) s++;
                if (/[a-z]/.test(v) && /[A-Z]/.test(v)) s++;
                if (/\d/.test(v)) s++;
                if (/[^A-Za-z0-9]/.test(v)) s++;
                if (!v) { meter.className = 'pw-meter'; hint.textContent = tip; return; }
                meter.className = 'pw-meter s' + s;
                hint.textContent = 'Strength: ' + labels[s];
            });

            // "confirm password" input is rendered by the JSP (name="confirm", data-confirm)
            var again = $('input[data-confirm]', form);
            if (!again) return;
            function match() { again.setCustomValidity(inp.value !== again.value ? 'Passwords do not match' : ''); }
            inp.addEventListener('input', match);
            again.addEventListener('input', match);
        });
    }

    /* ---------- busy state on submit ---------- */
    function submitState() {
        $$('form').forEach(function (f) {
            f.addEventListener('submit', function (e) {
                if (e.defaultPrevented) return;
                var b = $('button[type=submit]:not([name])', f);
                if (!b || b.classList.contains('is-busy')) return;
                b.classList.add('is-busy');
                b.dataset.label = b.innerHTML;
                setTimeout(function () { b.disabled = true; b.innerHTML = '<span class="um-spin"></span>Please wait\u2026'; }, 0);
            });
        });
        window.addEventListener('pageshow', function (e) {
            if (!e.persisted) return;
            $$('button.is-busy').forEach(function (b) {
                b.disabled = false; b.classList.remove('is-busy');
                if (b.dataset.label) b.innerHTML = b.dataset.label;
            });
        });
    }

    /* ---------- users table: search, sort, count ---------- */
    function tables() {
        $$('table.table-dark-glass').forEach(function (tbl) {
            var tbody = tbl.tBodies[0];
            if (!tbody) return;
            var all = $$('tr', tbody).filter(function (r) { return !r.querySelector('td[colspan]'); });
            if (!all.length) return;
            var cols = $$('thead th', tbl).length;

            var live = $('input[data-live-search]');
            var input, count, none = null;
            if (live) {
                input = live;                       // the search box is part of the page's own search form
                count = $('.um-count');
            } else {
                var tools = h('div', 'um-tools',
                    '<label class="um-search"><i class="fa-solid fa-magnifying-glass"></i>' +
                    '<input type="search" placeholder="Search users\u2026" aria-label="Search users"></label>' +
                    '<span class="um-count" aria-live="polite"></span>');
                var holder = tbl.closest('.table-responsive') || tbl;
                holder.parentNode.insertBefore(tools, holder);
                input = $('input', tools);
                count = $('.um-count', tools);
            }

            function apply() {
                var q = input.value.trim().toLowerCase(), n = 0;
                all.forEach(function (r) {
                    var show = !q || r.textContent.toLowerCase().indexOf(q) > -1;
                    r.hidden = !show;
                    if (show) n++;
                });
                if (count) count.textContent = q ? n + ' of ' + all.length + ' shown' : all.length + (all.length === 1 ? ' user' : ' users');
                if (!n) {
                    if (!none) { none = h('tr', 'um-empty-row', '<td colspan="' + cols + '">No matches found</td>'); tbody.appendChild(none); }
                    none.hidden = false;
                } else if (none) { none.hidden = true; }
            }
            input.addEventListener('input', apply);
            apply();

            var ths = $$('thead th', tbl);
            var numbered = ths.length && ths[0].textContent.trim() === '#';
            ths.forEach(function (th, i) {
                var label = th.textContent.trim().toLowerCase();
                if (!label || label === 'actions' || label === '#') return;
                th.classList.add('um-sortable');
                th.tabIndex = 0;
                th.setAttribute('aria-sort', 'none');
                function sort() {
                    var dir = th.dataset.dir === 'asc' ? 'desc' : 'asc';
                    ths.forEach(function (o) { o.removeAttribute('data-dir'); o.setAttribute('aria-sort', 'none'); });
                    th.dataset.dir = dir;
                    th.setAttribute('aria-sort', dir === 'asc' ? 'ascending' : 'descending');
                    var f = dir === 'asc' ? 1 : -1;
                    all.sort(function (a, b) {
                        var x = (a.cells[i] ? a.cells[i].textContent : '').trim();
                        var y = (b.cells[i] ? b.cells[i].textContent : '').trim();
                        return x.localeCompare(y, undefined, { numeric: true, sensitivity: 'base' }) * f;
                    });
                    all.forEach(function (r, j) {
                        tbody.insertBefore(r, none);
                        if (numbered && r.cells[0]) r.cells[0].textContent = pad(j + 1);
                    });
                }
                th.addEventListener('click', sort);
                th.addEventListener('keydown', function (e) { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); sort(); } });
            });
        });
    }

    /* ---------- profile photo picker: instant preview + type/size check ---------- */
    function photos() {
        $$('input[type=file][data-photo-input]').forEach(function (inp) {
            var box = inp.form ? $('.avatar', inp.form) : null;
            var original = box ? box.innerHTML : '';
            function reject(msg) {
                inp.setCustomValidity(msg);
                inp.reportValidity();
                inp.setCustomValidity('');
                inp.value = '';
                if (box) box.innerHTML = original;
            }
            inp.addEventListener('change', function () {
                var f = inp.files && inp.files[0];
                if (!box) return;
                if (!f) { box.innerHTML = original; return; }
                if (!/^image\/(jpeg|png|gif|webp)$/.test(f.type)) { reject('Please choose a JPG, PNG, GIF or WEBP image'); return; }
                if (f.size > 2 * 1024 * 1024) { reject('Photo must be 2 MB or smaller'); return; }
                var img = d.createElement('img');
                img.alt = '';
                img.src = URL.createObjectURL(f);
                box.innerHTML = '';
                box.appendChild(img);
            });
        });
    }

    function init() {
        toasts();
        confirms();
        passwords();
        photos();
        submitState();
        tables();
    }
    if (d.readyState === 'loading') d.addEventListener('DOMContentLoaded', init); else init();
})();
