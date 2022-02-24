#!/usr/bin/env bash
set -eou pipefail

source $1
shift

# https://stackoverflow.com/a/246128
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
>&2 pushd "$SCRIPT_DIR"

# /usr/local/bin might have permissions restrictions
export MANAGED_BINS="${SCRIPT_DIR}/bin/"
>&2 mkdir -p "$MANAGED_BINS"
export PATH="${MANAGED_BINS}:${PATH}"

if ! command -v dad >&2; then
    >&2 echo "Downloading dad"
    >&2 curl -L \
        https://raw.githubusercontent.com/liquidz/dad/master/script/download \
        | bash 2> /dev/null
    >&2 mv dad "${MANAGED_BINS}"
fi
# dad sync.clj "$@"
if ! command -v nix >&2; then
    >&2 echo "Downloading "
    curl -L https://nixos.org/nix/install | bash
    >&2 popd
    exec zsh $0 -- $1 $@
fi
if ! command -v nix >&2; then
    >&2 echo "Downloading "
    nix-build https://github.com/LnL7/nix-darwin/archive/master.tar.gz -A installer
    ./result/bin/darwin-installer
    >&2 popd
    exec zsh $0 -- $1 $@
fi


nix-shell -p nix-info --run "nix-info -m"
nix-channel --add https://github.com/rycee/home-manager/archive/master.tar.gz home-manager
nix-channel --update --verbose

# export NIX_USER_CONF_FILES="${NIX_USER_CONF_FILES}:${PWD}/nix.conf"

# A general-purpose command-line fuzzy finder.
>&2 popd
