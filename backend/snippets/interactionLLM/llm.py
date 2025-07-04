import requests
from gigachat import GigaChat
import requests
import uuid
import json
import os
import re
from IPython.display import display, Markdown
from dotenv import load_dotenv
from gigachat import GigaChat
class LLM:
    """
      Выполняет POST-запрос к эндпоинту, который выдает токен.
      Параметры:
      - auth_token (str): токен авторизации, необходимый для запроса.
      - область (str): область действия запроса API. По умолчанию — «GIGACHAT_API_PERS».
      Возвращает:
      - ответ API, где токен и срок его "годности".
    """
    @staticmethod
    def get_gigachat_token(auth_key, scope='GIGACHAT_API_PERS', model='GigaChat:latest'):
        giga = GigaChat(
            credentials=auth_key,
            scope=scope,
            model=model,
            #FIXME !!!!!!!!!!!!!!!!!!!!!!!!!!!!!установить сертификат
            verify_ssl_certs=False,
        )
        try:
            response = giga.get_token()
            return response.access_token
        except requests.RequestException as e:
            return None



    """
    Отправляет POST-запрос к API чата для получения ответа от модели GigaChat.

    Параметры:
    - auth_token (str): Токен для авторизации в API.
    - user_message (str): Сообщение от пользователя, для которого нужно получить ответ.

    Возвращает:
    - str: Ответ от API в виде текстовой строки.
    """
    @staticmethod
    def talk_to_chat(auth_token, user_message, model='GigaChat:latest'):
        giga = GigaChat(access_token=auth_token,
                        model=model,
                        verify_ssl_certs=False
                        )
        try:
            response = giga.chat(user_message)
            return response.choices[0].message.content
        except requests.RequestException as e:
            return None

