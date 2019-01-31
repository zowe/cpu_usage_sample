#!/bin/sh

################################################################################
# This program and the accompanying materials are made available under the terms of the
# Eclipse Public License v2.0 which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-v20.html
#
# SPDX-License-Identifier: EPL-2.0
#
# Copyright IBM Corporation 2019
################################################################################

# Fill in the following variables
ZOWE_LOCATION=    # The root directory of the Zowe installation
ZOWE_EXPLORER_HOST=   # Host name of the Zowe installation
PORT={available_port_number}       # The address where your application is running 

echo "Configuring cpu usage behind API mediation layer" 

# Create the static api definitions folder
STATIC_DEF_CONFIG=$ZOWE_LOCATION"/api-mediation/api-defs"
TEMP_DIR=./

# Add static definition for CPU Usage
cat <<EOF >$TEMP_DIR/usage.yml
#
services:
  - serviceId: cpu
    title: CPU Usage snapshot
    description: Quick view of CPU usage
    catalogUiTileId: cpu
    instanceBaseUrls:
      - https://$ZOWE_EXPLORER_HOST:$PORT/
    homePageRelativeUrl:
    routedServices:
      - gatewayUrl: api/v1
        serviceRelativeUrl: cpu
    apiInfo:
      - apiId: com.zowe.usage
        gatewayUrl: api/v1
        version: 1.0.0
        documentationUrl: https://$ZOWE_EXPLORER_HOST:$PORT/swagger-ui.html
catalogUiTiles:
  cpu:
    title: CPU Usage snapshot
    description: Quick view of CPU usage
EOF

iconv -f IBM-1047 -t IBM-850 $TEMP_DIR/usage.yml > $STATIC_DEF_CONFIG/usage.yml	
rm $TEMP_DIR/usage.yml
chmod -R 777 $STATIC_DEF_CONFIG/usage.yml

echo "Completed cpu usage - restart zowe server to take effect" 
