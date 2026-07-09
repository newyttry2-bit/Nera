from fastapi import FastAPI, Query, HTTPException
from fastapi.responses import JSONResponse
from moviebox_api.v2 import Moviebox
import asyncio

app = FastAPI(title="Moviebox API", description="Working Moviebox API using Simatwa/moviebox-api")

@app.get("/")
async def root():
    return {"message": "Moviebox API is running. Use /search?q=Avatar or /play?query=Avatar"}

@app.get("/search")
async def search(q: str = Query(..., description="Search query")):
    """Search for movies and TV shows"""
    try:
        client = Moviebox()
        results = await client.search(q)
        return {"success": True, "results": results}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/detail")
async def detail(slug: str = Query(..., description="Movie slug or ID")):
    """Get movie details including stream URL"""
    try:
        client = Moviebox()
        details = await client.get_details(slug)
        return {"success": True, "details": details}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/stream")
async def stream(slug: str = Query(..., description="Movie slug or ID")):
    """Get direct stream URL for a movie"""
    try:
        client = Moviebox()
        details = await client.get_details(slug)
        stream_url = details.get("stream_url")
        if not stream_url:
            return {"success": False, "message": "No stream URL found"}
        return {"success": True, "url": stream_url}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/play")
async def play(query: str = Query(..., description="Movie name to search and play")):
    """Search and directly return stream URL for the first result"""
    try:
        client = Moviebox()
        results = await client.search(query)
        if not results:
            return {"success": False, "message": "No results found"}
        
        first = results[0]
        slug = first.get("slug") or first.get("id")
        if not slug:
            return {"success": False, "message": "No slug found"}
        
        details = await client.get_details(slug)
        stream_url = details.get("stream_url")
        if not stream_url:
            return {"success": False, "message": "No stream URL found"}
        
        return {
            "success": True,
            "title": first.get("title"),
            "slug": slug,
            "url": stream_url
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
