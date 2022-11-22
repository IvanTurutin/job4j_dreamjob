SELECT p.id as id,
p.name as name,
p.description as description,
p.date as date,
p.visible as visible,
p.city_id as city_id,
c.name as city_name
FROM posts as p JOIN cities as c ON p.city_id = c.id;