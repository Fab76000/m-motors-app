/**
 * Affiche/masque les champs prix/mensualité selon le type de véhicule
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

/**
 * Ouvre la modal de switch et configure les champs selon le type actuel
 * @param {HTMLElement} button - Le bouton cliqué contenant les data-attributes
 */
function openSwitchModal(button) {
    const vehicleId = button.getAttribute('data-vehicle-id');
    const brand = button.getAttribute('data-vehicle-brand');
    const model = button.getAttribute('data-vehicle-model');
    const currentType = button.getAttribute('data-current-type');

    // Mettre à jour l'info véhicule
    document.getElementById('vehicleInfo').textContent = brand + ' ' + model;

    const currentTypeBadge = document.getElementById('currentType');
    const newTypeBadge = document.getElementById('newType');

    currentTypeBadge.textContent = currentType === 'ACHAT' ? 'Achat' : 'Location';

    // Colorer le badge actuel
    currentTypeBadge.className = 'badge ' + (currentType === 'ACHAT' ? 'bg-success' : 'bg-info');

    // Déterminer le nouveau type et le label
    newTypeBadge.textContent = currentType === 'ACHAT' ? 'Location' : 'Achat';

    // Colorer le badge nouveau type
    newTypeBadge.className = 'badge ' + (currentType === 'ACHAT' ? 'bg-info' : 'bg-success');

    // Adapter le label du champ
    const priceLabel = document.getElementById('priceLabel');
    const priceHelp = document.getElementById('priceHelp');

    if (currentType === 'ACHAT') {
        // ACHAT → LOCATION
        priceLabel.textContent = 'Mensualité (€/mois)';
        priceHelp.textContent = 'Entrez la mensualité en euros';
        document.getElementById('newPriceOrRent').placeholder = 'Ex: 450';
    } else {
        // LOCATION → ACHAT
        priceLabel.textContent = 'Prix de vente (€)';
        priceHelp.textContent = 'Entrez le prix de vente en euros';
        document.getElementById('newPriceOrRent').placeholder = 'Ex: 18500';
    }

    // Mettre à jour l'action du formulaire
    const form = document.getElementById('switchForm');
    form.action = '/admin/vehicles/' + vehicleId + '/switch';
}

/**
 * Ouvre la modal de rejet de dossier
 * @param button - Bouton "Rejeter" cliqué
 */
function openRejectModal(button) {
    const dossierId = button.getAttribute('data-dossier-id');
    document.getElementById('dossierRef').textContent = button.getAttribute('data-dossier-ref');

    const form = document.getElementById('rejectForm');
    form.action = '/admin/dossiers/' + dossierId + '/reject';

    document.getElementById('rejectionReason').value = '';
}
/**
 * Ouvre la modal de validation de dossier
 * @param button - Bouton "Valider" cliqué
 */
function openValidateModal(button) {
    const dossierId = button.getAttribute('data-dossier-id');
    const dossierRef = button.getAttribute('data-dossier-ref');
    const vehicleInfo = button.getAttribute('data-vehicle-info');

    document.getElementById('validateDossierRef').textContent = dossierRef;
    document.getElementById('validateVehicleInfo').textContent = vehicleInfo;

    const form = document.getElementById('validateForm');
    form.action = '/admin/dossiers/' + dossierId + '/validate';
}