const UserInfo = require("../models/userModel");

// Helper dùng chung
const getUserByUserId = async (userId) => {
    return await UserInfo.findOne({ where: { userId } });
};
//
// // Tạo user mới
// const addUser = async (req, res) => {
//     const { userId, phone, address } = req.body;
//
//     if (!userId || !phone || !address) {
//         return res.status(400).json({
//             success: false,
//             message: "Thiếu thông tin bắt buộc!",
//             dataResponse: null,
//         });
//     }
//
//     const newUser = await UserInfo.create({ userId, phone, address });
//
//     return res.status(200).json({
//         success: true,
//         message: "Thêm thông tin user thành công!",
//         dataResponse: newUser,
//     });
// };

// Lấy user theo ID
const getUserById = async (req, res) => {
    const { userId } = req.params;
    const user = await getUserByUserId(userId);

    if (!user) {
        return res.status(404).json({
            success: false,
            message: "Không tồn tại người dùng!",
            dataResponse: null,
        });
    }

    return res.status(200).json({
        success: true,
        message: "Lấy thông tin người dùng thành công!",
        dataResponse: user,
    });
};

// Cập nhật user
const updateUserById = async (req, res) => {
    const { userId } = req.params;
    const { phone, address } = req.body;

    if (!phone || !address) {
        return res.status(400).json({
            success: false,
            message: "Thiếu thông tin cần cập nhật!",
            dataResponse: null,
        });
    }

    const user = await getUserByUserId(userId);
    if (!user) {
        return res.status(404).json({
            success: false,
            message: "Không tìm thấy người dùng!",
            dataResponse: null,
        });
    }

    user.phone = phone;
    user.address = address;
    await user.save();

    return res.status(200).json({
        success: true,
        message: "Cập nhật thông tin thành công!",
        dataResponse: user,
    });
};

const addAddress = async (req, res) => {
    const { userId } = req.params;
    const { newAddress } = req.body;

    if (!newAddress) {
        return res.status(400).json({
            success: false,
            message: "Thiếu địa chỉ mới!",
            dataResponse: null,
        });
    }

    const user = await getUserByUserId(userId);
    if (!user) {
        return res.status(404).json({
            success: false,
            message: "Không tìm thấy người dùng!",
            dataResponse: null,
        });
    }

    const currentAddresses = Array.isArray(user.address)
        ? user.address
        : typeof user.address === "string"
            ? [user.address]
            : [];

    // Check địa chỉ trùng
    if (currentAddresses.includes(newAddress)) {
        return res.status(409).json({
            success: false,
            message: "Địa chỉ đã tồn tại!",
            dataResponse: currentAddresses,
        });
    }

    user.address = [...currentAddresses, newAddress];
    await user.save();

    return res.status(200).json({
        success: true,
        message: "Thêm địa chỉ thành công!",
        dataResponse: user.address,
    });
};


// Cập nhật địa chỉ
const updateAddress = async (req, res) => {
    const { userId } = req.params;
    const { oldAddress, newAddress } = req.body;

    const user = await getUserByUserId(userId);
    if (!user || !Array.isArray(user.address)) {
        return res.status(404).json({
            success: false,
            message: "Không tìm thấy người dùng hoặc không có địa chỉ!",
            dataResponse: null,
        });
    }

    const index = user.address.indexOf(oldAddress);
    if (index === -1) {
        return res.status(400).json({
            success: false,
            message: "Không tìm thấy địa chỉ cần cập nhật!",
            dataResponse: null,
        });
    }

    user.address[index] = newAddress;
    await user.save();

    return res.status(200).json({
        success: true,
        message: "Cập nhật địa chỉ thành công!",
        dataResponse: user.address,
    });
};

// Xóa địa chỉ
const deleteAddress = async (req, res) => {
    const { userId } = req.params;
    const { addressToDelete } = req.body;

    const user = await getUserByUserId(userId);
    if (!user || !Array.isArray(user.address)) {
        return res.status(404).json({
            success: false,
            message: "Không tìm thấy người dùng hoặc không có địa chỉ!",
            dataResponse: null,
        });
    }

    const index = user.address.indexOf(addressToDelete);
    if (index === -1) {
        return res.status(400).json({
            success: false,
            message: "Không tìm thấy địa chỉ cần xóa!",
            dataResponse: null,
        });
    }

    user.address.splice(index, 1);
    await user.save();

    return res.status(200).json({
        success: true,
        message: "Xóa địa chỉ thành công!",
        dataResponse: user.address,
    });
};

module.exports = {
    getUserById,
    updateUserById,
    addAddress,
    updateAddress,
    deleteAddress,
};
