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
**BroadCast Receiver** 컴포넌트를 활용해야 한다. 안드로이드 4대 컴포넌트로 
