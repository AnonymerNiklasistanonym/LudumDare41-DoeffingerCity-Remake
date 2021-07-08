.PHONY: clean run_web_page

NAME=td-racing
VERSION=1.0
DIST_DIR_WEB_PAGE=bin

all: web_page

clean:
	rm -rf "$(DIST_DIR_WEB_PAGE)"

web_page: clean
	mkdir -p "$(DIST_DIR_WEB_PAGE)"
	# Copy website files
	cp -R css "$(DIST_DIR_WEB_PAGE)"
	cp -R js "$(DIST_DIR_WEB_PAGE)"
	cp -R favicon "$(DIST_DIR_WEB_PAGE)"
	cp index.html "$(DIST_DIR_WEB_PAGE)"
	# Copy HTML build
	cp -R "../td-racing-remake/bin/$(NAME)-html-$(VERSION)" "$(DIST_DIR_WEB_PAGE)/html"
	# Copy JavaDocs
	mkdir -p "$(DIST_DIR_WEB_PAGE)/javadoc"
	$(foreach SUB_PROJECT,html core desktop,cp -R "../td-racing-remake/javadoc/$(NAME)-$(SUB_PROJECT)-$(VERSION)" "$(DIST_DIR_WEB_PAGE)/javadoc/$(NAME)-$(SUB_PROJECT)-$(VERSION)";)

run_web_page: web_page
	python -m http.server --directory "$(DIST_DIR_WEB_PAGE)"
