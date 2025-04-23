import requests
from PIL import Image
from transformers import Blip2Processor, Blip2ForConditionalGeneration
import torch

MODEL_PATH = "/models_shared/blip2-opt-2.7b"

class BLIP2Service:
    class BLIP2Service:
        def __init__(self):
            self.device = "cuda" if torch.cuda.is_available() else "cpu"
            self.processor = Blip2Processor.from_pretrained(MODEL_PATH)
            self.model = Blip2ForConditionalGeneration.from_pretrained(MODEL_PATH).to(self.device)

            if self.device == "cuda":
                self.model = self.model.half()

        def analyze_image(self, image_url):
            """分析图片并生成描述
            Args:
                image_path: 图片路径（URL）
            Returns:
                str: 生成的描述文本
            """
            prompt = "a detailed description of the scene:"
            try:
                # 支持图片URL
                response = requests.get(image_url, stream=True, timeout=10)
                response.raise_for_status()
                image = Image.open(response.raw).convert('RGB')

                # 条件生成或无条件生成
                inputs = self.processor(image, text=prompt, return_tensors="pt").to(self.device)

                if self.device == "cuda":
                    inputs = inputs.to(torch.float16)

                with torch.no_grad():
                    outputs = self.model.generate(
                        **inputs
                    )

                return self.processor.decode(outputs[0], skip_special_tokens=True)

            except Exception as e:
                raise RuntimeError(f"BLIP analysis failed: {str(e)}")

    # 单例模式（全局共享实例）
    blip2_service = BLIP2Service()
