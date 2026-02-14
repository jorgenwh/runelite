import asyncio
import json
import signal
import websockets

PORT = 8765


async def handler(websocket):
    addr = websocket.remote_address
    print(f"[+] Client connected: {addr}")
    try:
        async for message in websocket:
            obs = json.loads(message)
            print(
                f"  tick={obs['tick']:>6d}  "
                f"hp={obs['hp']}/{obs['hp_max']}  "
                f"prayer={obs['prayer']}/{obs['prayer_max']}  "
                f"pos=({obs['x']}, {obs['y']}, {obs['plane']})"
            )
            # Return a no-op action
            await websocket.send(json.dumps({"type": "none"}))
    except websockets.ConnectionClosed:
        print(f"[-] Client disconnected: {addr}")


async def main():
    loop = asyncio.get_running_loop()
    stop = loop.create_future()
    for sig in (signal.SIGINT, signal.SIGTERM):
        loop.add_signal_handler(sig, stop.set_result, None)

    async with websockets.serve(handler, "localhost", PORT):
        print(f"Agent server listening on ws://localhost:{PORT}")
        await stop

    print("\nShutting down.")


if __name__ == "__main__":
    asyncio.run(main())
