#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "${DIR}" || exit 99

function build {
  local BUILD_DIR=$1
  local IMAGE_NAME=$2
  local VERSION=$3

  echo "#############################################################################################################"
  echo "# build: BUILD_DIR=$BUILD_DIR, IMAGE_NAME=$IMAGE_NAME, VERSION=$VERSION"
  echo "#############################################################################################################"

  echo "# create aarch64 image"
  docker build --platform=linux/aarch64 -t "${IMAGE_NAME}:${VERSION}"-aarch64 "${BUILD_DIR}" || exit 1
  docker push "${IMAGE_NAME}:${VERSION}-aarch64" || exit 2

  echo "# create amd64 image"
  docker build --platform=linux/amd64 -t "${IMAGE_NAME}:${VERSION}-amd64" "${BUILD_DIR}" || exit 3
  docker push "${IMAGE_NAME}:${VERSION}-amd64" || exit 4

  echo "# create version image"
  docker manifest create "${IMAGE_NAME}:${VERSION}" \
    --amend "${IMAGE_NAME}:${VERSION}-amd64" \
    --amend "${IMAGE_NAME}:${VERSION}-aarch64" || exit 5
  docker manifest push "${IMAGE_NAME}:${VERSION}" || exit 6

  echo "# create latest image"
  docker manifest create "${IMAGE_NAME}:latest" \
    --amend "${IMAGE_NAME}:${VERSION}-amd64" \
    --amend "${IMAGE_NAME}:${VERSION}-aarch64" || exit 7
  docker manifest push "${IMAGE_NAME}:latest" || exit 8

  echo "#############################################################################################################"
  echo "# done"
  echo "#############################################################################################################"
}

build "./docker/kraft" "mbopm/kraft" "2.13-3.7.0"
