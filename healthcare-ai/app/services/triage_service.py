import json
import httpx
import re
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
Tim mạch
Da liễu
Tai Mũi Họng
Thần kinh
Tiêu hóa
Hô hấp
Cơ xương khớp
Nội tổng quát
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
    print("CONTENT BEFORE PARSE:")
    print(repr(content))
    
    # Tìm JSON object bất kể AI thêm text/markdown
    match = re.search(r'\{[\s\S]*?\}', content)
    if not match:
        raise ValueError("AI không trả về JSON object")
        
    json_text = match.group(0)
    print("JSON EXTRACTED:")
    print(json_text)
    
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
    
    async with httpx.AsyncClient(timeout=30.0) as client:
        response = await client.post(
            OPENROUTER_URL,
            headers=headers,
            json=payload
        )
        
    if response.status_code != 200:
        print("OPENROUTER ERROR:", response.text)
        raise RuntimeError(
            f"OpenRouter error "
            f"{response.status_code}: "
            f"{response.text}"
        )
        
    data = response.json()
    try:
        content = data["choices"][0]["message"]["content"]
        
        print("==============================")
        print("AI RAW RESPONSE:")
        print(content)
        print("==============================")
        
        parsed = extract_json(content)
        
        print("AI PARSED JSON:", parsed)
        return TriageResult(**parsed)
    except Exception as exc:
        print(
            "AI PARSE ERROR:",
            type(exc).__name__,
            str(exc)
        )
        raise RuntimeError(
            f"AI trả về dữ liệu không hợp lệ: {str(exc)}"
        ) from exc
