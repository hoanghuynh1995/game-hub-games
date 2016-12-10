# game-hub-games
## Vì project game phát triển ngoài flow của chương trình nên phải sử dụng server riêng (ít thao tác xử lý hơn server chính).
## Chỉ sử dụng class SimpleEntry, AndroidLauncher và các class ở package pong
## Ở client, muốn vào game phải ấn button "start" tương ứng với button start bắt đầu game bên flow chương trình
## Để test, dùng 1 máy ảo và cắm cable vào máy thật
## Để chạy: 
* Chạy command line "ipconfig" để xem địa chỉ IP4
* Vào class AndroidLauncher chỉnh lại địa chỉ IP cho đúng phần khởi tạo socket
* Qua server chạy file index.js "node index"
* Chạy chương trình ở cả máy ảo và máy thật
* Khi cả 2 đã vào chương trình, 1 trong 2 ấn "Start" để cả 2 vào game, khi vào game, chạm 1 lần nữa vào màn hình để sẵn sàng, khi cả 2 sẵn sàng thì game bắt đầu chạy
* Khi cả 2 đã thoát game, thì có thể bắt đầu vào lại mà không cần restart server. Tránh trường hợp 1 máy out rồi vào lại trong khi máy kia chưa out vì server này chỉ tạo 1 room để test, sẽ xảy ra lỗi 

## Thắc mắc gì liên hệ Huynh Phan để hỏi
 
