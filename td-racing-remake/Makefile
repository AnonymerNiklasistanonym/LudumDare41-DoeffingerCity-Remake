.PHONY: clean clean_android clean_desktop clean_html
.PHONY: build_android build_desktop build_html
.PHONY: dist dist_android dist_desktop dist_html
.PHONY: run_desktop run_html

VERSION=1.0

all: dist_desktop

clean: clean_android clean_desktop clean_html
	# Clean gradlew build files in general additionally
	./gradlew clean
	# Remove the created bin directory
	rm -rf bin/

clean_android:
	# Run the gradle command to clean the desktop build files
	./gradlew android:clean
	# Remove the created JAR file
	rm -f bin/android-$(VERSION).apk

clean_desktop:
	# Run the gradle command to clean the desktop build files
	./gradlew desktop:clean
	# Remove the created JAR file
	rm -f bin/desktop-$(VERSION).jar

clean_html:
	# Run the gradle command to clean the html build files
	./gradlew html:clean
	# Remove the created HTML directory
	rm -rf bin/html-$(VERSION)

build: build_android build_desktop build_html

build_android:
	# I recommend saving the following necessary variables in a shell file as
	# environment variables and the sourcing this file before executing this target
	# [Because of the singing step this is excluded from the default make call]
	@echo "----------------------------------------------------------------------------"
	@echo "You need to set the following environment variables to sign the APK:"
	@echo "KEY_STORE_PATH=$(KEY_STORE_PATH)"
	@echo "KEY_STORE_PASSWORD=$(KEY_STORE_PASSWORD)"
	@echo "KEY_ALIAS=$(KEY_ALIAS)"
	@echo "KEY_ALIAS_PASSWORD=$(KEY_ALIAS_PASSWORD)"
	@echo "----------------------------------------------------------------------------"
	# Run the gradle command to build a signed executable (APK file)
	./gradlew android:assembleRelease

build_desktop:
	# Run the gradle command to build the desktop executable (JAR file)
	./gradlew desktop:dist

build_html:
	# Run the gradle command to build the html directory (can be hosted)
	./gradlew html:dist

dist: dist_android dist_desktop dist_html

dist_android: build_android
	# After building android copy the created apk into a new bin directory
	mkdir -p bin
	cp android/build/outputs/apk/release/android-release.apk bin/android-$(VERSION).apk

dist_desktop: build_desktop
	# After building desktop copy the created jar into a new bin directory
	mkdir -p bin
	cp desktop/build/libs/desktop-$(VERSION).jar bin/desktop-$(VERSION).jar

dist_html: build_html
	# After building html copy the created directory into a new bin directory
	mkdir -p bin
	cp -R html/build/dist bin/html-$(VERSION)

run_desktop: dist_desktop
	java -jar bin/desktop-$(VERSION).jar

run_html: dist_html
	cd bin/html-$(VERSION); \
	python -m http.server
