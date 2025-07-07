import { Sequelize } from 'sequelize';
require("dotenv").config();

export const sequelize = new Sequelize(process.env.POSTGRES_DB_URI );

