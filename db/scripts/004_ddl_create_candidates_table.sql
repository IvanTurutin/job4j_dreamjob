CREATE TABLE candidates (
   id SERIAL PRIMARY KEY,
   name TEXT,
   description TEXT,
   date timestamp,
   visible boolean,
   city_id int references cities(id)
);