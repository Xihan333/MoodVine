from flask import Blueprint, request, jsonify, Response
import requests
from config import Config
import json

deepseek_bp = Blueprint('deepseek', __name__)

# def generate_stream(api_response):
#     """生成SSE格式的流数据"""
#     for line in api_response.iter_lines():
#         if line:
#             decoded_line = line.decode('utf-8')
#             if decoded_line.startswith("data:"):
#                 json_data = decoded_line[5:].strip()
#                 if json_data != "[DONE]":
#                     try:
#                         chunk = json.loads(json_data)
#                         if "choices" in chunk and chunk["choices"]:
#                             content = chunk["choices"][0].get("delta", {}).get("content", "")
#                             if content:
#                                 yield f"data: {json.dumps({'content': content})}\n\n"
#                     except json.JSONDecodeError:
#                         pass
#     yield "data: [DONE]\n\n"
#
#
# @deepseek_bp.route('/stream-deepseek', methods=['POST'])
# def stream_deepseek():
#     try:
#         data = request.get_json()
#         data['stream'] = True  # 确保 DeepSeek API 启用流式
#
#         headers = {
#             "Authorization": f"Bearer {Config.DEEPSEEK_API_KEY}",
#             "Content-Type": "application/json",
#         }
#
#         # 调用 DeepSeek API（流式）
#         api_response = requests.post(
#             Config.DEEPSEEK_API_URL,
#             headers=headers,
#             json=data,
#             stream=True,
#             timeout=30
#         )
#
#         # 返回 SSE 流式响应
#         def generate():
#             for line in api_response.iter_lines():
#                 if line:
#                     decoded = line.decode('utf-8')
#                     if decoded.startswith("data:"):
#                         yield decoded + "\n\n"  # 确保符合 SSE 格式
#
#         return Response(
#             generate(),
#             mimetype="text/event-stream",
#             headers={"Cache-Control": "no-cache"}
#         )
#
#     except Exception as e:
#         return jsonify({"error": str(e)}), 500
#

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