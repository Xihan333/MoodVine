from funasr import AutoModel
import os
import subprocess

# 确保使用Conda环境的FFmpeg
os.environ["PATH"] = f"/home/deepseek/anaconda3/envs/moodvine/bin:{os.environ['PATH']}"


def validate_ffmpeg():
    try:
        subprocess.run(["ffmpeg", "-version"], check=True, capture_output=True)
        return True
    except:
        return False


if not validate_ffmpeg():
    raise RuntimeError("FFmpeg 未正确配置！")

# 初始化模型
model = AutoModel(
    model="/models_shared/SenseVoiceSmall",
    vad_model="fsmn-vad",
    vad_kwargs={"max_single_segment_time": 30000},
    device="cuda:1",
    hub="hf",
    disable_update=True,
)


def process_audio(audio_path):
    # 尝试直接处理
    try:
        return model.generate(
            input=audio_path,
            cache={},
            language="auto",
            use_itn=True,
            batch_size_s=60,
            merge_vad=True,
            merge_length_s=15,
            audio_format="mp3",
        )
    except Exception as e:
        print(f"直接处理失败，尝试转换格式... 错误：{e}")

        # 转换音频格式
        wav_path = audio_path.replace(".mp3", ".wav")
        subprocess.run([
            "ffmpeg", "-i", audio_path,
            "-ar", "16000", "-ac", "1", "-acodec", "pcm_s16le",
            wav_path
        ], check=True)

        return model.generate(input=wav_path)


# 使用绝对路径
audio_path = os.path.abspath("../static/zh.mp3")
res = process_audio(audio_path)
print(res[0]["text"])
