from kafka import KafkaConsumer
from json import loads
import pandas as pd
from sklearn.linear_model import LinearRegression
import pickle

consumer = KafkaConsumer('json', auto_offset_reset='earliest',
                             bootstrap_servers=['localhost:9092'], 
                         consumer_timeout_ms=1000, value_deserializer=lambda x: loads(x.decode('utf-8')))

data = []

for message in consumer:
    values = message.value
    values['city'] = message.key
    data.append(values)
    
df = pd.DataFrame(data)

model = LinearRegression().fit(df[['temp', 'wind']], df.aqi)

pickle_out = open('./aqi_prediction_model.pickle', 'wb')
pickle.dump(model, pickle_out)



