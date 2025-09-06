const { kafka } = require('./connectKafka');

const producer = kafka.producer();

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
