from RealtimeSTT import AudioToTextRecorder

if __name__ == '__main__':
    recorder = AudioToTextRecorder()
    recorder.start()  # 开始录音
    input("Press Enter to stop recording...")  # 等待用户手动结束
    recorder.stop()  # 停止录音
    print("Transcription: ", recorder.text())  # 输出转录结果
