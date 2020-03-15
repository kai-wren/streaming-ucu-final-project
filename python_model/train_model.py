from kafka import KafkaConsumer
from json import loads
import pandas as pd
from sklearn.linear_model import LinearRegression
import pickle


consumer = KafkaConsumer('aqi-weather-joined', auto_offset_reset='earliest',
                                     bootstrap_servers=['10.0.1.171:9092','10.0.0.154:9092','10.0.2.72:9092'], 
                                     enable_auto_commit=True,
                                     value_deserializer=lambda x: loads(x.decode('utf-8')))
        
data = []
count = 0

for message in consumer:
    count += 1
    if count > 120:
        consumer.close()
        break
    values = message.value
    data.append(values)
        
            
df = pd.DataFrame(data)
        
model = LinearRegression().fit(df[['temp', 'pressure', 'humidity', 'windSpeed', 'windDeg']], df.aqi)
        
pickle_out = open('./aqi_prediction_model.pickle', 'wb')
pickle.dump(model, pickle_out)
