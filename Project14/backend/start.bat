@echo off
echo ====================================
echo   小区物业系统 - 后端服务启动
echo ====================================
echo.

cd /d "%~dp0"

if not exist venv (
    echo 正在创建虚拟环境...
    python -m venv venv
)

echo 正在激活虚拟环境...
call venv\Scripts\activate.bat

echo 正在安装依赖...
pip install -r requirements.txt -q

echo 正在启动服务...
echo.
echo 服务地址: http://localhost:5000
echo API文档: http://localhost:5000/api/
echo.
echo 测试账号:
echo   管理员: admin / admin123
echo   业主:   owner1 / 123456
echo.
echo 按 Ctrl+C 停止服务
echo ====================================
echo.

python app.py
