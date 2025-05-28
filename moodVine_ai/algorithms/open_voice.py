# import os
# import torch
# from openvoice import se_extractor
# from openvoice.api import BaseSpeakerTTS, ToneColorConverter
# from flask import Blueprint, request, jsonify
# from werkzeug.utils import secure_filename
#
#
# class OpenVoiceService:
#     def __init__(self):
#         self.device = "cuda:1" if torch.cuda.is_available() else "cpu"
#         self.ckpt_base = '/models_shared/checkpoints/base_speakers/EN'
#         self.ckpt_converter = '/models_shared/checkpoints/converter'
#         self.output_dir = 'outputs'
#
#         # 初始化模型
#         self.base_speaker_tts = None
#         self.tone_color_converter = None
#         self.source_se = None
#
#         self._load_models()
#         os.makedirs(self.output_dir, exist_ok=True)
#
#     def _load_models(self):
#         """加载所有必要的模型"""
#         self.base_speaker_tts = BaseSpeakerTTS(
#             f'{self.ckpt_base}/config.json',
#             device=self.device
#         )
#         self.base_speaker_tts.load_ckpt(f'{self.ckpt_base}/checkpoint.pth')
#
#         self.tone_color_converter = ToneColorConverter(
#             f'{self.ckpt_converter}/config.json',
#             device=self.device
#         )
#         self.tone_color_converter.load_ckpt(f'{self.ckpt_converter}/checkpoint.pth')
#
#         self.source_se = torch.load(f'{self.ckpt_base}/en_default_se.pth').to(self.device)
#
#     def voice_clone(self, text, reference_speaker_path):
#         """执行语音克隆"""
#         try:
#             # 提取目标说话人特征
#             target_se, _ = se_extractor.get_se(
#                 reference_speaker_path,
#                 self.tone_color_converter,
#                 target_dir='processed',
#                 vad=True
#             )
#
#             # 生成临时音频文件路径
#             src_path = os.path.join(self.output_dir, 'tmp.wav')
#             output_path = os.path.join(self.output_dir, 'output.wav')
#
#             # 生成基础语音
#             self.base_speaker_tts.tts(
#                 text,
#                 src_path,
#                 speaker='default',
#                 language='English',
#                 speed=1.0
#             )
#
#             # 转换音色
#             encode_message = "@MyShell"
#             self.tone_color_converter.convert(
#                 audio_src_path=src_path,
#                 src_se=self.source_se,
#                 tgt_se=target_se,
#                 output_path=output_path,
#                 message=encode_message
#             )
#
#             return output_path
#
#         except Exception as e:
#             raise RuntimeError(f"语音克隆失败: {str(e)}")
#
#
# open_voice_service = OpenVoiceService()  # 单例实例
