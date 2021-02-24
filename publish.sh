#!/bin/sh
if [ -z $1 ]
then
  echo "Must provide publisher user name"
  exit 1
fi
export GITHUB_PUBLISH_USERNAME=$1

if [ -z $2 ]
then
  echo "Must provide user token for publishing"
  exit 1
fi
export GITHUB_PUBLISH_TOKEN=$2

if [ -z $3 ]
then
  export VERSION_SUFFIX=""
else
  export VERSION_SUFFIX="$2-$(date +%Y%m%d)"
fi

echo "About tu publish with the following version suffix: $VERSION_SUFFIX."
while true; do
  read -p "Is this the desired behavior? [y/n] " yn
  case $yn in
    [yY]* ) break;;
    [nN]* ) exit 1;;
    * ) echo "Choose y/n";;
  esac
done

./gradlew :crane:publish :crane-router:publish
