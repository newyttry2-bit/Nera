from fastapi import FastAPI, Query
import asyncio

app = FastAPI()

@app.get("/")
def root():
    return {"message": "API is alive"}

@app.get("/search")
async def search(q: str = Query(...)):
    try:
        from moviebox_api.v2 import Moviebox
        client = Moviebox()
        results = await client.search(q)
        return {"success": True, "results": results}
    except Exception as e:
        return {"success": False, "error": str(e)}

@app.get("/play")
async def play(query: str = Query(...)):
    try:
        from moviebox_api.v2 import Moviebox
        client = Moviebox()
        results = await client.search(query)
        if not results:
            return {"success": False, "error": "No results"}
        first = results[0]
        slug = first.get("slug") or first.get("id")
        if not slug:
            return {"success": False, "error": "No slug"}
        details = await client.get_details(slug)
        url = details.get("stream_url")
        if not url:
            return {"success": False, "error": "No stream URL"}
        return {"success": True, "title": first.get("title"), "url": url}
    except Exception as e:
        return {"success": False, "error": str(e)}
