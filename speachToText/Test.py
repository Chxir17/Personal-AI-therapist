from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

class TextRequest(BaseModel):
    text: str

@app.post("/echo")
async def echo_string(request: TextRequest):
    return {"received_text": request.text}