.PHONY: clean build run server

clean:
	./gradlew cleanAll

build:
	./gradlew :client:build -x javadoc -x pmdMain

run:
	java -jar runelite-client/build/libs/client-*-shaded.jar

server:
	cd python && uv run agent_server.py
