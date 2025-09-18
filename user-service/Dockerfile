FROM node:20.19.5-alpine

WORKDIR /app

COPY package*.json .
RUN npm install

COPY . .

EXPOSE 8001

CMD ["npm", "start"]
