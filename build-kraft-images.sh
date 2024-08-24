#!/usr/bin/env bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "${DIR}" || exit 99

KAFKA_VERSION="2.13-3.8.0"
IMAGE_NAME="mbopm/kraft"

function build {
  local BUILD_DIR=$1
  local IMAGE_NAME=$2
  local VERSION=$3

  echo "#############################################################################################################"
  echo "# build: BUILD_DIR=$BUILD_DIR, IMAGE_NAME=$IMAGE_NAME, VERSION=$VERSION"
  echo "#############################################################################################################"

  # Create a multi-platform image using buildx
  docker buildx create --use --name mybuilder --platform linux/amd64,linux/arm64 --driver docker-container || exit 1

  echo "# Build and push multi-platform image"
  docker buildx build --platform linux/amd64,linux/arm64 -t "${IMAGE_NAME}:${VERSION}" -t "${IMAGE_NAME}:latest" --push "${BUILD_DIR}" || exit 2

  echo "#############################################################################################################"
  echo "# done"
  echo "#############################################################################################################"

  # Clean up builder instance after use
  docker buildx rm mybuilder
}

build "./docker/kraft" "$IMAGE_NAME" "$KAFKA_VERSION"
