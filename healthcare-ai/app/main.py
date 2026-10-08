from fastapi import FastAPI, HTTPException
from app.schemas import TriageRequest, TriageResult
from app.services.triage_service import analyze_symptoms

app = FastAPI(
    title="Healthcare AI Service",
    version="1.0.0",
    description="AI Triage Service for Healthcare API"
)

@app.get("/")
async def root():
    return {
        "service": "Healthcare AI Service",
        "status": "running"
    }

@app.get("/health")
async def health():
    return {
        "status": "UP"
    }

@app.post(
    "/api/triage",
    response_model=TriageResult
)
async def triage(request: TriageRequest):
    try:
        result = await analyze_symptoms(
            request.trieu_chung
        )
        return result
    except RuntimeError as exc:
        raise HTTPException(
            status_code=502,
            detail=str(exc)
        )
