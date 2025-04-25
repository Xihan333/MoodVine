from funasr import AutoModel
import os
import subprocess
import torch

MODEL_PATH = "/models_shared/SenseVoiceSmall"


class SenseVoiceService:
    def __init__(self):
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.model = self._load_model()

    def _load_model(self):
        return AutoModel(
            model=MODEL_PATH,
            vad_model="fsmn-vad",
            vad_kwargs={"max_single_segment_time": 30000},
            device=self.device,
            disable_update=True,
        )

    def transcribe(self, audio_path):
        try:
            result = self.model.generate(
                input=audio_path,
                language="auto",
                batch_size_s=30,
            )
            return result[0]["text"]
        except Exception as e:
            raise RuntimeError(f"处理失败: {str(e)}")


sense_voice_service = SenseVoiceService()  # 单例实例
