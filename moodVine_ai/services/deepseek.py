from flask import Blueprint, request, jsonify
import requests
from config import Config

deepseek_bp = Blueprint('deepseek', __name__)

@deepseek_bp.route('/test-deepseek', methods=['POST'])
def test_deepseek():
    """处理DeepSeek聊天请求"""
    try:
        # 1. 验证输入
        data = request.get_json()
        if not data or 'messages' not in data:
            return jsonify({"error": "Missing 'messages' in request"}), 400

        # 2. 调用DeepSeek API
        headers = {
            "Authorization": f"Bearer {Config.DEEPSEEK_API_KEY}",
            "Content-Type": "application/json"
        }
        response = requests.post(
            Config.DEEPSEEK_API_URL,
            headers=headers,
            json=data,
            timeout=30
        )

        # 3. 返回标准化响应
        if response.ok:
            return jsonify(response.json())
        return jsonify({
            "error": "DeepSeek API error",
            "status": response.status_code,
            "message": response.text
        }), response.status_code

    except Exception as e:
        return jsonify({"error": str(e)}), 500