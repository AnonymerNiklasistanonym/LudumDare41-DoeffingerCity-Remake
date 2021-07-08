.PHONY: clean clean_android clean_desktop clean_html
.PHONY: build_android build_android_debug build_desktop build_html
.PHONY: dist dist_android dist_desktop dist_html
.PHONY: doc
.PHONY: run_desktop run_html
.PHONY: setup_gradle

NAME=td-racing
VERSION=1.0
DIST_DIR=bin
DIST_FILE_ANDROID=$(DIST_DIR)/$(NAME)-android-$(VERSION).apk
DIST_FILE_DESKTOP=$(DIST_DIR)/$(NAME)-desktop-$(VERSION).jar
DIST_DIR_HTML=$(DIST_DIR)/$(NAME)-html-$(VERSION)
DIST_ZIP_HTML=$(DIST_DIR)/$(NAME)-html-$(VERSION).zip
JAVADOC_DIR=javadoc

all: dist_desktop

clean: clean_android clean_desktop clean_html
	# Clean gradlew build files in general additionally
	./gradlew clean
	# Remove the created dist directory
	rm -rf "$(DIST_DIR)"
	# Remove the created javadoc directory
	rm -rf "$(JAVADOC_DIR)"
	# Remove gradle build directory
	rm -rf "build"
	# Remove the gradle directory
	rm -rf ".gradle"

setup_gradle:
	gradle wrapper --gradle-version 6.7.1 --distribution-type bin

clean_android:
	# Run the gradle command to clean the desktop build files
	./gradlew android:clean
	# Remove the created APK file
	rm -f "$(DIST_FILE_ANDROID)"

clean_desktop:
	# Run the gradle command to clean the desktop build files
	./gradlew desktop:clean
	# Remove the created JAR file
	rm -f "$(DIST_FILE_DESKTOP)"

clean_html:
	# Run the gradle command to clean the html build files
	./gradlew html:clean
	# Remove the created HTML directory
	rm -rf "$(DIST_DIR_HTML)"
	# Remove the war directory that is not removed by gradle
	rm -rf "html/war"

build: build_android build_desktop build_html

build_android:
	# I recommend saving the following necessary variables in a shell file as
	# environment variables and the sourcing this file before executing this target
	# [Because of the singing step this is excluded from the default make call]
	@echo ----------------------------------------------------------------------------
	@echo You need to set the following environment variables to sign the APK:
	@echo KEY_STORE_PATH=\"$(KEY_STORE_PATH)\"
	@echo KEY_STORE_PASSWORD=\"$(KEY_STORE_PASSWORD)\"
	@echo KEY_ALIAS=\"$(KEY_ALIAS)\"
	@echo KEY_ALIAS_PASSWORD=\"$(KEY_ALIAS_PASSWORD)\"
	@echo ----------------------------------------------------------------------------
	# Run the gradle command to build a signed executable (APK file)
	./gradlew android:assembleRelease

build_android_debug:
	# Run the gradle command to build a debug executable (APK file)
	./gradlew android:assembleDebug

build_desktop:
	# Run the gradle command to build the desktop executable (JAR file)
	./gradlew desktop:dist

build_html:
	# Run the gradle command to build the html directory (can be hosted)
	./gradlew html:dist

dist: dist_android dist_desktop dist_html

dist_android: build_android
	# After building android copy the created apk into a new dist directory
	mkdir -p "$(DIST_DIR)"
	cp "android/build/outputs/apk/release/android-release.apk" "$(DIST_FILE_ANDROID)"

dist_desktop: build_desktop
	# After building desktop copy the created jar into a new dist directory
	mkdir -p "$(DIST_DIR)"
	cp "desktop/build/libs/desktop-$(VERSION).jar" "$(DIST_FILE_DESKTOP)"

dist_html: build_html
	# After building html copy the created directory into a new dist directory
	mkdir -p "$(DIST_DIR)"
	# Remove if existing the previously created HTML directory
	rm -rf "$(DIST_DIR_HTML)"
	cp -R "html/build/dist" "$(DIST_DIR_HTML)"
	cd "html/build/dist" && zip -r "../../../$(DIST_ZIP_HTML)" . -i "*"

doc:
	./gradlew core:javadoc desktop:javadoc html:javadoc
	mkdir -p "$(JAVADOC_DIR)"
	$(foreach SUB_PROJECT,html core desktop,cp -R "$(SUB_PROJECT)/build/docs/javadoc" "$(JAVADOC_DIR)/$(NAME)-$(SUB_PROJECT)-$(VERSION)";)

run_desktop: dist_desktop
	java -jar "$(DIST_FILE_DESKTOP)"

run_html: dist_html
	@echo Make sure to reload the page and disable caching when updating the code
	python -m http.server --directory "$(DIST_DIR_HTML)"

test:
	./gradlew --info test
