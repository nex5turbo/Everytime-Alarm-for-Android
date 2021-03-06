# Naz

## 결과화면
![Screenshot_20210530-165646_Gallery](https://user-images.githubusercontent.com/50979183/120099868-cdec8c00-c178-11eb-8240-e78278165e00.jpg)
![Screenshot_20210530-165655](https://user-images.githubusercontent.com/50979183/120099870-ce852280-c178-11eb-89a2-7578a610b42f.jpg)
![Screenshot_20210530-165704](https://user-images.githubusercontent.com/50979183/120099871-ce852280-c178-11eb-9b29-ffec7728f04c.jpg)
![Screenshot_20210530-170126](https://user-images.githubusercontent.com/50979183/120099872-cf1db900-c178-11eb-9c95-8d88167a62b9.jpg)
![Screenshot_20210530-165533](https://user-images.githubusercontent.com/50979183/120099873-cf1db900-c178-11eb-91c6-541d7effa6da.jpg)
![Screenshot_20210530-165542](https://user-images.githubusercontent.com/50979183/120099874-cfb64f80-c178-11eb-8749-34f96d8a6361.jpg)

## OpenCV
프로젝트 개요는 이렇다.    
대학생들에게는 거의 필수적인 모바일앱인 '에브리타임'의 시간표를 분석해서    
자동으로 첫 수업전에 알람을 맞춰주는 것이다.    
    
먼저 시간표 분석을 위해 OpenCV를 사용했다. 기본적으로 이미지나 영상처리를 위해    
많이 사용되는 라이브러리인데, 따로 대체할만한 라이브러리를 찾지 못했다.    
    
OpenCV는 C++, C, Python등에서 주로 사용되는데, 안드로이드에서 사용할 경우 여러가지 환경설정등을 해주어야한다.    
**(환경설정하는데만 2개월 소모....)**    
    
사진 분석은 CPP를 사용했다. HoughLine 변환을 통해 시간표내에 있는 선들을 전부 인식하여 구간을 나누고,    
구간들의 배경색을 비교한다. 배경색이 하얀 구간은 수업이 없는 시간이고,    
다른 색으로 칠해진 구간은 수업이 있는 시간이라는 뜻이다. 이것을 이용해, 각 요일마다의    
첫번째 수업시간을 확인한다. 이렇게 분석한 결과를 intArray에 담아 각 요일의 첫 수업이 시작되는 시간을    
Kotlin으로 보내준다. (수업이 없는 날은 -1, 있는 날은 9시부터 1,2,3,4.... 이런식)    
이렇게 되면 OpenCV의 역할은 끝이다. 더 필요없다.    
    
## Alarm
이 프로젝트를 하면서 가장 많이 공부한 부분이다. 알람을 설정하려면 기본적으로    
**BroadCast Receiver** 컴포넌트를 활용해야 한다.    
AlarmReceiver 클래스를 하나 생성해서 BroadcastReceiver 클래스를 상속받고 해당 리시버의    
트리거가 잡혔을 때 할 일들을 나열해주면 된다.    
    
막연히 알람이 울리는 기능이 필요해서 알람을 울리는 방법만 검색하고 구현했었는데,    
이 Broadcast Receiver, Pending Intent 등에 대한 이해가 없으면 원하는 대로 커스터마이징을    
할 수 없다는 것을 깨달았다. 그 뒤로 며칠동안 구현에 대한 고민을 잠시 접어두고, 이 컴포넌트들이    
어떤 방식으로 작동하는지에 대한 공부를 했다.    
    
## Retrofit
이전에 **MovieRecommendApp** 을 만들면서 OkHttp3가 아닌 Retrofit을 사용해보기로 했었다.    
그러다 이 앱에서 날씨정보를 받아오면 어떨까 해서 간단하게 AWS Lambda서버를 만들고 Retrofit으로     
오늘의 날씨 정보를 받아오는 기능을 구현해놨다.    
    
이 프로젝트를 하기 전에 Riot API(**롤**)를 활용한 간단한 앱을 만들었었는데, 거기서 처음으로 Retrofit을    
적용하면서 어떤 방식으로 사용하는건지 대략적인 방법을 익혀두긴 했다.    
    
다른 객체들과는 다르게 context정보가 필요가 없었다. 그러다보니 싱글톤 객체를 생성해놔도 NullPointerException이나    
메모리 낭비같은 문제는 발생하지 않을 것 같다...!    
    
많은 데이터 정보를 활용하기 위해 API를 사용하는 것이 아니다보니 data class에 자료형들도 간단하게만 작성을 해두었다.    
비가 오거나, 날씨가 맑거나, 눈이 오는 상황에 대해서만 조건을 걸어두었다. **OpenWeatherMap API를 사용해서 서버에 넣어놨다.**    
    
확실히 OkHttp3에서는 데이터를 받아온 뒤, Json파싱하는 부분이 굉장히 복잡했었는데, Retrofit을 사용하니 기본적으로 틀을    
만들어놓고 사용하는게 많이 편리한 것 같다.    
    
## SQLite
설정에 관한 부분에 대해서도 생각이 많았다.    
일단 알람이 설정되면 기본적으로 요일마다의 시간과, 알람이 켜져있는지 꺼져있는지,    
수업시작 얼마전에 알람이 울릴건지, 알람음은 무엇으로 할건지 등등의 정보를 저장해야한다.    
    
처음에는 이러한 정보들을 데이터베이스에 저장해서 사용하려고 했다.    
하지만, 사용하는 데이터양이 끽해야 20row도 안되는데 이것을 위해 SQLite를 사용하는 것부터    
마음이 불편했다. 심지어 알람음의 경우는 비슷한 카테고리도 없기 때문에 이 설정 하나 때문에    
테이블을 하나 생성해야하는게 꽤나 심한 낭비라고 생각이 들었다.    
    
## Preferences
그러던 중 간단하게 설정등을 저장하는 방법으로 텍스트 파일에 데이터를 저장하는 **Preferences** 라는 것을 발견했다.    
    
Preferences를 사용해 알람음, 수업시작 얼마전에 알람이 울릴지 정하는 시간을 저장하기로 했다.    
또 이것을 이용하면 이 앱이 사용자의 핸드폰에서 처음 실행된건지 아닌지에 대해 아는 것도 가능했기에    
사용하지 않을 수가 없었다. 또 Preferences를 활용한 Fragment까지 제공하니 설정창에서는 개발하는    
입장에서 너무 간편하게 사용할 수 있는게 큰 장점인 것 같다.     
    
기존에 요일별 알람 시간과 알람 On Off 여부를 저장하는것은 SQLite로 만들어 놓은 것도 있고 생각보다    
옮기는 과정이 번거로워 놔뒀다.     
    
여기까지 구현을 마치고 나니 필요한 기능들의 구현이 마무리됐다.    
    
## Finish
처음에는 단순히 스토어에 등록할 앱을 간단하게 만들어보기만 하려고 시작했는데,    
잘 알고 있다고 생각했던 네이티브의 특성들이 너무 어렵게 다가왔다.    
    
안드로이드 4대 컴포넌트 Broadcast Receiver, Activity, ContentProvider, Service...    
아직도 공부할 게 너무나도 많다.. 그래도 이 프로젝트로 최소한 Broadcast Receiver는    
굉장히 많은 것을 알게되었다. 앞으로 receiver가 필요한 앱을 제작할 때 정말 많은 도움이 될 것 같다.    
    
이론적으로만 공부했던 액티비티 생명 주기 역시, 일일이 로그를 찍어보며 create, destroy, stop, pause 등등의    
시점이 언제 찍히는지도 알아보았다. 내가 알고 있던것과는 많이 다르게 찍히는 것을 보며    
**나는 아직 멀었구나...** 라는 생각...도 ㅋㅋ..    
