from pydub import AudioSegment
try:
    audio = AudioSegment.from_mp3("resources/example_reference.mp3")
    print("pydub 可读取 MP3！")
except Exception as e:
    print("pydub 不支持 MP3:", e)