FROM node:20.19.5-alpine

WORKDIR /app

COPY package.json .
RUN npm install

COPY src .

EXPOSE 8001

ENTRYPOINT ["npm", "start"]
