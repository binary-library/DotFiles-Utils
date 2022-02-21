#!/usr/bin/env bash
set -eou pipefail

source $1

# https://stackoverflow.com/a/246128
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
>&2 pushd "$SCRIPT_DIR"

# /usr/local/bin might have permissions restrictions
export MANAGED_BINS="${SCRIPT_DIR}/bin/"
>&2 mkdir -p "$MANAGED_BINS"
export PATH="${MANAGED_BINS}:${PATH}"

if ! command -v dad >&2; then
    >&2 echo "Downloading dad"
    >&2 curl -L https://raw.githubusercontent.com/liquidz/dad/master/script/download | bash 2> /dev/null
    >&2 mv dad "${MANAGED_BINS}"
fi

dad sync.clj

>&2 popd
