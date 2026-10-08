from typing import Literal
from pydantic import BaseModel, Field

class TriageRequest(BaseModel):
    trieu_chung: str = Field(
        min_length=3,
        description="Triệu chứng do bệnh nhân nhập"
    )

class TriageResult(BaseModel):
    chuyen_khoa: str
    muc_do_khan_cap: Literal["THAP", "TRUNG_BINH", "CAO", "CAP_CUU"]
    giai_thich_ngan: str
    canh_bao: str
