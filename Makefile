.PHONY: clean dist

all: build_desktop

clean:
	$(MAKE) -C "td-racing-remake" clean
	$(MAKE) -C "web-page" clean

build_desktop:
	# This builds the desktop version
	$(MAKE) -C "td-racing-remake" dist_desktop

dist:
	# This builds the desktop, html and android version + the project website
	$(MAKE) -C "td-racing-remake" dist
	$(MAKE) -C "web-page" web_page
