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

.PHONY: clean-skip-tests
clean-skip-tests:
ifeq ($(strip $(VERSION)),)
	$(MVN) clean -Dmaven.test.skip=true
	cd ./spring-profiler-extension && $(MVN) clean -Dmaven.test.skip=true
else
	$(MVN) clean -Drevision=$(VERSION) -Dmaven.test.skip=true
	cd ./spring-profiler-extension && $(MVN) clean -Drevision=$(VERSION) -Dmaven.test.skip=true
endif

.PHONY: package
package: clean install
ifeq ($(strip $(VERSION)),)
	${MVN} package -pl '!coverage-report-aggregate,!spring-startup-cli'
	cd ./spring-profiler-extension && ${MVN} package
else
	${MVN} package -Drevision=$(VERSION) -pl '!coverage-report-aggregate,!spring-startup-cli'
	cd ./spring-profiler-extension && ${MVN} package -Drevision=$(VERSION)
endif
	mkdir -p ./build && rm -rf ./build/* && cp -r ~/${PROJECT_NAME}/* ./build/ && rm -rf ./build/${PROJECT_NAME}.tar.gz


.PHONY: package-skip-tests
package-skip-tests: clean-skip-tests install-skip-tests
ifeq ($(strip $(VERSION)),)
	${MVN} package -pl '!coverage-report-aggregate,!spring-startup-cli' -Dmaven.test.skip=true
	cd ./spring-profiler-extension && ${MVN} package -Dmaven.test.skip=true
else
	${MVN} package -Drevision=$(VERSION) -pl '!coverage-report-aggregate,!spring-startup-cli' -Dmaven.test.skip=true
	cd ./spring-profiler-extension && ${MVN} package -Drevision=$(VERSION) -Dmaven.test.skip=true
endif
	mkdir -p ./build && rm -rf ./build/* && cp -r ~/${PROJECT_NAME}/* ./build/ && rm -rf ./build/${PROJECT_NAME}.tar.gz


.PHONY: install
install: clean
ifeq ($(strip $(VERSION)),)
	${MVN} install -pl '!coverage-report-aggregate,!spring-startup-cli'
	cd ./spring-profiler-extension && ${MVN} install
else
	${MVN} install -Drevision=$(VERSION) -pl '!coverage-report-aggregate,!spring-startup-cli'
	cd ./spring-profiler-extension && ${MVN} install -Drevision=$(VERSION)
endif

.PHONY: install-skip-tests
install-skip-tests: clean-skip-tests
ifeq ($(strip $(VERSION)),)
	${MVN} install -pl '!coverage-report-aggregate,!spring-startup-cli' -Dmaven.test.skip=true
	cd ./spring-profiler-extension && ${MVN} install -Dmaven.test.skip=true
else
	${MVN} install -Drevision=$(VERSION) -pl '!coverage-report-aggregate,!spring-startup-cli' -Dmaven.test.skip=true
	cd ./spring-profiler-extension && ${MVN} install -Drevision=$(VERSION) -Dmaven.test.skip=true
endif

.PHONY: deploy
deploy: clean
ifeq ($(strip $(VERSION)),)
	${MVN} deploy -pl '!coverage-report-aggregate,!spring-startup-cli'
	cd ./spring-profiler-extension && ${MVN} deploy
else
	${MVN} deploy -Drevision=$(VERSION)
	cd ./spring-profiler-extension && ${MVN} deploy -Drevision=$(VERSION)
endif

.PHONY: deploy-skip-tests
deploy-skip-tests: clean-skip-tests
ifeq ($(strip $(VERSION)),)
	${MVN} deploy -pl '!coverage-report-aggregate,!spring-startup-cli' -Dmaven.test.skip=true
	cd ./spring-profiler-extension && ${MVN} deploy -Dmaven.test.skip=true
else
	${MVN} deploy -Drevision=$(VERSION)
	cd ./spring-profiler-extension && ${MVN} deploy -Drevision=$(VERSION) -Dmaven.test.skip=true
endif

.PHONY: docker-build
docker-build: package
ifeq ($(strip $(VERSION)),)
	docker build --tag ${PROJECT_NAME}:latest .
else
	docker build --tag ${PROJECT_NAME}:${VERSION} .
endif


.PHONY: tar
tar:
	cd ${INSTALL_DIR} && tar -zcvf ${PROJECT_NAME}.tar.gz ./lib/ ./config/ ./template

.PHONY: all
all: clean install package tar

.PHONY: all-skip-tests
all-skip-tests: clean-skip-tests install-skip-tests package-skip-tests tar