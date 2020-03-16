#!/usr/bin/env bash

set -e  # exit immediately if a command exits with a non-zero status
set -x  # print all executed commands on terminal

export $(grep -v '^#' .env | xargs)

SERVICE_NAME=$1

if [ "$SERVICE_NAME" != "aqi_streaming-app" ] && [ "$SERVICE_NAME" != "weather_streaming" ] && [ "$SERVICE_NAME" != "aqi_weather_streaming_app" ]
then
        echo "should supply name of the service [aqi_streaming-app|weather_streaming|aqi_weather_streaming_app]";
        exit 1;
fi

shift # shift arguments

ecs-cli compose \
   --cluster-config ucu-class \
   --region us-east-1 \
   --debug \
   --file staging-$SERVICE_NAME.yml \
   --project-name $STUDENT_NAME-$SERVICE_NAME \
   service "$@"
