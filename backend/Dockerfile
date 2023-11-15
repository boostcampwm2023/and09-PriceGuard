FROM node:lts-alpine

WORKDIR /usr/src/app

COPY ./cd-test-nest/package*.json ./

RUN npm install

COPY ./cd-test-nest/. .

RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]
