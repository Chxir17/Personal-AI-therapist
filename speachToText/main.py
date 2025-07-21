from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import requests
import subprocess
import whisper
import tempfile
import io
import os

app = FastAPI()
model = whisper.load_model("base")

class AudioRequest(BaseModel):
    url: str

@app.post("/transcribe")
def transcribe_audio(req: AudioRequest):
    response = requests.get(req.url, headers={"User-Agent": "Mozilla/5.0"})
    if response.status_code != 200:
        raise HTTPException(status_code=400, detail="Ошибка загрузки файла")

    with tempfile.NamedTemporaryFile(suffix=".oga") as oga_temp:
        oga_temp.write(response.content)
        oga_temp.flush()

        # Теперь Whisper сам прочитает и конвертирует
        result = model.transcribe(oga_temp.name, language="en")

    return {"text": result["text"]}