FROM ubuntu:latest
LABEL authors="Rainnn"

ENTRYPOINT ["top", "-b"]

FROM python:3.8

# 设置工作目录
WORKDIR /moodVine_ai

# 复制文件需求并安装依赖
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# 复制源代码
COPY . .

# 暴露应用运行的端口
EXPOSE 5000

# 指定启动命令
CMD ["python", "app.py"]


