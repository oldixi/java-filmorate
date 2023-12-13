# java-filmorate
Фильмов много — и с каждым годом становится всё больше. Чем их больше, тем больше разных оценок. 
Чем больше оценок, тем сложнее сделать выбор.
Приложение представляет собой бекэнд сервиса для работы с пользователями и фильмамы. 
Позволяет лайкать понравившиеся фильмы, получать информацию по самым популярным из них, 
добавлять пользователей в друзья и т.п.
 
## Схема базы данных для работы с пользователями и фильмами:
![Схема базы данных для проекта](schema-filmorate.png)
 
 
#### С помощью приложения в базе можно выполнять сделующие действия:

### Добавить новый фильм
``` 
insert into films(name, description, duration, mpa_code, release_date)
values('Гараж', 'На заседании гаражного кооператива предстоит выбрать четырех «крайних», которые должны сами отказаться от будущего собственного гаража.',
96, 1, '30.11.1979')
```

### Получить список друзей пользователя
 ```
 select u.* 
 from friends_link fl join users u on fl.request_user_id = u.id 
 where fl.accept_user_id = 1 
   and fl.status_code = 2
 ```

### Принять запрос в друзья 
``` 
update friends_link 
set status_code = 2 
where request_user_id = 1 
  and accept_user_id = 2
```

### Получить список 10 самых популярных фильмов
``` 
select f.* 
from films f left join (select ll.film_id, count(ll.user_id) cnt from likes_link ll group by ll.film_id) l on f.id = l.film_id 
order by l.cnt desc 
limit 10
```
