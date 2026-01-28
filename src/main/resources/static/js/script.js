/**
 * Affiche/masque les champs prix/loyer selon le type de véhicule
 */
function togglePriceFields() {
    const type = document.getElementById('type').value;
    const priceCard = document.getElementById('priceCard');
    const rentCard = document.getElementById('rentCard');
    const priceInput = document.getElementById('price');
    const rentInput = document.getElementById('monthlyRent');

    if (type === 'ACHAT') {
        priceCard.style.display = 'block';
        rentCard.style.display = 'none';
        priceInput.required = true;
        rentInput.required = false;
        rentInput.value = '';
    } else if (type === 'LOCATION') {
        priceCard.style.display = 'none';
        rentCard.style.display = 'block';
        priceInput.required = false;
        priceInput.value = '';
        rentInput.required = true;
    } else {
        priceCard.style.display = 'none';
        rentCard.style.display = 'none';
        priceInput.required = false;
        rentInput.required = false;
    }
}