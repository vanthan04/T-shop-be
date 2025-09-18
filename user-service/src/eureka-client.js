const Eureka = require('eureka-js-client').Eureka;

const client = new Eureka({
    instance: {
        app: 'USER-SERVICE',
        hostName: process.env.SERVICE_HOST,
        ipAddr: process.env.SERVICE_HOST,
        port: {
            '$': process.env.SERVER_PORT,
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
        host: process.env.EUREKA_HOST,
        port: process.env.EUREKA_PORT,
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
