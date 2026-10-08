import json
import httpx
import re
import asyncio
import logging

logger = logging.getLogger(__name__)
from app.config import settings
from app.schemas import TriageResult

OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions"

SYSTEM_PROMPT = """Bạn là hệ thống ĐIỀU HƯỚNG LỊCH KHÁM.
Bạn KHÔNG chẩn đoán bệnh.
Bạn KHÔNG xác định bệnh mà người dùng mắc phải.
Bạn KHÔNG kê đơn hoặc đề xuất thuốc.
Nhiệm vụ duy nhất của bạn là:
- Dựa trên mô tả triệu chứng để chọn khoa khám phù hợp.
- Xác định mức ưu tiên để người dùng biết nên đặt lịch thông thường  hay tìm chăm sóc y tế khẩn cấp.
- Giải thích ngắn gọn vì sao nên đến chuyên khoa đó.
Chỉ chọn một trong các chuyên khoa sau:
Nội tổng quát
Tim mạch
Thần kinh
Da liễu
Nhi khoa
Mắt
Xương khớp
Tai mũi họng
Mức độ khẩn cấp chỉ được là:
THAP
TRUNG_BINH
CAO
CAP_CUU
Không đưa ra tên bệnh có thể mắc.
Không viết chẩn đoán.
Bạn BẮT BUỘC phải trả về duy nhất một JSON object.
Ví dụ:
{
  "chuyen_khoa": "Tim mạch",
  "muc_do_khan_cap": "CAO",
  "giai_thich_ngan": "Các triệu chứng được mô tả phù hợp để được đánh giá tại chuyên khoa Tim mạch.",
  "canh_bao": "Nếu triệu chứng nghiêm trọng, đột ngột hoặc xấu đi, hãy tìm hỗ trợ y tế khẩn cấp."
}
Không markdown.
Không dùng ```json.
Không chào hỏi.
Không thêm nội dung trước JSON.
Không thêm nội dung sau JSON."""

def extract_json(content: str) -> dict:
    content = content.strip()
    
    # Tìm JSON object bất kể AI thêm text/markdown
    match = re.search(r'\{[\s\S]*?\}', content)
    if not match:
        raise ValueError("AI không trả về JSON object")
        
    json_text = match.group(0)
    
    return json.loads(json_text)

async def analyze_symptoms(symptoms: str) -> TriageResult:
    payload = {
        "model": settings.OPENROUTER_MODEL,
        "messages": [
            {
                "role": "system",
                "content": SYSTEM_PROMPT
            },
            {
                "role": "user",
                "content": f"""Hãy phân loại yêu cầu đặt lịch sau.
Mô tả của người dùng:
{symptoms}

Chỉ trả về JSON theo schema đã quy định."""
            }
        ],
        "temperature": 0.1
    }
    
    headers = {
        "Authorization": f"Bearer {settings.OPENROUTER_API_KEY}",
        "Content-Type": "application/json"
    }
    
    if not settings.OPENROUTER_API_KEY:
        logger.error("OPENROUTER_API_KEY is missing")
        raise RuntimeError("AI service is not configured")

    # Retry only temporary upstream errors. Do not retry auth, invalid models, or 400.
    try:
        async with httpx.AsyncClient(timeout=httpx.Timeout(25.0, connect=8.0)) as client:
            for attempt in range(3):
                try:
                    response = await client.post(OPENROUTER_URL, headers=headers, json=payload)
                    if response.status_code in (429, 500, 502, 503, 504) and attempt < 2:
                        logger.warning("OpenRouter temporary HTTP %s (attempt %s)", response.status_code, attempt + 1)
                        await asyncio.sleep(1.5 * (attempt + 1))
                        continue
                    break
                except (httpx.TimeoutException, httpx.TransportError) as exc:
                    logger.warning("OpenRouter network error: %s", type(exc).__name__)
                    if attempt == 2:
                        raise RuntimeError("AI provider connection temporarily unavailable") from exc
                    await asyncio.sleep(1.5 * (attempt + 1))
    except httpx.HTTPError as exc:
        raise RuntimeError("AI provider request failed") from exc

    if response.status_code != 200:
        logger.error("OpenRouter returned HTTP %s", response.status_code)
        raise RuntimeError(f"AI provider returned HTTP {response.status_code}")

    data = response.json()
    try:
        content = data["choices"][0]["message"]["content"]
        
        
        parsed = extract_json(content)
        
        return TriageResult(**parsed)
    except Exception as exc:
        logger.error("AI response validation failed: %s", type(exc).__name__)
        raise RuntimeError(
            "AI response format is invalid"
        ) from exc
