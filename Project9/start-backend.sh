#!/bin/bash

echo "========================================"
echo "  启动后端服务"
echo "========================================"

cd backend

if [ ! -d "venv" ]; then
    echo "创建 Python 虚拟环境..."
    python3 -m venv venv
fi

echo "激活虚拟环境..."
source venv/bin/activate

echo "安装依赖..."
pip install -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple

echo "启动 Flask 服务..."
echo "服务地址: http://localhost:5000"
echo ""
python run.py
