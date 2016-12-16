create table users(
  id_user integer not null primary key autoincrement,
  name varchar(30) ,
  login varchar(30) unique not null,
  password char(40) not null,
  token char(40) not null
);

create table channels(
  id_channel integer not null primary key autoincrement,
  name varchar(30) not null unique,
  owner_id integer not null , foreign key (owner_id) references users(id_user)
);
