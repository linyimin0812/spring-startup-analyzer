#!/usr/bin/env bash

set -euxf -o pipefail

PRODUCT_NAME=spring-startup-analyzer
LAST_TAG=${1:-v2.0.6}
PROFILER_HOME=${HOME}/spring-startup-analyzer

check_permission() {
  if [ ! -w "${HOME}" ]; then
    echo "permission denied, ${HOME} is not writable." && exit 1
  fi
}

download_jar() {

  if [ ! -d "${PROFILER_HOME}" ]; then
    mkdir -p "${PROFILER_HOME}"
  fi

  download_url="https://github.com/linyimin0812/${PRODUCT_NAME}/releases/download/${LAST_TAG}/${PRODUCT_NAME}.tar.gz"

  echo "Download spring-startup-analyzer from: ${download_url}"

  curl -#Lkf \
      -o "${PROFILER_HOME}/${PRODUCT_NAME}.tar.gz" \
      "${download_url}" \
    || (echo "Download spring-startup-analyzer from: ${download_url} error, please install manually!!!" && exit 1)

}

extract_jar() {
  if [ ! -f "${PROFILER_HOME}/${PRODUCT_NAME}.tar.gz" ]; then
    echo "${PROFILER_HOME}/${PRODUCT_NAME}.tar.gz does not exist, please install manually!!!" && exit 1
  fi

  cd "${PROFILER_HOME}" || (echo "cd ${PROFILER_HOME} error." && exit 1)

  tar -zxvf "${PROFILER_HOME}/${PRODUCT_NAME}.tar.gz"

}

main() {

  check_permission

  download_jar

  extract_jar

  rm -rf "${PROFILER_HOME}/${PRODUCT_NAME}.tar.gz"

  echo "${PRODUCT_NAME} install success. install directory: ${PROFILER_HOME}"

}

main
