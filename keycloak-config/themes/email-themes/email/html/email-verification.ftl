<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác minh tài khoản</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            color: #333;
            background: #f6f6f6;
            padding: 20px;
        }
        .container {
            background: white;
            padding: 30px;
            max-width: 600px;
            margin: auto;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        .logo {
            display: block;
            margin: 0 auto 20px;
            max-height: 60px;
        }
        .btn {
            background-color: #4CAF50;
            color: white;
            padding: 14px 20px;
            text-decoration: none;
            border-radius: 6px;
            display: inline-block;
            margin-top: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <img class="logo" src="https://res.cloudinary.com/dfruwhu2f/image/upload/v1750432807/logo_ga0c1y.png" alt="Logo"/>

    <h2>Chào ${user.firstName!user.username},</h2>

    <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>T-shop</strong>.</p>

    <p>Vui lòng xác minh địa chỉ email của bạn bằng cách nhấn nút bên dưới:</p>

    <p style="text-align:center;">
        <a href="${link}" class="btn">Xác minh tài khoản</a>
    </p>

    <p>Nếu bạn không đăng ký tài khoản, hãy bỏ qua email này.</p>
</div>
</body>
</html>
