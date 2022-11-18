# TODO LIST

1. Inbound
    - 연결됐을 때 socket 리소스가 정리되어있어야 한다<br/>
      ~~- 스레드 작동 여부~~<br/>
      ~~- 종료 됐을 때 socket Resource 삭제~~<br/>
      ~~- socket 종류 정책 세우기 (에러 관련 정책)~~<br/>
      ~~- 클라이언트와 브로커 작성해서 테스트 코드 작성~~<br/>
    - 스레드풀로 스레드 관리법 공부 및 테스트 코드 작성
    -
2. Broker
    - broker queue에 publish 혹은 subscribe 실패시 에러처리
    - socket 연결 끊긴 client에 대응하는 queue 내 리소스 정리

3. Mapper
4. OutBound
5. Filter
6. Branch
7. Sender

# TODO

# DISCOVERED PROBLEM

~~ReadThread에서 readline() 구문에서 exception이 발생하면서 ReadThread는 작동을 멈춤 -> WriteThread는 ReadThread의 종료 이벤트를 받지 않아 무한
루프~~<br/>
~~Socket이 끊겨서 Client와의 연결이 종료 됐을 때 SocketManager에 있는 HashMap에 해당 socket 값 삭제 필요~~

## COMMIT

| DATE         | content                                                                                                |
|--------------|--------------------------------------------------------------------------------------------------------|
| [2022-11-17] | 인바운드 되는 비즈니스 로직에 대한 보안 로직 추가 , SocketResource에서 ReadThread, WriteThread에 대한 핸들링 메소드 추가 및 socket 리소스 정리 |                                                                                                            |                                                                                                     |
| [2022-11-18] | SocketManager Test Code 작성중 , ThreadPool 관련 스킬 습득중, Thread pool 테스트 코드 작성                              |                                                                                                            |                                                                                                     |
