# My Database Management System(ThienDB)

## Overview

This project involves the creation of a custom Database Management System (DBMS) using Java and the Spring Framework.

## Getting Started

To run the program, Docker Compose is utilized:

`docker compose up -d`

## Các Câu Lệnh SQL Mẫu

Dưới đây là một số câu lệnh SQL mẫu mà bạn có thể sử dụng để thao tác với cơ sở dữ liệu trong ThienDB:

### API `/api/parse-sql`

#### Tạo Cơ Sở Dữ Liệu
Để tạo một cơ sở dữ liệu mới, sử dụng câu lệnh sau:
*** CREATE DATABASE TenCSDL; ***

### API `/api/parse-sql/{TenCSDL}`

#### Tạo Chỉ Mục
Để tạo chỉ mục cho một cột trong bảng, sử dụng câu lệnh sau:
> ***CREATE INDEX idx_lastname ON students (name);***

#### Tạo Bảng
Để tạo một bảng mới, sử dụng câu lệnh sau:

> ***CREATE TABLE Persons ( PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255), City varchar(255) );***

#### Chèn Dữ Liệu
Để chèn dữ liệu vào bảng, sử dụng câu lệnh sau:
> ***INSERT INTO students (id, name, age) VALUES (5, ‘Stavanger’, 60);***

#### Cập Nhật Dữ Liệu
Để cập nhật dữ liệu trong bảng, sử dụng câu lệnh sau:

> ***UPDATE students SET name=‘Juan’ WHERE id = 4;***

#### Xóa Bảng
Để xóa một bảng, sử dụng câu lệnh sau:
> ***DROP TABLE TenBang;***


## Hãy thay thế `TenCSDL` và `TenBang` bằng tên cơ sở dữ liệu và tên bảng mà bạn muốn sử dụng.




