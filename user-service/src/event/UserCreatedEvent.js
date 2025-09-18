const { kafka } = require('./connectKafka');
const { Partitioners } = require('kafkajs'); // 👈 cần import thêm

const producer = kafka.producer({
    createPartitioner: Partitioners.LegacyPartitioner
});

const initProducer = async () => {
    await producer.connect();
};

const sendUserCreatedEvent = async (userId) => {
    await producer.send({
        topic: 'userCreated',
        messages: [
            {
                value: JSON.stringify({ userId }),
                headers: {
                    '__TypeId__': 'userCreated'
                }
            }
        ]
    });
};

module.exports = {
    initProducer,
    sendUserCreatedEvent,
};
