const { Sequelize, DataTypes } = require("sequelize");
const sequelize = require("../untils/connect-db");

const UserInfo = sequelize.define(
    "UserInfo",
    {
        userId: {
            type: DataTypes.STRING,
            allowNull: false,
            primaryKey: true,
        },
        address: {
            type: DataTypes.STRING,
        },
        phone: {
            type: DataTypes.STRING,
        },
        isVerify: {
            type: DataTypes.BOOLEAN,
            defaultValue: false,
        },
    },
    {
        tableName: "userinfo",
        timestamps: true,
        underscored: true,
    }
);

module.exports = UserInfo;
