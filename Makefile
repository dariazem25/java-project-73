setup:
	gradle wrapper --gradle-version 7.6

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew installDist

start-dist:
	./build/install/app/bin/app

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

check-updates:
	./gradlew dependencyUpdates