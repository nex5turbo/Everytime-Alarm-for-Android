//
// Created by nex5t on 2021-05-07.
//

#include <jni.h>
#include <string>
#include "com_example_myapplication2_OpenCvModule.h"

#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

extern "C" {
JNIEXPORT jintArray JNICALL Java_com_example_myapplication2_OpenCvModule_ConvertRGBtoGray
        (JNIEnv *env, jobject instance, jlong mat_addr_input, jlong mat_addr_result){
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;
    int width = matInput.size().width;
    int height = matInput.size().height;
    Mat org = matInput;
    Mat cannyImg;
    Mat grayImg;
    Mat edges;
    cvtColor(matInput, grayImg, COLOR_BGR2GRAY);
    Canny(grayImg, edges, 50,50,3);

    vector<Vec2f> lines;
    vector<int> x_list, y_list;
    HoughLines(edges, lines, 1, CV_PI/180, 200);

    if(lines.empty()){
        return nullptr;
    }

    for(size_t j = 0; j < lines.size();j++){
        float rho = lines[j][0];
        float theta = lines[j][1];
        double a = cos(theta),b=sin(theta);
        double x0 = a*rho, y0=b*rho;
        int x1 = cvRound(x0+ width*(-b));
        int y1 = cvRound(y0 + height*(a));
        int x2 = cvRound(x0 - width*(-b));
        int y2 = cvRound(y0 - height*(a));

        if((x1+x2)/2 > 0){
            if(abs(x1-x2) > 10) continue;
            x_list.push_back((x1+x2)/2);
        }
        if((y1+y2)> 0){
            if(abs(y1-y2) > 10) continue;
            y_list.push_back((y1+y2)/2);
        }

        line(matInput, Point(x1,y1), Point(x2,y2), Scalar(0,0,255), 1);
    }

    if(x_list.size() == 0 || y_list.size() == 0) return NULL;

    sort(x_list.begin(), x_list.end());
    sort(y_list.begin(), y_list.end());

    matResult = matInput;
    int tagX_gap = x_list[0];
    int tagY_gap = y_list[0];
    for(size_t i = 0; i < x_list.size();i++){
        if(x_list[i] > 10){
            tagX_gap = x_list[i];
            break;
        }
    }
    for(size_t i = 0; i < y_list.size();i++){
        if(y_list[i] > 10){
            tagY_gap = y_list[i];
            break;
        }
    }

    int x_gap = (width-x_list[x_list.size()-1]);
    int y_gap = (height-y_list[y_list.size()-1])/2;

    for(size_t i = 0; i< x_list.size();i++){
        if(width-x_list[x_list.size()-i-1]>10){
            x_gap = width-x_list[x_list.size()-i-1];
            break;
        }
    }
    for(size_t i = 0; i< y_list.size();i++){
        if((height-y_list[y_list.size()-i-1])/2>10){
            y_gap = (height-y_list[y_list.size()-i-1])/2;
            break;
        }
    }

    if(tagX_gap > 50 || tagY_gap > 50){
        return nullptr;
    }

    vector<Mat> monday;
    vector<Mat> tuesday;
    vector<Mat> wednesday;
    vector<Mat> thursday;
    vector<Mat> friday;
    int now_height = tagY_gap;

    while(true){
        if(now_height > height){
            break;
        }
        Rect rect(tagX_gap, now_height, x_gap, y_gap);
        Rect bounds(0,0,org.cols,org.rows);
        Mat img_trim = org(rect&bounds);
        monday.push_back(img_trim);
        now_height = now_height+y_gap;
    }
    now_height = tagY_gap;
    while(true){
        if(now_height > height){
            break;
        }
        Rect rect(tagX_gap+x_gap, now_height, x_gap, y_gap);
        Rect bounds(0,0,org.cols,org.rows);
        Mat img_trim = org(rect&bounds);
        tuesday.push_back(img_trim);
        now_height = now_height+y_gap;
    }
    now_height = tagY_gap;
    while(true){
        if(now_height > height){
            break;
        }
        Rect rect(tagX_gap+x_gap+x_gap, now_height, x_gap, y_gap);
        Rect bounds(0,0,org.cols,org.rows);
        Mat img_trim = org(rect&bounds);
        wednesday.push_back(img_trim);
        now_height = now_height+y_gap;
    }
    now_height = tagY_gap;
    while(true){
        if(now_height > height){
            break;
        }
        Rect rect(tagX_gap+x_gap+x_gap+x_gap, now_height, x_gap, y_gap);
        Rect bounds(0,0,org.cols,org.rows);
        Mat img_trim = org(rect&bounds);
        thursday.push_back(img_trim);
        now_height = now_height+y_gap;
    }
    now_height = tagY_gap;
    while(true){
        if(now_height > height){
            break;
        }
        Rect rect(tagX_gap+x_gap+x_gap+x_gap+x_gap, now_height, x_gap, y_gap);
        Rect bounds(0,0,org.cols,org.rows);
        Mat img_trim = org(rect&bounds);
        friday.push_back(img_trim);
        now_height = now_height+y_gap;
    }

    vector<int> dayList;

    //월요일
    for(size_t i = 0; i< monday.size();i++){
        Mat curMat = monday[i];
        Mat curHSV;
        cvtColor(curMat, curHSV, COLOR_BGR2HSV);
        int mean_sat = 0;
        for (int y = 0; y<curHSV.size().height;y++){
            for(int x=0;x<curHSV.size().width;x++){
                mean_sat+=(int)curHSV.at<Vec3b>(y,x)[1];
            }
        }
        if(mean_sat/(curHSV.size().height * curHSV.size().width) > 60){
            dayList.push_back(1);
        }else{
            dayList.push_back(0);
        }
    }
    //화요일
    for(size_t i = 0; i< tuesday.size();i++){
        Mat curMat = tuesday[i];
        Mat curHSV;
        cvtColor(curMat, curHSV, COLOR_BGR2HSV);
        int mean_sat = 0;
        for (int y = 0; y<curHSV.size().height;y++){
            for(int x=0;x<curHSV.size().width;x++){
                mean_sat+=(int)curHSV.at<Vec3b>(y,x)[1];
            }
        }
        if(mean_sat/(curHSV.size().height * curHSV.size().width) > 60){
            dayList.push_back(1);
        }else{
            dayList.push_back(0);
        }
    }
    //수요일
    for(size_t i = 0; i< wednesday.size();i++){
        Mat curMat = wednesday[i];
        Mat curHSV;
        cvtColor(curMat, curHSV, COLOR_BGR2HSV);
        int mean_sat = 0;
        for (int y = 0; y<curHSV.size().height;y++){
            for(int x=0;x<curHSV.size().width;x++){
                mean_sat+=(int)curHSV.at<Vec3b>(y,x)[1];
            }
        }
        if(mean_sat/(curHSV.size().height * curHSV.size().width) > 60){
            dayList.push_back(1);
        }else{
            dayList.push_back(0);
        }
    }
    //목요일
    for(size_t i = 0; i< thursday.size();i++){
        Mat curMat = thursday[i];
        Mat curHSV;
        cvtColor(curMat, curHSV, COLOR_BGR2HSV);
        int mean_sat = 0;
        for (int y = 0; y<curHSV.size().height;y++){
            for(int x=0;x<curHSV.size().width;x++){
                mean_sat+=(int)curHSV.at<Vec3b>(y,x)[1];
            }
        }
        if(mean_sat/(curHSV.size().height * curHSV.size().width) > 60){
            dayList.push_back(1);
        }else{
            dayList.push_back(0);
        }
    }
    //금요일
    for(size_t i = 0; i< friday.size();i++){
        Mat curMat = friday[i];
        Mat curHSV;
        cvtColor(curMat, curHSV, COLOR_BGR2HSV);
        int mean_sat = 0;
        for (int y = 0; y<curHSV.size().height;y++){
            for(int x=0;x<curHSV.size().width;x++){
                mean_sat+=(int)curHSV.at<Vec3b>(y,x)[1];
            }
        }
        if(mean_sat/(curHSV.size().height * curHSV.size().width) > 60){
            dayList.push_back(1);
        }else{
            dayList.push_back(0);
        }
    }
    jintArray result;
    result = env->NewIntArray(dayList.size());
    if(result == nullptr){
        return nullptr;
    }
    jint ji_array[dayList.size()];
    for(size_t i = 0; i < dayList.size(); i++){
        ji_array[i] = dayList[i];
    }
    env->SetIntArrayRegion(result, 0, dayList.size(), ji_array);
    return result;
}

}