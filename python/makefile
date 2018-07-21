clean: 
	rm -rf ../python-dist
deps:
	echo '[Build] install python requirements.'
	if [ ! -d 'dist' ]; then mkdir dist; fi
	pip install -r requirements.txt -U -t ./dist/
	echo '[Build] installation of python requirements is done.'
build:
	echo '[Build] pack python modules.'
	rm -rf ./dist/
	make deps
	if [ -d '../python-dist' ]; then rm -rf ../python-dist/; fi
	mkdir ../python-dist
	cp -r mock/* ./dist/
	mv dist ../python-dist/
	cp -r ../conf/ ../python-dist/
	echo '[Build] python modules are all in python-dist.'