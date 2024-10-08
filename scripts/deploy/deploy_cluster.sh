#!/bin/bash
# Copyright 2022 The Databend Authors.
# SPDX-License-Identifier: Apache-2.0.

set -e

echo "*************************************"
echo "* Setting STORAGE_TYPE to S3.       *"
echo "*                                   *"
echo "* Please make sure that S3 backend  *"
echo "* is ready, and configured properly.*"
echo "*************************************"
export STORAGE_TYPE=s3
export STORAGE_S3_BUCKET=testbucket
export STORAGE_S3_ROOT=admin
export STORAGE_S3_ENDPOINT_URL=http://127.0.0.1:9900
export STORAGE_S3_ACCESS_KEY_ID=minioadmin
export STORAGE_S3_SECRET_ACCESS_KEY=minioadmin
export STORAGE_ALLOW_INSECURE=true

SCRIPT_PATH="$(cd "$(dirname "$0")" >/dev/null 2>&1 && pwd)"
cd "$SCRIPT_PATH/../.." || exit
BUILD_PROFILE=${BUILD_PROFILE:-debug}

# Caveat: has to kill query first.
# `query` tries to remove its liveness record from meta before shutting down.
# If meta is stopped, `query` will receive an error that hangs graceful
# shutdown.
killall databend-query || true
sleep 3

killall databend-meta || true
sleep 3

for bin in databend-query databend-meta; do
	if test -n "$(pgrep $bin)"; then
		echo "The $bin is not killed. force killing."
		killall -9 $bin || true
	fi
done

# Wait for killed process to cleanup resources
sleep 1

echo 'Start Meta service ...'

mkdir -p ./.databend/

nohup ./databend/bin/databend-meta -c scripts/deploy/config/databend-meta-node-1.toml >./.databend/meta-1.out 2>&1 &
python3 scripts/wait_tcp.py --timeout 30 --port 9191 || { echo "wait_tcp failed. Showing  meta-1.out:"; cat ./.databend/meta-1.out; exit 1; }
# wait for cluster formation to complete.
sleep 1

echo 'Start databend-query node-1'
nohup env RUST_BACKTRACE=1 ./databend/bin/databend-query -c scripts/deploy/config/databend-query-node-1.toml --internal-enable-sandbox-tenant >./.databend/query-1.out 2>&1 &

echo "Waiting on node-1..."
python3 scripts/wait_tcp.py --timeout 30 --port 9091

echo 'Start databend-query node-2'
env "RUST_BACKTRACE=1" nohup ./databend/bin/databend-query -c scripts/deploy/config/databend-query-node-2.toml --internal-enable-sandbox-tenant >./.databend/query-2.out 2>&1 &

echo "Waiting on node-2..."
python3 scripts/wait_tcp.py --timeout 30 --port 9092

echo 'Start databend-query node-3'
env "RUST_BACKTRACE=1" nohup ./databend/bin/databend-query -c scripts/deploy/config/databend-query-node-3.toml --internal-enable-sandbox-tenant >./.databend/query-3.out 2>&1 &

echo "Waiting on node-3..."
python3 scripts/wait_tcp.py --timeout 30 --port 9093

echo "All done..."
