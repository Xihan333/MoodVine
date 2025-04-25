from flask import Flask
from config import Config
from services.deepseek import deepseek_bp
from services.blip_api import blip_bp
from services.sense_voice_api import sense_voice_bp


def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    # 注册蓝图
    app.register_blueprint(deepseek_bp, url_prefix='/ai')
    app.register_blueprint(blip_bp, url_prefix='/ai')
    app.register_blueprint(sense_voice_bp, url_prefix='/ai')

    # 基础路由
    @app.route('/')
    def home():
        return "MoodVine AI Service is Running"

    @app.route('/hello', methods=['GET'])
    def hello():
        return "hello"

    return app


if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=5000)
