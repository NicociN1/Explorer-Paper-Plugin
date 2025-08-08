BUILD_FILE=Explorer-Paper-1.0-SNAPSHOT-all.jar
SRC_DIR=/Users/taiki/Devs/Minecraft/Explorer-Paper-Plugin/
BUILD_DIR=$SRC_DIR/build/libs
PLUGINS_DIR=/Users/taiki/Devs/MC-Servers/Explorer-Paper/plugins

cd $SRC_DIR
echo "Start building..."
./gradlew build
echo "Done."
cp $BUILD_DIR/$BUILD_FILE $PLUGINS_DIR
echo "Copy success!"