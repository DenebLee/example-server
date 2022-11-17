# TODO LIST

1. Inbound
    - 연결됐을 때 socket 리소스가 정리되어있어야 한다
      ~~- 스레드 작동 여부~~
      ~~- 종료 됐을 때 socket Resource 삭제~~
      ~~- socket 종류 정책 세우기 (에러 관련 정책)~~
      ~~- 클라이언트와 브로커 작성해서 테스트 코드 작성~~
2. Broker
3. Mapper
4. OutBound
5. Filter
6. Branch
7. Sender

# TODO

# DISCOVERED PROBLEM

~~ReadThread에서 readline() 구문에서 exception이 발생하면서 ReadThread는 작동을 멈춤 -> WriteThread는 ReadThread의 종료 이벤트를 받지 않아 무한 루프~~
~~Socket이 끊겨서 Client와의 연결이 종료 됐을 때 SocketManager에 있는 HashMap에 해당 socket 값 삭제 필요~~



