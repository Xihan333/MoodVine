import torchaudio
from torchaudio.backend import soundfile_backend

# 强制注册SoundFile后端（如果未自动选择）
if not hasattr(torchaudio, 'load'):
    torchaudio.load = soundfile_backend.load
    torchaudio.save = soundfile_backend.save

# 验证实际使用的后端
print("实际使用的加载器:", torchaudio.load.__module__)  # 应显示soundfile_backend