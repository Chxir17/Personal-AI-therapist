# Используем официальный образ Python
FROM python:3.9-slim

# Устанавливаем FFmpeg (обязательно для Whisper)
RUN apt-get update && apt-get install -y ffmpeg && rm -rf /var/lib/apt/lists/*

# Рабочая директория
WORKDIR /app

# Копируем зависимости
COPY requirements.txt .

# Устанавливаем зависимости
RUN pip install --no-cache-dir -r requirements.txt

# Предзагружаем модель Whisper (опционально)
RUN python -c "import whisper; whisper.load_model('base')"

# Копируем код
COPY . .

# Запускаем сервер
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]