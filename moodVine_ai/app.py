import requests
from flask import Flask, request, jsonify

app = Flask(__name__)

DEEPSEEK_API_URL = "http://10.2.8.77:3000/v1/chat/completions"  # 替换为真实地址
DEEPSEEK_API_KEY = "sk-93nWYhI8SrnXad5m9932CeBdDeDf4233B21d93D217095f22"  # 替换为真实API Key


@app.route('/test-deepseek', methods=['POST'])
def test_deepseek():
    try:
        # 1. 获取请求数据
        request_data = request.get_json()
        if not request_data:
            return jsonify({"error": "请求数据必须是JSON"}), 400

        # 2. 调用DeepSeek API
        headers = {
            "Authorization": f"Bearer {DEEPSEEK_API_KEY}",
            "Content-Type": "application/json"
        }
        response = requests.post(
            DEEPSEEK_API_URL,
            headers=headers,
            json=request_data,
            timeout=30,
            proxies={"http": None, "https": None}  # 禁用代理
        )

        # 3. 处理响应
        if response.status_code == 200:
            return jsonify(response.json())
        else:
            return jsonify({
                "error": "API请求失败",
                "status_code": response.status_code,
                "response": response.text  # 返回原始响应以便调试
            }), response.status_code

    except Exception as e:
        return jsonify({
            "error": "服务器内部错误",
            "message": str(e)
        }), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
