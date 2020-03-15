from kafka import KafkaConsumer, KafkaProducer
from json import loads, dumps
import pandas as pd
import pickle

consumer = KafkaConsumer('aqi-weather-joined', auto_offset_reset='earliest',
                             bootstrap_servers=['10.0.1.171:9092','10.0.0.154:9092','10.0.2.72:9092'],
                             value_deserializer=lambda x: loads(x.decode('utf-8')))

producer = KafkaProducer(bootstrap_servers=['10.0.1.171:9092','10.0.0.154:9092','10.0.2.72:9092'],
                         key_serializer=lambda x: bytes(str(x), encoding='utf-8'),
                         value_serializer=lambda x: dumps(x).encode('utf-8') )

filename = './aqi_prediction_model.pickle'
with open(filename, 'rb') as file:
  model = pickle.load(file)

for message in consumer:
    data=[]
    values = message.value
    data.append(values)
    df = pd.DataFrame(data)
    predicted = model.predict(df[['temp', 'pressure', 'humidity', 'windSpeed', 'windDeg']])
    data = {"City": df.city[0], "Predicted": predicted[0], "True": df.aqi[0]}
    producer.send('aqi-predict', value=data, key=message.key)
