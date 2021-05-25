.PHONY: clean clean_desktop clean_html build_desktop build_html

VERSION=1.0

all: export_desktop export_html

clean: clean_desktop clean_html

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

build_desktop:
	# Run the gradle command to build the desktop exectuable (JAR file)
	./gradlew desktop:dist

build_html:
	# Run the gradle command to build the html directory (can be hosted)
	./gradlew html:dist

export_desktop: build_desktop
	# After building desktop copy the created jar into a new bin directory
	mkdir -p bin
	cp desktop/build/libs/desktop-$(VERSION).jar bin/desktop-$(VERSION).jar

export_html: build_html
	# After building html copy the created directory into a new bin directory
	mkdir -p bin
	cp -R html/build/dist bin/html-$(VERSION)
