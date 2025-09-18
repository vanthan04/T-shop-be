<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt lại mật khẩu</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f4f4;
            padding: 20px;
            color: #333;
        }
        .container {
            background-color: #ffffff;
            max-width: 600px;
            margin: auto;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        .logo {
            display: block;
            margin: 0 auto 20px;
            max-height: 60px;
        }
        h2 {
            text-align: center;
        }
        .btn {
            background-color: #1976d2;
            color: white;
            padding: 14px 20px;
            text-decoration: none;
            border-radius: 6px;
            display: inline-block;
            text-align: center;
            margin: 20px auto;
        }
    </style>
</head>
<body>
<div class="container">
    <img class="logo" src="/logo.png" alt="Logo"/>

    <h2>Đặt lại mật khẩu</h2>

    <p>Xin chào ${user.firstName!user.username},</p>

    <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản E-commerce của bạn.</p>

    <p>Vui lòng nhấn vào nút bên dưới để thay đổi mật khẩu:</p>

    <p style="text-align:center;">
        <a href="${link}" class="btn">Đặt lại mật khẩu</a>
    </p>

    <p>Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email này. Tài khoản của bạn vẫn an toàn.</p>

</div>
</body>
</html>
