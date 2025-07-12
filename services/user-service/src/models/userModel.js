const { Sequelize, DataTypes } = require("sequelize");
const sequelize = require("../untils/connect-db");

const UserInfo = sequelize.define(
    "UserInfo",
    {
        userId: {
            type: DataTypes.UUID,
            allowNull: false,
            primaryKey: true,
        },
        address: {
            type: DataTypes.ARRAY(DataTypes.STRING),
        },
        phone: {
            type: DataTypes.STRING,
        },
    },
    {
        tableName: "user_info",
        timestamps: true,
        underscored: true,
    }
);

module.exports = UserInfo;
