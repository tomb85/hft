#!/usr/bin/env bash

VERSION=$1
BUILD_DIR=$2
ARTIFACT=$VERSION-dist.zip

echo "Cleaning previous versions"
ssh boltinst@boltd01 "
pid=\$(ps ux | grep arb-scanner | grep -v grep | awk '{print \$2}')
echo \$pid
if [ ! -z \$pid ]; then kill -9 \$pid; fi
cd /apps/bolt
rm -rf *"

echo "Deploying $ARTIFACT"
scp $BUILD_DIR/$ARTIFACT boltinst@boltd01:/apps/bolt/

echo "Starting environment"
ssh boltinst@boltd01 "
cd /apps/bolt
unzip $ARTIFACT
cd $VERSION
./bin/app_ctrl.sh"


