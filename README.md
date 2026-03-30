解压后，先导入数据库：
mysql -u root -p --default-character-set=utf8mb4 < flower_db_export.sql

修改 backend/src/main/resources/application.yml 中的数据库密码为本机密码

启动后端：
cd backend && mvn spring-boot:run

启动前端：
cd frontend && npm install && npm run dev