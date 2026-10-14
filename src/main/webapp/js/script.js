/**
 * ElectroBill - Client Side Controller
 * Handles form validation, asynchronous POST communication with ElectricityBillServlet,
 * dynamic rendering of progressive slab breakdown, and print action.
 */
document.addEventListener('DOMContentLoaded', () => {
    // DOM Elements
    const billForm = document.getElementById('electricityBillForm');
    const consumerNameInput = document.getElementById('consumerName');
    const consumerNumberInput = document.getElementById('consumerNumber');
    const unitsInput = document.getElementById('units');

    const consumerNameError = document.getElementById('consumerNameError');
    const consumerNumberError = document.getElementById('consumerNumberError');
    const unitsError = document.getElementById('unitsError');

    const errorAlert = document.getElementById('errorAlert');
    const errorMessageText = document.getElementById('errorMessageText');

    const loadingState = document.getElementById('loadingState');
    const resultCard = document.getElementById('resultCard');

    const resultDate = document.getElementById('resultDate');
    const resultConsumerName = document.getElementById('resultConsumerName');
    const resultConsumerNumber = document.getElementById('resultConsumerNumber');
    const resultTotalUnits = document.getElementById('resultTotalUnits');
    const resultTotalAmount = document.getElementById('resultTotalAmount');
    const slabTableBody = document.getElementById('slabTableBody');

    const resetBtn = document.getElementById('resetBtn');
    const printBtn = document.getElementById('printBtn');
    const newCalculationBtn = document.getElementById('newCalculationBtn');
    const presetChips = document.querySelectorAll('.chip-btn');

    // Quick Test Chips listener
    presetChips.forEach(chip => {
        chip.addEventListener('click', () => {
            if (!consumerNameInput.value.trim()) {
                consumerNameInput.value = 'Rahul Sharma';
            }
            if (!consumerNumberInput.value.trim()) {
                consumerNumberInput.value = 'EB-2026-8849';
            }
            unitsInput.value = chip.dataset.units;
            clearValidation();
            submitCalculation();
        });
    });

    // Real-time input clearing
    [consumerNameInput, consumerNumberInput, unitsInput].forEach(input => {
        input.addEventListener('input', () => {
            input.classList.remove('has-error');
            hideAlert();
        });
    });

    // Reset Button Handler
    resetBtn.addEventListener('click', () => {
        billForm.reset();
        clearValidation();
        hideAlert();
        resultCard.style.display = 'none';
        consumerNameInput.focus();
    });

    // Print Button Handler
    printBtn.addEventListener('click', () => {
        window.print();
    });

    // Calculate Another Handler
    newCalculationBtn.addEventListener('click', () => {
        resultCard.style.display = 'none';
        unitsInput.value = '';
        unitsInput.focus();
        billForm.scrollIntoView({ behavior: 'smooth' });
    });

    // Form Submit Handler
    billForm.addEventListener('submit', (e) => {
        e.preventDefault();
        submitCalculation();
    });

    /**
     * Performs client-side validation and sends HTTP POST to ElectricityBillServlet
     */
    async function submitCalculation() {
        clearValidation();
        hideAlert();

        const name = consumerNameInput.value.trim();
        const number = consumerNumberInput.value.trim();
        const unitsStr = unitsInput.value.trim();

        // 1. Client-Side Validation
        let hasError = false;

        if (!name) {
            showFieldError(consumerNameInput, consumerNameError, 'Please enter the consumer name.');
            hasError = true;
        } else if (name.length < 2) {
            showFieldError(consumerNameInput, consumerNameError, 'Consumer name must be at least 2 characters.');
            hasError = true;
        }

        if (!number) {
            showFieldError(consumerNumberInput, consumerNumberError, 'Please enter the consumer account/meter number.');
            hasError = true;
        }

        if (unitsStr === '') {
            showFieldError(unitsInput, unitsError, 'Please enter units consumed.');
            hasError = true;
        } else {
            const unitsVal = parseFloat(unitsStr);
            if (isNaN(unitsVal)) {
                showFieldError(unitsInput, unitsError, 'Units must be a valid numeric value.');
                hasError = true;
            } else if (unitsVal < 0) {
                showFieldError(unitsInput, unitsError, 'Units consumed cannot be negative (must be >= 0).');
                hasError = true;
            }
        }

        if (hasError) {
            showAlert('Please correct the highlighted errors above.');
            return;
        }

        // 2. Prepare Form Data for Server-Side POST
        const formData = new URLSearchParams();
        formData.append('consumerName', name);
        formData.append('consumerNumber', number);
        formData.append('units', unitsStr);

        // 3. UI State: Show Loading
        loadingState.style.display = 'flex';
        resultCard.style.display = 'none';

        try {
            const response = await fetch('ElectricityBillServlet', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                    'Accept': 'application/json'
                },
                body: formData.toString()
            });

            const data = await response.json();

            loadingState.style.display = 'none';

            if (!response.ok || !data.success) {
                const errorMsg = data.errorMessage || 'Failed to calculate bill. Please check inputs.';
                showAlert(errorMsg);
                return;
            }

            // 4. Render Bill Result Card
            renderBillResult(data);

        } catch (error) {
            console.error('Servlet request error:', error);
            loadingState.style.display = 'none';
            showAlert('Unable to connect to the ElectricityBillServlet. Ensure the application is deployed on Apache Tomcat.');
        }
    }

    /**
     * Renders progressive bill calculation details returned by the servlet
     */
    function renderBillResult(data) {
        resultConsumerName.textContent = data.consumerName;
        resultConsumerNumber.textContent = data.consumerNumber;
        resultTotalUnits.textContent = `${data.formattedTotalUnits} kWh`;
        resultTotalAmount.textContent = Number(data.totalAmount).toLocaleString('en-IN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
        resultDate.textContent = data.billDate || new Date().toLocaleString('en-IN');

        // Clear and rebuild slab breakdown rows
        slabTableBody.innerHTML = '';

        if (data.slabs && Array.isArray(data.slabs)) {
            data.slabs.forEach(slab => {
                const tr = document.createElement('tr');
                const isApplicable = slab.unitsConsumed > 0;
                tr.className = isApplicable ? 'active-slab' : 'inactive-slab';

                tr.innerHTML = `
                    <td><strong>${escapeHtml(slab.slabTitle)}</strong></td>
                    <td>${escapeHtml(slab.slabRange)}</td>
                    <td class="text-right">${slab.formattedUnits} kWh</td>
                    <td class="text-right">${slab.formattedRate}</td>
                    <td class="text-right"><strong>${slab.formattedAmount}</strong></td>
                `;
                slabTableBody.appendChild(tr);
            });
        }

        // Display Result Card and Scroll Into View smoothly
        resultCard.style.display = 'block';
        resultCard.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    function showFieldError(input, errorElement, message) {
        input.classList.add('has-error');
        errorElement.textContent = message;
    }

    function clearValidation() {
        [consumerNameInput, consumerNumberInput, unitsInput].forEach(input => {
            input.classList.remove('has-error');
        });
        consumerNameError.textContent = '';
        consumerNumberError.textContent = '';
        unitsError.textContent = '';
    }

    function showAlert(message) {
        errorMessageText.textContent = message;
        errorAlert.style.display = 'flex';
        errorAlert.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    function hideAlert() {
        errorAlert.style.display = 'none';
        errorMessageText.textContent = '';
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});
