const Eureka = require('eureka-js-client').Eureka;

// Configure Eureka client
const client = new Eureka({
    instance: {
        app: 'USER-SERVICE',
        hostName: 'localhost',
        ipAddr: '127.0.0.1',
        port: {
            '$': 8001,
            '@enabled': 'true',
        },
        vipAddress: 'USER-SERVICE',
        dataCenterInfo: {
            '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
            name: 'MyOwn',
        },
        preferIpAddress: true
    },
    eureka: {
        host: 'localhost',
        port: 8761,
        servicePath: '/eureka/apps/',
    }
});

// Start Eureka client
client.start(error => {
    if (error) {
        console.error('Eureka client failed to start:', error);
    } else {
        console.log('Eureka client started successfully');
    }
});
