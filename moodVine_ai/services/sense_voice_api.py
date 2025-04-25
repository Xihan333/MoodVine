import os
from flask import Blueprint, request, jsonify
from werkzeug.utils import secure_filename

from algorithms.sense_voice import sense_voice_service

sense_voice_bp = Blueprint('sense_voice', __name__)

ALLOWED_EXTENSIONS = {'mp3', 'wav', 'm4a'}

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@sense_voice_bp.route('/transcribe', methods=['POST'])
def transcribe_audio():
    if 'file' not in request.files:
        return jsonify({"error": "未上传文件"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "空文件名"}), 400

    if not allowed_file(file.filename):
        return jsonify({"error": "不支持的文件格式"}), 400

    try:
        # 保存上传文件
        upload_dir = os.path.join(os.getcwd(), 'static', 'uploads')
        os.makedirs(upload_dir, exist_ok=True)
        filepath = os.path.join(upload_dir, secure_filename(file.filename))
        file.save(filepath)

        # 执行语音识别
        text = sense_voice_service.transcribe(filepath)
        return jsonify({"text": text})

    except Exception as e:
        return jsonify({"error": str(e)}), 500

