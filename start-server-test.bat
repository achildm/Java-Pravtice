@echo off
echo 启动测试服务器...
echo 请等待服务器启动完成后再启动客户端
echo.
java -cp target/classes com.achldm.chess.ChessGameMain server
pause