import torch
from PIL import Image
from transformers import Blip2Processor, Blip2ForConditionalGeneration

# 配置参数
MODEL_PATH = "/models_shared/blip2-opt-2.7b"
IMAGE_PATH = "../static/1.jpg"

def load_model():
    """加载模型到GPU并返回处理器和模型"""
    device = "cuda" if torch.cuda.is_available() else "cpu"
    print(f"✅ 正在使用设备: {device.upper()}")

    # 加载处理器和模型
    processor = Blip2Processor.from_pretrained(MODEL_PATH)
    print("✅ 加载模型成功")
    model = Blip2ForConditionalGeneration.from_pretrained(
        MODEL_PATH,
        device_map={"": device},
        torch_dtype=torch.float16
    )
    return processor, model, device


def generate_caption(image_path, processor, model, device):
    """生成图像描述"""
    try:
        text = "a detailed description of the scene:"
        # 加载图像并预处理
        image = Image.open(image_path).convert("RGB")
        inputs = processor(image, text, return_tensors="pt").to(device, torch.float16)

        # 生成描述
        with torch.no_grad():
            outputs = model.generate(
                **inputs,
                max_length=150,
                num_beams=5,
                min_new_tokens=20,
                repetition_penalty=1.5,
                length_penalty=1.0,
            )

            return processor.decode(outputs[0], skip_special_tokens=True)

    except Exception as e:
        raise RuntimeError(f"生成描述失败: {str(e)}")


if __name__ == "__main__":
    try:
        # 初始化
        processor, model, device = load_model()

        # 生成描述
        caption = generate_caption(IMAGE_PATH, processor, model, device)
        print(f"\n🖼️ 图像描述: {caption}")

        # 显存监控
        if device == "cuda":
            print(f"🔥 显存占用: {torch.cuda.memory_allocated() / 1024 ** 2:.2f} MB")

    except FileNotFoundError:
        print(f"❌ 文件未找到，请检查路径: {IMAGE_PATH}")
    except RuntimeError as e:
        print(f"❌ 运行时错误: {str(e)}")
        if "CUDA out of memory" in str(e):
            print("💡 尝试: 1. 减小图像尺寸 2. 关闭其他占用显存的程序 3. 使用--no-half禁用半精度")
