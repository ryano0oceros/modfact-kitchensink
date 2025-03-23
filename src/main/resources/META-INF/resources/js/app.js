document.addEventListener('alpine:init', () => {
    Alpine.store('flash', {
        messages: [],
        add(message) {
            this.messages.push(message);
            setTimeout(() => {
                this.messages = this.messages.filter(m => m !== message);
            }, 5000);
        }
    });
});

// Handle HTMX flash messages
document.addEventListener('showMessage', (evt) => {
    Alpine.store('flash').add(evt.detail);
});

// Reset form after successful submission
document.addEventListener('htmx:afterRequest', (evt) => {
    if (evt.detail.successful && evt.target.tagName === 'FORM') {
        evt.target.reset();
    }
}); 