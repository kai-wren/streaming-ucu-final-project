from kafka import KafkaConsumer, KafkaProducer
from json import loads, dumps
import pandas as pd
import pickle

consumer = KafkaConsumer('json-test1', auto_offset_reset='earliest',
                             bootstrap_servers=['localhost:9092'],
                         consumer_timeout_ms=1000, value_deserializer=lambda x: loads(x.decode('utf-8')))

producer = KafkaProducer(bootstrap_servers=['localhost:9092'],
                         key_serializer=lambda x: bytes(str(x), encoding='utf-8'),
                         value_serializer=lambda x: dumps(x).encode('utf-8') )

filename = './aqi_prediction_model.pickle'
with open(filename, 'rb') as file:
  model = pickle.load(file)

for message in consumer:
    data=[]
    values = message.value
    values['city'] = message.key
    print(message.key)
    data.append(values)
    df = pd.DataFrame(data)
    predicted = model.predict(df[['temp', 'wind']])
    data = {"Predicted": predicted[0], "True": df.aqi[0]}
    producer.send('aqi-predict', value=data, key=message.key)
