from flask import Blueprint, request, jsonify
from algorithms.BLIP import blip_service
from algorithms.BLIP2 import blip2_service
import os
from werkzeug.utils import secure_filename
from config import Config

blip_bp = Blueprint('blip', __name__)

# 允许的图片扩展名
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@blip_bp.route('/blip-analyze-image', methods=['POST'])
def blip_analyze_image():
    """处理图片上传和分析请求
    支持传递图片URL（application/json）
    """
    try:
        data = request.get_json()
        image_url = data.get('image_url')
        if not image_url:
            return jsonify({"error": "Missing 'image_url'"}), 400

        description = blip_service.blip_analyze_image(image_url)
        return jsonify({"description": description})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

zhh
