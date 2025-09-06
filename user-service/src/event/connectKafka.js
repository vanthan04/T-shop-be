const { Kafka } = require('kafkajs');

const kafka = new Kafka({
    clientId: 'user-service',
    brokers: ['localhost:29092'],
});

module.exports = { kafka };
