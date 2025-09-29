var stompClient = null;

document.addEventListener("DOMContentLoaded", async () => {
    await connectWebSocket();
});

window.addEventListener("beforeunload", () => {
    disconnectWebSocket();
});

document.getElementById("start").addEventListener("click", () => {
    startSimulation(parseInt(document.getElementById("kitchenCompletionTime").value),parseInt(document.getElementById("customerArrivalTime").value), parseInt(document.getElementById("waiterServingTime").value));
});

document.getElementById("end").addEventListener("click", () => {
    endSimulation();
});

async function connectWebSocket() {
    return new Promise((resolve) => {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({},() => {
            stompClient.subscribe('/frontend/customercreation', (message) => {
                logMessage(message);
            });
            stompClient.subscribe('/frontend/startcreation', (message) => {
                logMessage(message);
            });
            stompClient.subscribe('/frontend/endcreation', (message) => {
                logMessage(message);
            });
            stompClient.subscribe('/frontend/startcooking', (message) => {
                logMessage(message);
            });
            stompClient.subscribe('/frontend/endcooking', (message) => {
                logMessage(message);
            });
            stompClient.subscribe('/frontend/serving', (message) => {
                logMessage(message);
            });
            stompClient.subscribe('/frontend/kitchen', (message) => {
                console.log(message);
                endSimulation();
            });
            stompClient.subscribe('/frontend/waiter', (message) => {
                console.log(message);
                endSimulation();
            });
            resolve();
        });
    });
}

function disconnectWebSocket() {
    stompClient.disconnect();
}

function logMessage(message){
    console.log(message);
}

function startSimulation(kitchenCompletionTime, customerArrivalTime, waiterServingTime){
    kitchenCompletionTime=3000;
    customerArrivalTime=5000;
    waiterServingTime=2000;
    fetch(`http://localhost:8080/simulation/start?kitchenCompletionTime=${kitchenCompletionTime}&customerArrivalTime=${customerArrivalTime}&waiterServingTime=${waiterServingTime}`, {
        mode: 'no-cors'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Ошибка запроса');
        }
    })
    .catch(error => {
        console.log(error);
    });
}
function endSimulation(){
    fetch(`http://localhost:8080/simulation/end`, {
        mode: 'no-cors'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Ошибка запроса');
        }
    })
    .catch(error => {
        console.log(error);
    });
}