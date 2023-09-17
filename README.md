# Filmorate

## _Description_

>Typical social network created to simplify choosing film for evening.<br>
Project allows for users to add friends, to like or dislike films, 
to find film by it rating, release year, category or name.

## _Used HTTP methods_

| Method | Description         |
|--------|---------------------|
| GET    | Get film or user    |
| POST   | Post film or user   |
| UPDATE | Update film or user |
| DELETE | Delete film or user |

## _Possible HTTP response codes_

| Code | Description                    |
|------|--------------------------------|
| 200  | OK                             |
| 201  | Successfully posted or updates |
| 400  | Bad request                    |
| 404  | Not found                      |
| 500  | Internal server error          |

## _End points_

> Films controller

| Method | URL                        | Description                  | Params                  |
|--------|----------------------------|------------------------------|-------------------------|
| GET    | /films                     | Returns all film             | N/A                     |
|        | /films/{id}                | Returns film with id         | id > 0                  |
|        | /films/popular?count=count | Returns @count popular films | count > 0 by default 10 |
| POST   | /films                     | Create film                  | Requires json body      |
| 500    | /films                     | Internal server error        |                         |

> Users controller

| Method | URL | Description                    | Params |
|--------|-----|--------------------------------|--------|
| GET    |     | OK                             |        |
| 201    |     | Successfully posted or updates |        |
| 400    |     | Bad request                    |        |
| 404    |     | Not found                      |        |
| 500    |     | Internal server error          |        |

## _Project structure_

>Used Spring boot 2.14.0<br>
> Java version 11

## _Database architecture_

### Basics

> Used PostgreSQL version 16.0<br>
> Normalized to 3NF

### ER diagram

![Filmorate ER diagram.jpg](src%2Fmain%2Fresources%2FFilmorate%20ER%20diagram.jpg)

### SQL request examples

> Allowed all standard SQL methods used for PostgreSQL.<br>
> LEFT JOIN is preferred method to join two or more tables.

> Get first 15 films released in 2015 with it name, description and rating
>```postgres-psql
>SELECT
>    name,
>    description,
>    rating
>FROM films
>WHERE
>    EXTRACT(YEAR FROM release_date) = '2015'
>LIMIT 15;
>```

