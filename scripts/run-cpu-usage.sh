# Sample shell script to start the cpu usage sample running.
# Alternatively this could be specified as a service

# Variables to be specified:
# - Location of SDSF libraries (normally /usr/lpp/sdsf/java/lib_64) 
SDSF=/usr/lpp/sdsf/java/lib_64

# - Port number to run this from
PORT={available_port_number}

# - Location of your zowe installation
ZOWE_LOCATION={folder}/zowe/1.0.0

DIR=`dirname $0`

java -Djava.library.path=.:$SDSF -Xms16m -Xmx512m \
    -Dserver.port=$PORT \
    -Dserver.ssl.enabled=true -Dserver.scheme=https \
    -Dserver.ssl.keyAlias=localhost \
    -Dserver.ssl.keyStore=$ZOWE_LOCATION/api-mediation/keystore/localhost/localhost.keystore.p12 \
    -Dserver.ssl.keyStorePassword=password \
    -Dserver.ssl.keyStoreType=PKCS12 \
	-Dserver.serviceId=usage -Dserver.serviceTitle="z/OS System Information" -Dserver.serviceDescription="API's for collecting z/OS CPU Usage Information" \
    -jar $DIR/cpu-1.0.0.jar 