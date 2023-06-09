MVN=mvn
PROJECT_NAME=spring-startup-analyzer
INSTALL_DIR=${HOME}/${PROJECT_NAME}

.PYTHON: clean
clean:
	$(MVN) clean
	cd ./spring-profiler-extension && ${MVN} clean

.PYTHON: package
package: clean install
	${MVN} package
	cd ./spring-profiler-extension && ${MVN} package
	${MVN} package

.PYTHON: install
install: clean
	${MVN} install
	cd ./spring-profiler-extension && ${MVN} install

.PYTHON: deploy
deploy: clean
	${MVN} deploy

.PYTHON: tar
tar:
	cd ${INSTALL_DIR} && tar -zcvf ${PROJECT_NAME}.tar.gz ./lib/ ./config/

.PYTHON: all
all: clean install package tar