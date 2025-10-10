class FastFoodSimulator {
    constructor() {
        this.stompClient = null;
        this.isConnected = false;
        this.isRunning = false;

        this.stats = {
            totalCustomers: 0,
            processedOrders: 0,
            completedOrders: 0,
            customerCounter: 0,
        };

        this.init();
    }

    init() {
        this.bindEvents();
        this.connectWebSocket();
        this.updateStatistics();
    }

    bindEvents() {
        document.getElementById('startBtn').addEventListener('click', () => this.startSimulation());
        document.getElementById('endBtn').addEventListener('click', () => this.endSimulation());
        document.getElementById('clearLogsBtn').addEventListener('click', () => this.clearLogs());
        document.getElementById('customerArrivalTime').addEventListener('change', (e) => this.validateInput(e.target));
        document.getElementById('waiterServingTime').addEventListener('change', (e) => this.validateInput(e.target));
        document.getElementById('kitchenCompletionTime').addEventListener('change', (e) => this.validateInput(e.target));
    }

    validateInput(input) {
        const value = parseInt(input.value);
        const min = parseInt(input.min);
        const max = parseInt(input.max);
        if (value < min || value > max) {
            alert(`Значение должно быть между ${min} и ${max} мс`);
        }
    }

    connectWebSocket() {
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, () => {
            this.isConnected = true;
            this.addLog('WebSocket подключен', 'system');
            this.subscribeToChannels();
        }, (error) => {
            this.addLog('WebSocket connection error','system');
            this.reconnect();
        });
    }

    reconnect() {
        setTimeout(() => {
            this.addLog('Попытка переподключения...', 'system');
            this.connectWebSocket();
        }, 5000);
    }

    subscribeToChannels() {
        const channels = {
            '/frontend/customercreation': (message) => this.logCustomerCreation(message),
            '/frontend/startcreation': (message) => this.logOrderStart(message),
            '/frontend/endcreation': (message) => this.logOrderEnd(message),
            '/frontend/startcooking': (message) => this.logCookingStart(message),
            '/frontend/endcooking': (message) => this.logCookingEnd(message),
            '/frontend/serving': (message) => this.logOrderServing(message),
            '/frontend/kitchen': (message) => this.logKitchenError(message),
            '/frontend/waiter': (message) => this.logWaiterError(message)
        };
        Object.entries(channels).forEach(([channel, loger]) => {
            this.stompClient.subscribe(channel, (message) => {
                loger(message.body);
            });
        });
    }

    logCustomerCreation(message) {
        this.addLog(message, 'customer');
        this.checkWaiters();
        this.stats.totalCustomers++;
        this.stats.customerCounter++;
        this.updateCustomerQueue(1);
        this.addToWaiterCustomerList(message);
        this.updateStatistics();
    }

    logOrderStart(message) {
        this.addLog(message, 'order');
        this.addCurrentOrderList(this.extractOrderId(message));
        this.updateCustomerQueue(-1);
        this.removeFromWaiterCustomerList();
    }

    logOrderEnd(message) {
        this.addLog(message, 'order');
        this.checkCooks();
        this.stats.processedOrders++;
        this.removeCurrentOrderList(this.extractOrderId(message));
        this.updateKitchenQueue(1);
        this.addToKitchenOrderList(message);
        this.updateWaitingCustomers(1);
        this.addToPickupCustomerList(message);
        this.updateStatistics();
    }

    logCookingStart(message) {
        this.addLog(message, 'kitchen');
        this.addCookingOrderList(this.extractOrderId(message));
        this.updateKitchenQueue(-1);
        this.removeFromKitchenOrderList();
    }

    logCookingEnd(message) {
        this.addLog(message, 'kitchen');
        this.removeCookingOrderList(this.extractOrderId(message));
        this.updateReadyOrder(this.extractOrderId(message));

        setTimeout(() => {
            this.clearReadyOrder();
        }, 2000);
    }

    logOrderServing(message) {
        this.addLog(message, 'serving');
        this.stats.completedOrders++;
        this.updateWaitingCustomers(-1);
        this.removeFromPickupCustomerList();
        this.updateStatistics();
    }

    checkWaiters(message) {
        const countElement = document.getElementById('customerQueueCount');
        let currentCount = parseInt(countElement.textContent) || 0;
        if(currentCount>=20){
            this.addLog('Ошибка у официанта.', 'error');
            this.endSimulation();
        }
    }

    checkCooks(message) {
        const countElement = document.getElementById('kitchenOrdersCount');
        let currentCount = parseInt(countElement.textContent) || 0;
        if(currentCount>=20){
            this.addLog('Ошибка на кухне.', 'error');
            this.endSimulation();
        }
    }

    startSimulation() {
        if (!this.isConnected) {
            console.log('Нет подключения к серверу');
            return;
        }

        const customerArrivalTime = document.getElementById('customerArrivalTime').value;
        const waiterServingTime = document.getElementById('waiterServingTime').value;
        const kitchenCompletionTime = document.getElementById('kitchenCompletionTime').value;
        const waiterCount = document.getElementById('waiterCount').value;
        const cookCount = document.getElementById('cookCount').value;

        if (!this.validateSimulationParameters(customerArrivalTime, waiterServingTime, kitchenCompletionTime, waiterCount, cookCount)) {
            return;
        }

        this.resetStatistics();

        fetch(`/simulation/start?kitchenCompletionTime=${kitchenCompletionTime}&waiterServingTime=${waiterServingTime}&customerArrivalTime=${customerArrivalTime}&waiterCount=${waiterCount}&cookCount=${cookCount}`)
            .then(response => {
                if (response.ok) {
                    this.setSimulationState(true);
                    this.addLog('Симуляция запущена', 'system');
                    this.clearAllData();
                } else {
                    throw new Error('Ошибка запуска симуляции');
                }
            })
            .catch(error => {
                console.error(error);
            });
    }

    endSimulation() {
        fetch('/simulation/end')
            .then(response => {
                if (response.ok) {
                    this.setSimulationState(false);
                    this.addLog('Симуляция остановлена', 'system');
                    this.clearAllData();
                    this.resetStatistics();
                }
            })
            .catch(error => {
                console.error(error);
            });
    }

    validateSimulationParameters(customer, waiter, kitchen, waiterCount, cookCount) {
        if (customer < 1000 || waiter < 1000 || kitchen < 1000) {
            console.log('Все интервалы должны быть не менее 1000 мс');
            return false;
        }
        if (customer > 10000 || waiter > 10000 || kitchen > 10000) {
            console.log('Все интервалы должны быть не более 10000 мс');
            return false;
        }
        if (waiterCount < 1 || cookCount < 1) {
            console.log('Количество официантов и поваров должно быть не менее 1');
            return false;
        }
        return true;
    }

    setSimulationState(running) {
        this.isRunning = running;
        document.getElementById('startBtn').disabled = running;
        document.getElementById('endBtn').disabled = !running;
        document.getElementById('simulationStatus').textContent = running ? 'Запущена' : 'Остановлена';
    }

    addLog(message, type = 'system') {
        const logContainer = document.getElementById('logContainer');
        const logEntry = document.createElement('div');
        logEntry.className = `log-entry log-${type}`;

        const timestamp = new Date().toLocaleTimeString();
        logEntry.innerHTML = `<strong>[${timestamp}]</strong> ${message}`;

        logContainer.appendChild(logEntry);
        logContainer.scrollTop = logContainer.scrollHeight;
    }

    clearLogs() {
        const logContainer = document.getElementById('logContainer');
        logContainer.innerHTML = '';
        this.addLog("Логи очищены");
    }

    updateCustomerQueue(change) {
        const countElement = document.getElementById('customerQueueCount');
        let currentCount = parseInt(countElement.textContent) || 0;
        currentCount = Math.max(0, currentCount + change);
        countElement.textContent = currentCount;
    }

    addCurrentOrderList(orderId) {
        const listElement = document.getElementById('currentOrdersList');
        const listItem = document.createElement('div');
        listItem.className = 'queue-item';
        listItem.textContent = `Заказ №${orderId}`;
        listItem.setAttribute('data-order-id', orderId);
        listElement.appendChild(listItem);
    }
    removeCurrentOrderList(orderId) {
        const currentList = document.getElementById('currentOrdersList');
        const orderElement = currentList.querySelector(`[data-order-id="${orderId}"]`);
        if (orderElement) {
            currentList.removeChild(orderElement);
        }
    }

    addToWaiterCustomerList(message) {
        const customerName = this.extractCustomerName(message);
        const listElement = document.getElementById('waiterCustomerList');
        const listItem = document.createElement('div');
        listItem.className = 'queue-item';
        listItem.textContent = customerName;
        listItem.setAttribute('data-customer-id', this.stats.customerCounter);
        listElement.appendChild(listItem);
    }

    removeFromWaiterCustomerList() {
        const listElement = document.getElementById('waiterCustomerList');
        if (listElement.firstChild) {
            listElement.firstChild.remove();
        }
    }

    updateKitchenQueue(change) {
        const countElement = document.getElementById('kitchenOrdersCount');
        let currentCount = parseInt(countElement.textContent) || 0;
        currentCount = Math.max(0, currentCount + change);
        countElement.textContent = currentCount;
    }

    addCookingOrderList(orderId) {
        const listElement = document.getElementById('cookingOrdersList');
        const listItem = document.createElement('div');
        listItem.className = 'queue-item';
        listItem.textContent = `Заказ №${orderId}`;
        listItem.setAttribute('data-order-id', orderId);
        listElement.appendChild(listItem);
    }
    removeCookingOrderList(orderId) {
        const cookingList = document.getElementById('cookingOrdersList');
        const orderElement = cookingList.querySelector(`[data-order-id="${orderId}"]`);
        if (orderElement) {
            cookingList.removeChild(orderElement);
        }
    }

    addToKitchenOrderList(message) {
        const orderId = this.extractOrderId(message);
        const listElement = document.getElementById('kitchenOrderList');
        const listItem = document.createElement('div');
        listItem.className = 'queue-item';
        listItem.textContent = orderId;
        listItem.setAttribute('data-order-id', orderId);
        listElement.appendChild(listItem);
    }

    removeFromKitchenOrderList() {
        const listElement = document.getElementById('kitchenOrderList');
        if (listElement.firstChild) {
            listElement.firstChild.remove();
        }
    }

    updateWaitingCustomers(change) {
        const countElement = document.getElementById('waitingCustomers');
        let currentCount = parseInt(countElement.textContent) || 0;
        currentCount = Math.max(0, currentCount + change);
        countElement.textContent = currentCount;
    }

    updateReadyOrder(orderId) {
        document.getElementById('readyOrder').textContent = orderId;
    }

    addToPickupCustomerList(message) {
        const customerName = this.extractCustomerName(message);
        const listElement = document.getElementById('pickupCustomerList');
        const listItem = document.createElement('div');
        listItem.className = 'queue-item';
        listItem.textContent = customerName;
        listItem.setAttribute('data-customer-id', this.stats.customerCounter);
        listElement.appendChild(listItem);
    }

    removeFromPickupCustomerList() {
        const listElement = document.getElementById('pickupCustomerList');
        if (listElement.firstChild) {
            listElement.firstChild.remove();
        }
    }

    clearReadyOrder() {
        document.getElementById('readyOrder').textContent = '-';
    }

    updateStatistics() {
        document.getElementById('totalCustomers').textContent = this.stats.totalCustomers;
        document.getElementById('processedOrders').textContent = this.stats.processedOrders;
        document.getElementById('completedOrders').textContent = this.stats.completedOrders;
    }

    resetStatistics() {
        this.stats = {
            totalCustomers: 0,
            processedOrders: 0,
            completedOrders: 0,
            customerCounter: 0
        };
        this.updateStatistics();
    }

    clearAllData() {
        document.getElementById('customerQueueCount').textContent = '0';
        document.getElementById('currentOrdersList').textContent = '';
        document.getElementById('waiterCustomerList').innerHTML = '';
        document.getElementById('kitchenOrdersCount').textContent = '0';
        document.getElementById('cookingOrdersList').textContent = '';
        document.getElementById('kitchenOrderList').innerHTML = '';
        document.getElementById('waitingCustomers').textContent = '0';
        document.getElementById('readyOrder').textContent = '-';
        document.getElementById('pickupCustomerList').innerHTML = '';
    }

    extractOrderId(message) {
        const match = message.match(/Айди заказа: (\d+)/);
        return match ? match[1] : null;
    }

    extractCustomerName(message) {
        const words = message.split(' ');
        return words.length > 0 ? words[0] : null;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    window.simulator = new FastFoodSimulator();
});