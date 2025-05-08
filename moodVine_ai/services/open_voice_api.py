# import os
# from flask import Blueprint, request, jsonify
# from werkzeug.utils import secure_filename
#
# from algorithms.open_voice import open_voice_service
#
# # 创建Flask蓝图
# open_voice_bp = Blueprint('open_voice', __name__)
#
# ALLOWED_EXTENSIONS = {'wav', 'mp3'}
#
#
# def allowed_file(filename):
#     return '.' in filename and \
#         filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS
#
#
# @open_voice_bp.route('/tts', methods=['POST'])
# def clone_voice():
#     # 检查文本输入
#     text = request.form.get('text')
#     if not text:
#         return jsonify({"error": "缺少文本参数"}), 400
#
#     # 检查音频文件
#     if 'file' not in request.files:
#         return jsonify({"error": "未上传参考音频文件"}), 400
#
#     file = request.files['file']
#     if file.filename == '':
#         return jsonify({"error": "空文件名"}), 400
#
#     if not allowed_file(file.filename):
#         return jsonify({"error": "不支持的文件格式"}), 400
#
#     try:
#         # 保存上传文件
#         upload_dir = os.path.join(os.getcwd(), 'static', 'uploads')
#         os.makedirs(upload_dir, exist_ok=True)
#         filepath = os.path.join(upload_dir, secure_filename(file.filename))
#         file.save(filepath)
#
#         # 执行语音克隆
#         output_path = open_voice_service.voice_clone(text, filepath)
#
#         # 返回生成的音频文件
#         return jsonify({
#             "message": "语音克隆成功",
#             "audio_path": output_path
#         })
#
#     except Exception as e:
#         return jsonify({"error": str(e)}), 500