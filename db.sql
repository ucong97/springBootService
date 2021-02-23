# XAMPP 최신버전 필요

# 최초

## root 접속 후 sbsst 계정 생성 및 권한 부여
## root 계정 접속정보 : root/패스워드없음
## GRANT ALL PRIVILEGES ON *.* sbsst@`%` IDENTIFIED BY 'sbs123414';
## 이후 부터는 [sbsst/sbs123414]

# 데이터 베이스 생성
DROP DATABASE IF EXISTS springBootService;
CREATE DATABASE springBootService;
USE springBootService;

# 게시물 테이블 생성
CREATE TABLE article(
    id INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    regDate DATETIME NOT NULL,
    updateDate DATETIME NOT NULL,
    title CHAR(100) NOT NULL,
    `body` TEXT NOT NULL
);

# 게시물, 테스트 데이터 생성
INSERT INTO article
SET regDate=NOW(),
    updateDate=NOW(),
    title="제목1 입니다.",
    `body`="내용1 입니다.";
    
INSERT INTO article
SET regDate=NOW(),
    updateDate=NOW(),
    title="제목2 입니다.",
    `body`="내용2 입니다.";
    
INSERT INTO article
SET regDate=NOW(),
    updateDate=NOW(),
    title="제목3 입니다.",
    `body`="내용3 입니다.";
    