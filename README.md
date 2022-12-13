# TODO LIST

1. Inbound
   ~~- 연결됐을 때 socket 리소스가 정리되어있어야 한다~~<br/>
   ~~- 스레드 작동 여부~~<br/>
   ~~- 종료 됐을 때 socket Resource 삭제~~<br/>
   ~~- socket 종류 정책 세우기 (에러 관련 정책)~~<br/>
   ~~- 클라이언트와 브로커 작성해서 테스트 코드 작성~~<br/>
   ~~- 스레드풀로 스레드 관리법 공부 및 테스트 코드 작성~~<br/>

2. Broker
   ~~- broker queue에 publish 혹은 subscribe 실패시 에러처리~~<br/>
   ~~- socket 연결 끊긴 client에 대응하는 queue 내 리소스 정리~~<br/>
3. Mapper
4. OutBound
5. Filter
6. Branch
7. Sender

# TODO

- DB 연결
- 작성 했던 테스트코드 전체 리팩토링 및 캡슐화가 안된 클래스들 싹다 수정
- 테스트 코드 구현중 (SocketResource, SocketManager, BrokerImpl,ModuleProcessManagerImpl => 완료)
- 모듈들 테스트 코드 구현중(Mapper, Branch => 완료)

## COMMIT

| DATE         | content                                                                                                        |
|--------------|----------------------------------------------------------------------------------------------------------------|
| [2022-11-17] | 인바운드 되는 비즈니스 로직에 대한 보안 로직 추가 , SocketResource에서 ReadThread, WriteThread에 대한 핸들링 메소드 추가 및 socket 리소스 정리         |                                                                                                            |                                                                                                     |
| [2022-11-18] | SocketManager Test Code 작성중 , ThreadPool 관련 스킬 습득중, Thread pool 테스트 코드 작성                                      |                                                                                                            |                                                                                                     |
| [2022-11-23] | ThreadManager Test 코드 구현중, 병렬 Thread 상태 제어 구현이 너무 어려워 공부중                                                      |                                                                                                            |                                                                                                     |
| [2022-11-26] | ThreadManager 기능 구현 완료                                                                                         |                                                                                                            |                                                                                                     |
| [2022-11-05] | Carrier 기능 추가 (가상 통신사 모듈)                                                                                      |                                                                                                            |                                                                                                     |
| [2022-12-06] | Sender -> Carrier -> OutBound 통신 테스트 완료 , Carrier 와 통신하는 Socket 에러 구현중, 기능에 따른 프로젝트 리팩토링 , Error Handling 시도 중 |                                                                                                            |                                                                                                     |
| [2022-12-07] | 캡슐화 지키지 않은 class들 정리 및 수정 , UnitTest 전부 수정 , ModuleProcessManagerImpl 기능 추가                                    |                                                                                                            |                                                                                                     |
| [2022-12-08] | ModuleProcessManagerImpl UnitTest 구현 완료 , SocketManager UnitTest 재수정                                           |                                                                                                            |                                                                                                     |
| [2022-12-09] | SocketManagerTest 구현 완료 , 각 모듈별 테스트코드 전면적으로 수정중                                                                |                                                                                                            |                                                                                                     |
| [2022-12-13] | Mapper, Branch 테스트 코드 작성 완료 , Filter 리팩토링중(유효성 검사 로직 추가)                                                       |                                                                                                            |                                                                                                     |
