import psycopg2
import pandas as pd
from datetime import datetime
import os

output_dir = r"C:\Users\iesaf\OneDrive\Documents\student"
os.makedirs(output_dir, exist_ok=True)
output_file = os.path.join(output_dir, f"student_dataset_{datetime.now():%Y%m%d_%H%M%S}.csv")

conn = psycopg2.connect(
    host="localhost",
    database="bd_colegios",
    user="postgres",
    password="tu_contraseña"
)

with conn.cursor() as cur:
    cur.execute("REFRESH MATERIALIZED VIEW ml.students_dataset_mv;")
    conn.commit()

df = pd.read_sql("SELECT * FROM ml.students_dataset_mv;", conn)
df.to_csv(output_file, index=False, encoding="utf-8")

print(f"✅ Dataset exportado a: {output_file}")
conn.close()
