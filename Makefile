MVN=mvn
PROJECT_NAME=java-profiler-boost
INSTALL_DIR=${HOME}/${PROJECT_NAME}

.PYTHON: clean
clean:
	$(MVN) clean
	cd ./java-profiler-extension && ${MVN} clean

.PYTHON: package
package: clean
	${MVN} package
	cd ./java-profiler-extension && ${MVN} package
	${MVN} package

.PYTHON: install
install: clean
	${MVN} install
	cd ./java-profiler-extension && ${MVN} install

.PYTHON: deploy
deploy: clean
	${MVN} deploy

.PYTHON: tar
tar:
	cd ${INSTALL_DIR} && tar -zcvf ${PROJECT_NAME}.tar.gz ./lib/ ./config/

.PYTHON: all
all: clean package tar