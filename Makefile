MVN=mvn
PROJECT_NAME=spring-startup-analyzer
INSTALL_DIR=${HOME}/${PROJECT_NAME}

.PHONY: clean
clean:
ifeq ($(strip $(VERSION)),)
	$(MVN) clean
	cd ./spring-profiler-extension && $(MVN) clean
else
	$(MVN) clean -Drevision=$(VERSION)
	cd ./spring-profiler-extension && $(MVN) clean -Drevision=$(VERSION)
endif

.PHONY: package
package: clean install
ifeq ($(strip $(VERSION)),)
	${MVN} package
	cd ./spring-profiler-extension && ${MVN} package
else
	${MVN} package -Drevision=$(VERSION)
	cd ./spring-profiler-extension && ${MVN} package -Drevision=$(VERSION)
endif

.PHONY: install
install: clean
ifeq ($(strip $(VERSION)),)
	${MVN} install
	cd ./spring-profiler-extension && ${MVN} install
else
	${MVN} install -Drevision=$(VERSION)
	cd ./spring-profiler-extension && ${MVN} install -Drevision=$(VERSION)
endif

.PHONY: deploy
deploy: clean
ifeq ($(strip $(VERSION)),)
	${MVN} deploy
	cd ./spring-profiler-extension && ${MVN} deploy
else
	${MVN} deploy -Drevision=$(VERSION)
	cd ./spring-profiler-extension && ${MVN} deploy -Drevision=$(VERSION)
endif


.PHONY: tar
tar:
	cd ${INSTALL_DIR} && tar -zcvf ${PROJECT_NAME}.tar.gz ./lib/ ./config/ ./template

.PHONY: all
all: clean install package tar