import asyncio

from agent.demo import DemoAgent
from agent.server import serve


def main() -> None:
    asyncio.run(serve(DemoAgent()))


if __name__ == "__main__":
    main()
