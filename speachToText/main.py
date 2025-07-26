from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import requests
import whisper
import tempfile
import os
import ffmpeg

app = FastAPI()
model = whisper.load_model("base")

class AudioRequest(BaseModel):
    url: str

def convert_to_wav(input_path, output_path):
    try:
        (
            ffmpeg
            .input(input_path)
            .output(output_path, acodec='pcm_s16le', ac=1, ar='16000')
            .run(quiet=True, overwrite_output=True)
        )
        return True
    except ffmpeg.Error as e:
        print(f"FFmpeg error: {e.stderr.decode()}")
        return False

@app.post("/transcribe")
def transcribe_audio(req: AudioRequest):
    try:
        response = requests.get(req.url, headers={"User-Agent": "Mozilla/5.0"})
        if response.status_code != 200:
            raise HTTPException(status_code=400, detail="Ошибка загрузки файла")

        with tempfile.NamedTemporaryFile(suffix=".oga", delete=False) as oga_temp:
            oga_temp.write(response.content)
            oga_temp.flush()
            oga_path = oga_temp.name

        wav_path = oga_path + ".wav"
        if not convert_to_wav(oga_path, wav_path):
            raise HTTPException(status_code=500, detail="Ошибка конвертации аудио")

        result = model.transcribe(wav_path, language="ru")  # Измените на нужный язык

        # Удаляем временные файлы
        os.unlink(oga_path)
        os.unlink(wav_path)

        return {"text": result["text"]}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))