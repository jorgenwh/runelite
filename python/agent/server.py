from __future__ import annotations

import asyncio
import json
import signal

import websockets

from agent.base import BaseAgent
from agent.protocol import Action, Observation


async def _handle(websocket, agent: BaseAgent) -> None:
    addr = websocket.remote_address
    print(f"[+] Client connected: {addr}")
    try:
        async for message in websocket:
            obs = Observation.from_dict(json.loads(message))
            action = agent.on_tick(obs)
            await websocket.send(json.dumps(action.to_dict()))
    except websockets.ConnectionClosed:
        print(f"[-] Client disconnected: {addr}")


async def serve(agent: BaseAgent, *, host: str = "localhost", port: int = 8765) -> None:
    """Start the agent WebSocket server."""
    loop = asyncio.get_running_loop()
    stop = loop.create_future()
    for sig in (signal.SIGINT, signal.SIGTERM):
        loop.add_signal_handler(sig, stop.set_result, None)

    async with websockets.serve(lambda ws: _handle(ws, agent), host, port):
        print(f"Agent server listening on ws://{host}:{port}")
        await stop

    print("\nShutting down.")
